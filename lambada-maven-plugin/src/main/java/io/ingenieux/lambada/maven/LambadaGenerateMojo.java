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
     * Location of the APIGatewayDefinition-only functions
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}/META-INF/apigateway/apigateway-swagger.json", property = "lambada.apiGatewayFile")
    private File apiGatewayFile;

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
        {
            MustacheFactory mf = new DefaultMustacheFactory();

            this.template = mf.compile(new StringReader(STR_METHOD_DEFINITION),
                    "path-definition");
        }

        outputFile.getParentFile().mkdirs();

        final Set<Method> methodsAnnotatedWith = extractRuntimeAnnotations(LambadaFunction.class);

        // TODO: Validate clashing paths

        final TreeSet<LambadaFunctionDefinition> definitionTreeSet = methodsAnnotatedWith
                .stream()
                .map(this::extractFunctionDefinitions)
                .collect(Collectors.toCollection(TreeSet::new));

        final List<LambadaFunctionDefinition> defList = new ArrayList<>(definitionTreeSet);

        OBJECT_MAPPER.writeValue(new FileOutputStream(outputFile), defList);

        rewriteTemplates(definitionTreeSet);
    }

    private static final String STR_METHOD_DEFINITION = "{\n" +
            "  \"consumes\":[" +
            "    \"application/json\"" +
            "  ]," +
            "  \"produces\":[" +
            "    \"application/json\"" +
            "  ]," +
            "  \"responses\":{" +
            "    \"200\":{" +
            "      \"description\":\"200 response\"," +
            "      \"schema\":{" +
            "        \"$ref\":\"#/definitions/Empty\"" +
            "      }" +
            "    }" +
            "  }," +
            "  \"x-amazon-apigateway-integration\":{" +
            "    \"type\":\"aws\"," +
            "    \"responses\":{" +
            "      \"default\":{" +
            "        \"statusCode\":\"200\"" +
            "      }" +
            "    }," +
            "    \"requestTemplates\":{" +
            "      \"application/json\":\"##  See http://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html\\n##  This template will pass through all parameters including path, querystring, header, stage variables, and context through to the integration endpoint via the body/payload\\n#set($allParams = $input.params())\\n{\\n\\\"body-json\\\" : $input.json('$'),\\n\\\"params\\\" : {\\n#foreach($type in $allParams.keySet())\\n    #set($params = $allParams.get($type))\\n\\\"$type\\\" : {\\n    #foreach($paramName in $params.keySet())\\n    \\\"$paramName\\\" : \\\"$util.escapeJavaScript($params.get($paramName))\\\"\\n        #if($foreach.hasNext),#end\\n    #end\\n}\\n    #if($foreach.hasNext),#end\\n#end\\n},\\n\\\"stage-variables\\\" : {\\n#foreach($key in $stageVariables.keySet())\\n\\\"$key\\\" : \\\"$util.escapeJavaScript($stageVariables.get($key))\\\"\\n    #if($foreach.hasNext),#end\\n#end\\n},\\n\\\"context\\\" : {\\n    \\\"account-id\\\" : \\\"$context.identity.accountId\\\",\\n    \\\"api-id\\\" : \\\"$context.apiId\\\",\\n    \\\"api-key\\\" : \\\"$context.identity.apiKey\\\",\\n    \\\"authorizer-principal-id\\\" : \\\"$context.authorizer.principalId\\\",\\n    \\\"caller\\\" : \\\"$context.identity.caller\\\",\\n    \\\"cognito-authentication-provider\\\" : \\\"$context.identity.cognitoAuthenticationProvider\\\",\\n    \\\"cognito-authentication-type\\\" : \\\"$context.identity.cognitoAuthenticationType\\\",\\n    \\\"cognito-identity-id\\\" : \\\"$context.identity.cognitoIdentityId\\\",\\n    \\\"cognito-identity-pool-id\\\" : \\\"$context.identity.cognitoIdentityPoolId\\\",\\n    \\\"http-method\\\" : \\\"$context.httpMethod\\\",\\n    \\\"stage\\\" : \\\"$context.stage\\\",\\n    \\\"source-ip\\\" : \\\"$context.identity.sourceIp\\\",\\n    \\\"user\\\" : \\\"$context.identity.user\\\",\\n    \\\"user-agent\\\" : \\\"$context.identity.userAgent\\\",\\n    \\\"user-arn\\\" : \\\"$context.identity.userArn\\\",\\n    \\\"request-id\\\" : \\\"$context.requestId\\\",\\n    \\\"resource-id\\\" : \\\"$context.resourceId\\\",\\n    \\\"resource-path\\\" : \\\"$context.resourcePath\\\"\\n    }\\n}\\n\"" +
            "    }," +
            "    \"credentials\":\"arn:aws:iam:::role/{{apiGatewayInvokerRole}}\"," +
            "    \"uri\":\"arn:aws:apigateway:{{region}}:lambda:path/2015-03-31/functions/arn:aws:lambda:{{region}}::function:{{functionName}}/invocations\"," +
            "    \"httpMethod\":\"{{httpMethod}}\"" +
            "  }\n" +
            "}";

    private static final String CORS_DEFINITION = "{\n" +
            "  \"consumes\":[" +
            "    \"application/json\"" +
            "  ]," +
            "  \"produces\":[" +
            "    \"application/json\"" +
            "  ]," +
            "  \"responses\":{" +
            "    \"200\":{" +
            "      \"description\":\"200 response\"," +
            "      \"schema\":{" +
            "        \"$ref\":\"#/definitions/Empty\"" +
            "      }" +
            "    }" +
            "  }," +
            "  \"x-amazon-apigateway-integration\":{" +
            "    \"type\":\"aws\"," +
            "    \"responses\":{" +
            "      \"default\":{" +
            "        \"statusCode\":\"200\"" +
            "      }" +
            "    }," +
            "    \"requestTemplates\":{" +
            "      \"application/json\":\"##  See http://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html\\n##  This template will pass through all parameters including path, querystring, header, stage variables, and context through to the integration endpoint via the body/payload\\n#set($allParams = $input.params())\\n{\\n\\\"body-json\\\" : $input.json('$'),\\n\\\"params\\\" : {\\n#foreach($type in $allParams.keySet())\\n    #set($params = $allParams.get($type))\\n\\\"$type\\\" : {\\n    #foreach($paramName in $params.keySet())\\n    \\\"$paramName\\\" : \\\"$util.escapeJavaScript($params.get($paramName))\\\"\\n        #if($foreach.hasNext),#end\\n    #end\\n}\\n    #if($foreach.hasNext),#end\\n#end\\n},\\n\\\"stage-variables\\\" : {\\n#foreach($key in $stageVariables.keySet())\\n\\\"$key\\\" : \\\"$util.escapeJavaScript($stageVariables.get($key))\\\"\\n    #if($foreach.hasNext),#end\\n#end\\n},\\n\\\"context\\\" : {\\n    \\\"account-id\\\" : \\\"$context.identity.accountId\\\",\\n    \\\"api-id\\\" : \\\"$context.apiId\\\",\\n    \\\"api-key\\\" : \\\"$context.identity.apiKey\\\",\\n    \\\"authorizer-principal-id\\\" : \\\"$context.authorizer.principalId\\\",\\n    \\\"caller\\\" : \\\"$context.identity.caller\\\",\\n    \\\"cognito-authentication-provider\\\" : \\\"$context.identity.cognitoAuthenticationProvider\\\",\\n    \\\"cognito-authentication-type\\\" : \\\"$context.identity.cognitoAuthenticationType\\\",\\n    \\\"cognito-identity-id\\\" : \\\"$context.identity.cognitoIdentityId\\\",\\n    \\\"cognito-identity-pool-id\\\" : \\\"$context.identity.cognitoIdentityPoolId\\\",\\n    \\\"http-method\\\" : \\\"$context.httpMethod\\\",\\n    \\\"stage\\\" : \\\"$context.stage\\\",\\n    \\\"source-ip\\\" : \\\"$context.identity.sourceIp\\\",\\n    \\\"user\\\" : \\\"$context.identity.user\\\",\\n    \\\"user-agent\\\" : \\\"$context.identity.userAgent\\\",\\n    \\\"user-arn\\\" : \\\"$context.identity.userArn\\\",\\n    \\\"request-id\\\" : \\\"$context.requestId\\\",\\n    \\\"resource-id\\\" : \\\"$context.resourceId\\\",\\n    \\\"resource-path\\\" : \\\"$context.resourcePath\\\"\\n    }\\n}\\n\"" +
            "    }," +
            "    \"credentials\":\"arn:aws:iam:::role/{{apiGatewayInvokerRole}}\"," +
            "    \"uri\":\"arn:aws:apigateway:{{region}}:lambda:path/2015-03-31/functions/arn:aws:lambda:{{region}}::function:{{functionName}}/invocations\"," +
            "    \"httpMethod\":\"{{httpMethod}}\"" +
            "  }\n" +
            "}";

    private void rewriteTemplates(TreeSet<LambadaFunctionDefinition> definitionTreeSet) throws Exception {
        if (!apiGatewayFile.exists()) {
            getLog().info("Skipping API Gateway generation (file doesnt exist: " + apiGatewayFile.getName() + ")");
            return;
        }

        String strDefinitions = IOUtils.toString(new FileInputStream(apiGatewayFile));

        ObjectNode docNode = ObjectNode.class.cast(OBJECT_MAPPER.readTree(strDefinitions));

        if (null == docNode.get("paths")) {
            docNode.putObject("paths");
        }

        ObjectNode pathsNode = ObjectNode.class.cast(docNode.get("paths"));

        PathGenerator pg = new PathGenerator(pathsNode, STR_METHOD_DEFINITION);

        definitionTreeSet
                .stream()
                .filter(lf -> null != lf.getApi())
                .filter(lf -> "default".equals(lf.getApi().getTemplate()))
                .forEach(pg::generateDefinition);

        String finalContent = OBJECT_MAPPER.writeValueAsString(docNode);

        IOUtils.write(finalContent.getBytes(DEFAULT_CHARSET), new FileOutputStream(apiGatewayFile));
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

    class PathGenerator {
        final ObjectNode pathsNode;

        final String methodDefinition;

        public PathGenerator(ObjectNode pathsNode, String methodDefinition) {
            this.pathsNode = pathsNode;
            this.methodDefinition = methodDefinition;
        }

        public void generateDefinition(LambadaFunctionDefinition lf) {
            try {
                APIGatewayDefinition def = lf.getApi();

                String parsedContent = interpolateDefinition(lf);

                ObjectNode templateNode = ObjectNode.class.cast(OBJECT_MAPPER.readTree(parsedContent));

                JsonNode _pathNode = pathsNode.path(def.getPath());
                ObjectNode pathNode;

                if (JsonNodeType.OBJECT != _pathNode.getNodeType()) {
                    pathsNode.remove(def.getPath());
                    pathNode = pathsNode.putObject(def.getPath());
                } else {
                    pathNode = ObjectNode.class.cast(_pathNode);
                }

                pathNode.set(def.getMethodType().name().toLowerCase(), templateNode);

                if (def.isCorsEnabled()) {
                    //ObjectNode corsNode =
                }
            } catch (Exception exc) {
                throw new RuntimeException(exc);
            }
        }
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
