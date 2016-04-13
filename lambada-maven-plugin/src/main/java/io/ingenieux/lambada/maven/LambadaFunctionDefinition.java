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

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class LambadaFunctionDefinition
    implements Comparable<LambadaFunctionDefinition> {

  String name;

  String alias;

  String description;

  int memorySize;

  String role;

  int timeout;

  String handler;

  APIGatewayDefinition api = null;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
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
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LambadaFunctionDefinition that = (LambadaFunctionDefinition) o;

    if (memorySize != that.memorySize) {
      return false;
    }
    if (timeout != that.timeout) {
      return false;
    }
    if (name != null ? !name.equals(that.name) : that.name != null) {
      return false;
    }
    if (alias != null ? !alias.equals(that.alias) : that.alias != null) {
      return false;
    }
    if (description != null ? !description.equals(that.description)
                            : that.description != null) {
      return false;
    }
    if (role != null ? !role.equals(that.role) : that.role != null) {
      return false;
    }
    if (handler != null ? !handler.equals(that.handler) : that.handler != null) {
      return false;
    }
    return api != null ? api.equals(that.api) : that.api == null;

  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (alias != null ? alias.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + memorySize;
    result = 31 * result + (role != null ? role.hashCode() : 0);
    result = 31 * result + timeout;
    result = 31 * result + (handler != null ? handler.hashCode() : 0);
    result = 31 * result + (api != null ? api.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("name", name)
        .append("alias", alias)
        .append("description", description)
        .append("memorySize", memorySize)
        .append("role", role)
        .append("timeout", timeout)
        .append("handler", handler)
        .append("api", api)
        .toString();
  }

  @Override
  public int compareTo(LambadaFunctionDefinition o) {
    if (o == this) {
      return 0;
    }
    if (null == o) {
      return -1;
    }

    return new CompareToBuilder()
        .append(this.name, o.name)
        .append(this.alias, o.alias)
        .append(this.description, o.description)
        .append(this.memorySize, o.memorySize)
        .append(this.role, o.role)
        .append(this.timeout, o.timeout)
        .append(this.handler, o.handler)
        .append(this.api, o.api)
        .toComparison();
  }
}
