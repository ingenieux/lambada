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
 *
 */

package io.ingenieux.lambada.runtime.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PassthoughRequest<T> {

    public static PassthoughRequest<ObjectNode> getRequest(ObjectMapper mapper, InputStream inputStream) throws IOException {
        return getRequest(mapper, ObjectNode.class, inputStream);
    }

    public static <T> PassthoughRequest<T> getRequest(ObjectMapper mapper, Class<T> clazz, InputStream inputStream) throws IOException {
        final TypeFactory typeFactory = mapper.getTypeFactory();

        final JavaType typeReference = typeFactory
                .constructParametrizedType(PassthoughRequest.class, PassthoughRequest.class, clazz);

        return mapper.readValue(inputStream, typeReference);
    }

    @JsonProperty("body-json")
    T body;

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @JsonProperty("stage-variables")
    Map<String, String> stageVariables = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public Map<String, String> getStageVariables() {
        return stageVariables;
    }

    public void setStageVariables(Map<String, String> stageVariables) {
        this.stageVariables = stageVariables;
    }

    @JsonProperty("context")
    RequestContext context = new RequestContext();

    public RequestContext getContext() {
        return context;
    }

    public void setContext(RequestContext context) {
        this.context = context;
    }

    @JsonProperty("params")
    Params params = new Params();

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "path",
            "querystring",
            "header"
    })

    public static class Params {
        @JsonProperty("path")
        Map<String, String> path = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        public Map<String, String> getPath() {
            return path;
        }

        public void setPath(Map<String, String> path) {
            this.path = path;
        }

        @JsonProperty("querystring")
        Map<String, String> queryString = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        public Map<String, String> getQueryString() {
            return queryString;
        }

        public void setQueryString(Map<String, String> queryString) {
            this.queryString = queryString;
        }

        @JsonProperty("header")
        Map<String, String> header = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        public Map<String, String> getHeader() {
            return header;
        }

        public void setHeader(Map<String, String> header) {
            this.header = header;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "account-id",
            "api-id",
            "api-key",
            "authorizer-principal-id",
            "caller",
            "cognito-authentication-provider",
            "cognito-authentication-type",
            "cognito-identity-id",
            "cognito-identity-pool-id",
            "http-method",
            "stage",
            "source-ip",
            "user",
            "user-agent",
            "user-arn",
            "request-id",
            "resource-id",
            "resource-path"
    })
    public static class RequestContext {

        @JsonProperty("account-id")
        private String accountId;
        @JsonProperty("api-id")
        private String apiId;
        @JsonProperty("api-key")
        private String apiKey;
        @JsonProperty("authorizer-principal-id")
        private String authorizerPrincipalId;
        @JsonProperty("caller")
        private String caller;
        @JsonProperty("cognito-authentication-provider")
        private String cognitoAuthenticationProvider;
        @JsonProperty("cognito-authentication-type")
        private String cognitoAuthenticationType;
        @JsonProperty("cognito-identity-id")
        private String cognitoIdentityId;
        @JsonProperty("cognito-identity-pool-id")
        private String cognitoIdentityPoolId;
        @JsonProperty("http-method")
        private String httpMethod;
        @JsonProperty("stage")
        private String stage;
        @JsonProperty("source-ip")
        private String sourceIp;
        @JsonProperty("user")
        private String user;
        @JsonProperty("user-agent")
        private String userAgent;
        @JsonProperty("user-arn")
        private String userArn;
        @JsonProperty("request-id")
        private String requestId;
        @JsonProperty("resource-id")
        private String resourceId;
        @JsonProperty("resource-path")
        private String resourcePath;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        /**
         *
         * @return
         * The accountId
         */
        @JsonProperty("account-id")
        public String getAccountId() {
            return accountId;
        }

        /**
         *
         * @param accountId
         * The account-id
         */
        @JsonProperty("account-id")
        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        /**
         *
         * @return
         * The apiId
         */
        @JsonProperty("api-id")
        public String getApiId() {
            return apiId;
        }

        /**
         *
         * @param apiId
         * The api-id
         */
        @JsonProperty("api-id")
        public void setApiId(String apiId) {
            this.apiId = apiId;
        }

        /**
         *
         * @return
         * The apiKey
         */
        @JsonProperty("api-key")
        public String getApiKey() {
            return apiKey;
        }

        /**
         *
         * @param apiKey
         * The api-key
         */
        @JsonProperty("api-key")
        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        /**
         *
         * @return
         * The authorizerPrincipalId
         */
        @JsonProperty("authorizer-principal-id")
        public String getAuthorizerPrincipalId() {
            return authorizerPrincipalId;
        }

        /**
         *
         * @param authorizerPrincipalId
         * The authorizer-principal-id
         */
        @JsonProperty("authorizer-principal-id")
        public void setAuthorizerPrincipalId(String authorizerPrincipalId) {
            this.authorizerPrincipalId = authorizerPrincipalId;
        }

        /**
         *
         * @return
         * The caller
         */
        @JsonProperty("caller")
        public String getCaller() {
            return caller;
        }

        /**
         *
         * @param caller
         * The caller
         */
        @JsonProperty("caller")
        public void setCaller(String caller) {
            this.caller = caller;
        }

        /**
         *
         * @return
         * The cognitoAuthenticationProvider
         */
        @JsonProperty("cognito-authentication-provider")
        public String getCognitoAuthenticationProvider() {
            return cognitoAuthenticationProvider;
        }

        /**
         *
         * @param cognitoAuthenticationProvider
         * The cognito-authentication-provider
         */
        @JsonProperty("cognito-authentication-provider")
        public void setCognitoAuthenticationProvider(String cognitoAuthenticationProvider) {
            this.cognitoAuthenticationProvider = cognitoAuthenticationProvider;
        }

        /**
         *
         * @return
         * The cognitoAuthenticationType
         */
        @JsonProperty("cognito-authentication-type")
        public String getCognitoAuthenticationType() {
            return cognitoAuthenticationType;
        }

        /**
         *
         * @param cognitoAuthenticationType
         * The cognito-authentication-type
         */
        @JsonProperty("cognito-authentication-type")
        public void setCognitoAuthenticationType(String cognitoAuthenticationType) {
            this.cognitoAuthenticationType = cognitoAuthenticationType;
        }

        /**
         *
         * @return
         * The cognitoIdentityId
         */
        @JsonProperty("cognito-identity-id")
        public String getCognitoIdentityId() {
            return cognitoIdentityId;
        }

        /**
         *
         * @param cognitoIdentityId
         * The cognito-identity-id
         */
        @JsonProperty("cognito-identity-id")
        public void setCognitoIdentityId(String cognitoIdentityId) {
            this.cognitoIdentityId = cognitoIdentityId;
        }

        /**
         *
         * @return
         * The cognitoIdentityPoolId
         */
        @JsonProperty("cognito-identity-pool-id")
        public String getCognitoIdentityPoolId() {
            return cognitoIdentityPoolId;
        }

        /**
         *
         * @param cognitoIdentityPoolId
         * The cognito-identity-pool-id
         */
        @JsonProperty("cognito-identity-pool-id")
        public void setCognitoIdentityPoolId(String cognitoIdentityPoolId) {
            this.cognitoIdentityPoolId = cognitoIdentityPoolId;
        }

        /**
         *
         * @return
         * The httpMethod
         */
        @JsonProperty("http-method")
        public String getHttpMethod() {
            return httpMethod;
        }

        /**
         *
         * @param httpMethod
         * The http-method
         */
        @JsonProperty("http-method")
        public void setHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
        }

        /**
         *
         * @return
         * The stage
         */
        @JsonProperty("stage")
        public String getStage() {
            return stage;
        }

        /**
         *
         * @param stage
         * The stage
         */
        @JsonProperty("stage")
        public void setStage(String stage) {
            this.stage = stage;
        }

        /**
         *
         * @return
         * The sourceIp
         */
        @JsonProperty("source-ip")
        public String getSourceIp() {
            return sourceIp;
        }

        /**
         *
         * @param sourceIp
         * The source-ip
         */
        @JsonProperty("source-ip")
        public void setSourceIp(String sourceIp) {
            this.sourceIp = sourceIp;
        }

        /**
         *
         * @return
         * The user
         */
        @JsonProperty("user")
        public String getUser() {
            return user;
        }

        /**
         *
         * @param user
         * The user
         */
        @JsonProperty("user")
        public void setUser(String user) {
            this.user = user;
        }

        /**
         *
         * @return
         * The userAgent
         */
        @JsonProperty("user-agent")
        public String getUserAgent() {
            return userAgent;
        }

        /**
         *
         * @param userAgent
         * The user-agent
         */
        @JsonProperty("user-agent")
        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        /**
         *
         * @return
         * The userArn
         */
        @JsonProperty("user-arn")
        public String getUserArn() {
            return userArn;
        }

        /**
         *
         * @param userArn
         * The user-arn
         */
        @JsonProperty("user-arn")
        public void setUserArn(String userArn) {
            this.userArn = userArn;
        }

        /**
         *
         * @return
         * The requestId
         */
        @JsonProperty("request-id")
        public String getRequestId() {
            return requestId;
        }

        /**
         *
         * @param requestId
         * The request-id
         */
        @JsonProperty("request-id")
        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        /**
         *
         * @return
         * The resourceId
         */
        @JsonProperty("resource-id")
        public String getResourceId() {
            return resourceId;
        }

        /**
         *
         * @param resourceId
         * The resource-id
         */
        @JsonProperty("resource-id")
        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        /**
         *
         * @return
         * The resourcePath
         */
        @JsonProperty("resource-path")
        public String getResourcePath() {
            return resourcePath;
        }

        /**
         *
         * @param resourcePath
         * The resource-path
         */
        @JsonProperty("resource-path")
        public void setResourcePath(String resourcePath) {
            this.resourcePath = resourcePath;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }
    }
}
