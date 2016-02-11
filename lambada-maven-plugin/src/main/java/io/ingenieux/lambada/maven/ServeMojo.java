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
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TreeSet;

import io.ingenieux.lambada.invoker.Invoker;
import io.ingenieux.lambada.invoker.UserHandlerFactory;
import io.ingenieux.lambada.runtime.ApiGateway;
import io.ingenieux.lambada.testing.LambadaContext;
import spark.Request;
import spark.Response;

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

  /**
   * Port to Use
   */
  @Parameter(property = "lambada.port", defaultValue = "8080")
  Integer serverPort;
  private final Set<PathHandler> pathHandlers = new TreeSet<PathHandler>();

//
//    public class ServeHandler implements HttpHandler {
//        PathHandler p;
//
//        public ServeHandler(PathHandler p) {
//            this.p = p;
//        }
//
//        @Override
//        public void handle(HttpExchange ex) throws IOException {
//            if (!p.matchesRequest(ex)) {
//                ex.sendResponseHeaders(404, 0L);
//                ex.close();
//
//                return;
//            }
//
//            try {
//                byte[] output = invokePath(p, ex, ex.getRequestBody());
//
//                ex.sendResponseHeaders(200, output.length);
//                //ex.getRequestHeaders().set("Content-Type", "application/json; charset=utf8");
//                ex.getResponseBody().write(output);
//                ex.close();
//            } catch (Exception e) {
//                byte[] eAsByteArray = e.toString().getBytes();
//
//                ex.sendResponseHeaders(500, eAsByteArray.length);
//                ex.getRequestHeaders().set("Content-Type", "text/plain");
//                ex.getResponseBody().write(eAsByteArray);
//                ex.close();
//            }
//        }
//    }
//

  @Override
  protected void executeInternal() throws Exception {
    loadPathHandlers();

    port(serverPort);

    threadPool(8);

    setupServer();

    awaitInitialization();

    while (10 != System.in.read()) {
      Thread.sleep(500);
    }

    stop();
  }

  private void setupServer() {
    for (PathHandler p : pathHandlers) {
      if (p.getMethodType() == ApiGateway.MethodType.GET) {
        get(p.getPath(), p::handle);
      } else if (p.getMethodType() == ApiGateway.MethodType.POST) {
        post(p.getPath(), p::handle);
      }
    }
  }

  public void loadPathHandlers() throws Exception {
    final Set<Method> methodsAnnotatedWith = extractRuntimeAnnotations(ApiGateway.class);

    getLog().info("There are " + methodsAnnotatedWith.size() + " found methods.");

    for (Method m : methodsAnnotatedWith) {
      ApiGateway a = m.getAnnotation(ApiGateway.class);
      PathHandler pathHandler = new PathHandler();

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

      getLog().info("Added path handler: " + pathHandler);

      pathHandlers.add(pathHandler.build());
    }


  }

  class PathHandler implements Comparable<PathHandler> {

    String path;

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
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
      return path.compareTo(o.path);
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
      InputStream is = IOUtils.toInputStream(defaultString(request.body(), "null"));
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try {
        invoker.invoke(is, baos, LambadaContext.Builder.lambadaContext().build());

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
        response.status(500);
        response.type("application/json");

        if (OBJECT_MAPPER.canSerialize(exc.getClass())) {
          return OBJECT_MAPPER.writeValueAsString(exc);
        } else {
          ObjectNode parentNode = OBJECT_MAPPER.createObjectNode();

          parentNode.put("message", exc.getMessage());
          parentNode.put("class", exc.getClass().getName());

          return parentNode;
        }
      }
    }
  }

}
