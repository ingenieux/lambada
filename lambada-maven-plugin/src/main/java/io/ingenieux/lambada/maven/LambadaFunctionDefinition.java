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

import java.io.Serializable;

public class LambadaFunctionDefinition implements Serializable, Comparable<LambadaFunctionDefinition> {
    public enum MethodType {
        GET,
        POST,
        DELETE,
        HEAD,
        OPTIONS,
        PATCH,
        PUT
    }

    String name;

    String description;

    int memorySize;

    String role;

    int timeout;

    String handler;

    APIGatewayDefinition api = null;

    public LambadaFunctionDefinition() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(int memorySize) {
        this.memorySize = memorySize;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public APIGatewayDefinition getApi() {
        return api;
    }

    public void setApi(APIGatewayDefinition api) {
        this.api = api;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LambadaFunctionDefinition)) return false;

        LambadaFunctionDefinition that = (LambadaFunctionDefinition) o;

        if (memorySize != that.memorySize) return false;
        if (timeout != that.timeout) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;
        if (handler != null ? !handler.equals(that.handler) : that.handler != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + memorySize;
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + timeout;
        result = 31 * result + (handler != null ? handler.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(LambadaFunctionDefinition o) {
        if (null == o)
            return -1;

        if (this == o)
            return 0;

        return this.name.compareTo(o.name);
    }
}
