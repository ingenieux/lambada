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
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import io.ingenieux.lambada.runtime.ApiGateway;
import io.ingenieux.lambada.runtime.LambadaFunction;

import static org.codehaus.plexus.util.StringUtils.isNotBlank;

@Mojo(name = "generate",
        requiresProject = true,
        defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class LambadaGenerateMojo
        extends AbstractLambadaMetadataMojo {
    /**
     * Location of the file for Lambda + APIGatewayDefinition Functions
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}/META-INF/lambda-definitions.json", property = "lambada.outputFile", required = true)
    private File outputFile;

    /**
     * Default Invoker Role
     */
    @Parameter(defaultValue = "apigateway-lambda-invoker", property = "lambada.apiGatewayInvokerRole")
    private String apiGatewayInvokerRole;

    /**
     * Default Invoker Role
     */
    @Parameter(defaultValue = "us-east-1", property = "beanstalker.region", required = true)
    private String region;

    private Mustache template;

    @Override
    protected void executeInternal() throws Exception {
        outputFile.getParentFile().mkdirs();

        final Set<Method> methodsAnnotatedWith = extractRuntimeAnnotations(LambadaFunction.class);

        // TODO: Validate clashing paths

        final TreeSet<LambadaFunctionDefinition> definitionTreeSet = methodsAnnotatedWith
                .stream()
                .map(this::extractFunctionDefinitions)
                .collect(Collectors.toCollection(TreeSet::new));

        final List<LambadaFunctionDefinition> defList = new ArrayList<>(definitionTreeSet);

        OBJECT_MAPPER.writeValue(new FileOutputStream(outputFile), defList);
    }

    private String interpolateDefinition(LambadaFunctionDefinition lambadaFunctionDefinition) throws IOException {
        Map<String, String> context = new HashMap<>();

        context.put("region", region);
        context.put("functionName", lambadaFunctionDefinition.getName());
        context.put("apiGatewayInvokerRole", apiGatewayInvokerRole);
        context.put("httpMethod", lambadaFunctionDefinition.getApi().getMethodType().toString().toLowerCase());

        StringWriter writer = new StringWriter();

        template.execute(writer, context).flush();

        return writer.toString();
    }

    private LambadaFunctionDefinition extractFunctionDefinitions(Method m) {
        LambadaFunction lF = m.getAnnotation(LambadaFunction.class);

        final LambadaFunctionDefinition result = new LambadaFunctionDefinition();

        final String name = defaultIfBlank(lF.name(), m.getName());

        result.setName(name); // #1

        final String handler = m.getDeclaringClass().getCanonicalName() + "::" + m.getName();

        result.setAlias(lF.alias());

        result.setHandler(handler);

        result.setMemorySize(lF.memorySize()); // #2

        if (isNotBlank(lF.role())) {
            result.setRole(lF.role()); // #3
        }

        result.setTimeout(lF.timeout());  // #4

        if (isNotBlank(lF.description())) {
            result.setDescription(lF.description());  // #5
        }

        result.setBindings(Arrays.asList(lF.bindings()));

        if (null != lF.api() && 1 == lF.api().length) {
            ApiGateway apiGatewayAnn = lF.api()[0];

            APIGatewayDefinition def = new APIGatewayDefinition();

            def.setMethodType(APIGatewayDefinition.MethodType.valueOf(apiGatewayAnn.method().name()));
            def.setPath(apiGatewayAnn.path());
            def.setTemplate(apiGatewayAnn.template());
            def.setCorsEnabled(apiGatewayAnn.corsEnabled());

            result.setApi(def);
        }

        return result;
    }
}
