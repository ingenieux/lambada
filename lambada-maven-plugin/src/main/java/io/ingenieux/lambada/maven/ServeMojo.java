/*
 * Copyright (c) 2016 ingenieux Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ingenieux.lambada.maven;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.ingenieux.lambada.invoker.Invoker;
import io.ingenieux.lambada.invoker.UserHandlerFactory;
import io.ingenieux.lambada.runtime.ApiGateway;
import io.ingenieux.lambada.runtime.LambadaFunction;
import io.ingenieux.lambada.runtime.model.PassthoughRequest;
import io.ingenieux.lambada.testing.LambadaContext;
import spark.Request;
import spark.Response;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static spark.Spark.awaitInitialization;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.stop;
import static spark.Spark.threadPool;

@Mojo(name = "serve", requiresDirectInvocation = true, requiresProject = true,
        defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ServeMojo extends AbstractLambadaMetadataMojo {
    @Parameter(defaultValue = "${session}", required = true)
    protected MavenSession session;

    /**
     * Regex to Find Stage Parameters
     */
    public static final Pattern KEY_STAGE_VARIABLE_REGEX = Pattern.compile("^apigateway\\.stage\\.([^\\.]{3,}).(.+)$");

    @Parameter(property = "apigateway.stageName", required = true, defaultValue = "dev")
    protected String stageName;

    /**
     * Port to Use
     */
    @Parameter(property = "lambada.port", defaultValue = "8080")
    Integer serverPort;
    private final Set<PathHandler> pathHandlers = new TreeSet<PathHandler>();

    @Override
    protected void executeInternal() throws Exception {
        setupStageVariables();

        loadPathHandlers();

        port(serverPort);

        threadPool(8);

        setupServer();

        awaitInitialization();

        getLog().info(
                format("Server is alive on http://%s:%d/%s/", "127.0.0.1", this.serverPort, this.stageName)
        );

        while (10 != System.in.read()) {
            Thread.sleep(500);
        }

        stop();
    }

    Map<String, String> stageVariables;

    private void setupStageVariables() {
        Map<String, String> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        Properties properties = new Properties();

        properties.putAll(project.getProperties());
        properties.putAll(session.getUserProperties());

        properties
                .entrySet()
                .stream()
                .forEach(entry -> {
                    String key = "" + entry.getKey();
                    String value = "";

                    final Matcher matcherKeyVariable = KEY_STAGE_VARIABLE_REGEX.matcher(key);

                    if (!matcherKeyVariable.matches())
                        return;

                    if (null != entry.getValue()) {
                        value = "" + entry.getValue();
                    }

                    String stageName = matcherKeyVariable.group(1);
                    String stageProperty = matcherKeyVariable.group(2);
                    String stagePropertyValue = value;

                    if (stageName.equalsIgnoreCase(this.stageName)) {
                        getLog().info("Setting Stage Variable " + stageProperty + "=" + stagePropertyValue);
                        result.put(stageProperty, stagePropertyValue);
                    }

                });

        this.stageVariables = result;
    }

    private void setupServer() {
        String prefix = "/" + this.stageName;

        for (PathHandler p : pathHandlers) {
            if (p.getMethodType() == ApiGateway.MethodType.GET) {
                get(prefix + p.getPath(), p::handle);
            } else if (p.getMethodType() == ApiGateway.MethodType.POST) {
                post(prefix + p.getPath(), p::handle);
            }
        }
    }

    public void loadPathHandlers() throws Exception {
        final Set<Method> methodsAnnotatedWith = extractRuntimeAnnotations(LambadaFunction.class);

        getLog().info("There are " + methodsAnnotatedWith.size() + " found methods.");

        for (Method m : methodsAnnotatedWith) {
            final LambadaFunction annotation = m.getAnnotation(LambadaFunction.class);

            if (null == annotation.api() || 1 != annotation.api().length)
                return;

            ApiGateway a = annotation.api()[0];

            PathHandler pathHandler = new PathHandler();

            pathHandler.setLambadaFunction(annotation);
            pathHandler.setPath(a.path());
            pathHandler.setMethod(m);

            boolean bStatic = Modifier.isStatic(m.getModifiers());

            // TODO: ApiGateway
            getLog().info("Class: " + m.getDeclaringClass().getName());

            if (!bStatic) {
                try {
                    pathHandler.setInstance(m.getDeclaringClass().newInstance());
                } catch (Exception exc) {
                    throw new RuntimeException("Unable to create class " + m.getDeclaringClass(), exc);
                }
            }

            pathHandler.setMethodType(a.method());

            getLog().info("Added path handler: " + pathHandler.getPath());

            pathHandlers.add(pathHandler.build());
        }


    }

    class PathHandler implements Comparable<PathHandler> {

        String path;
        private LambadaFunction lambadaFunction;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path.replaceAll("\\{([^}]+)\\}", ":$1");
        }

        ApiGateway.MethodType methodType;

        public ApiGateway.MethodType getMethodType() {
            return methodType;
        }

        public void setMethodType(ApiGateway.MethodType methodType) {
            this.methodType = methodType;
        }

        Object instance;

        public Object getInstance() {
            return instance;
        }

        public void setInstance(Object instance) {
            this.instance = instance;
        }

        Method method;

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        @Override
        public int compareTo(PathHandler o) {
            return new CompareToBuilder().append(this.path, o.path).append(this.methodType, o.getMethodType()).toComparison();
        }

        @Override
        public String toString() {
            return "PathHandler{" +
                    "path='" + path + '\'' +
                    ", methodType=" + methodType +
                    ", instance=" + instance +
                    ", method=" + method +
                    '}';
        }

        Invoker invoker = null;

        public PathHandler build() {
            invoker = new Invoker();

            invoker.setUserHandler(UserHandlerFactory.findUserFactory(this.instance, this.method));

            if (null == invoker.getUserHandler())
                throw new IllegalArgumentException("Oops: Unable to build an path handler for " + this.toString());

            return this;
        }

        public Object handle(Request request, Response response) throws Exception {
            PassthoughRequest<JsonNode> node = buildPassthroughtRequest(request);

            InputStream is = IOUtils.toInputStream(OBJECT_MAPPER.writeValueAsString(node));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try {
                final LambadaContext.LambadaContextBuilder builder = LambadaContext.builder();

                builder.memoryLimitInMB(this.lambadaFunction.memorySize());
                builder.functionName(defaultString(this.lambadaFunction.alias(), this.lambadaFunction.name()));
                builder.timeoutsAt(this.lambadaFunction.timeout() * 1000 + System.currentTimeMillis());

                invoker.invoke(is, baos, builder.build());

                response.status(200);
                response.type("application/json");

                if (0 == baos.size()) {
                    return "null";
                } else {
                    try {
                        JsonNode asJsonNode = OBJECT_MAPPER.readValue(baos.toByteArray(), JsonNode.class);

                        return asJsonNode;
                    } catch (Exception e) {
                        response.type("text/plain");

                        return new String(baos.toByteArray());
                    }
                }
            } catch (Exception exc) {
                getLog().warn("Exception: ", exc);

                response.status(500);
                response.type("application/json");

                if (OBJECT_MAPPER.canSerialize(exc.getClass())) {
                    try {
                        return OBJECT_MAPPER.writeValueAsString(exc);
                    } catch (Exception e) {
                        ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();

                        parentNode.put("message", exc.getMessage());
                        parentNode.put("class", exc.getClass().getName());

                        return parentNode;
                    }
                } else {
                    ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();

                    parentNode.put("message", exc.getMessage());
                    parentNode.put("class", exc.getClass().getName());

                    return parentNode;
                }
            }
        }

        public void setLambadaFunction(LambadaFunction lambadaFunction) {
            this.lambadaFunction = lambadaFunction;
        }
    }

    private PassthoughRequest<JsonNode> buildPassthroughtRequest(Request request) throws Exception {
        final String bodyJsonNode = defaultIfBlank(request.body(), "null");

        PassthoughRequest<JsonNode> node = new PassthoughRequest<>();

        final JsonNode body = OBJECT_MAPPER.readTree(bodyJsonNode);

        boolean looksLikePassthrough = asList("body-json", "params", "stage-variables", "context")
                .stream().map(x -> {
                    JsonNode childNode = body.path(x);

                    return !childNode.isMissingNode();
                }).filter(x -> x).count() > 2;

        if (looksLikePassthrough) {
            node = PassthoughRequest.getRequest(
                    OBJECT_MAPPER,
                    JsonNode.class,
                    new ByteArrayInputStream(bodyJsonNode.getBytes(Charset.defaultCharset())));

            node.getStageVariables().putAll(this.stageVariables);

            return node;
        } else {
            final PassthoughRequest<JsonNode> finalNode = node;

            node.setBody(body);

            request.params().entrySet().forEach(e -> {
                finalNode.getParams().getPath().put(e.getKey().substring(1), e.getValue());
            });

            request.queryParams().forEach(x -> {
                finalNode.getParams().getQueryString().put(x, request.queryParams(x));
            });

            request.headers().forEach(x -> {
                finalNode.getParams().getHeader().put(x, request.headers(x));
            });

            finalNode.getStageVariables().putAll(this.stageVariables);

            return finalNode;
        }
    }
}
