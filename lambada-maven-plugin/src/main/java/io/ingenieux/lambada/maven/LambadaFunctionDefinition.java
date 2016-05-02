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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class LambadaFunctionDefinition implements Comparable<LambadaFunctionDefinition> {

  String name;

  String alias;

  String description;

  int memorySize;

  String role;

  int timeout;

  String handler;

  APIGatewayDefinition api = null;

  List<String> bindings = new ArrayList<>();

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

  public List<String> getBindings() {
    return bindings;
  }

  public void setBindings(List<String> bindings) {
    this.bindings = bindings;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    LambadaFunctionDefinition that = (LambadaFunctionDefinition) o;

    return new EqualsBuilder()
        .append(memorySize, that.memorySize)
        .append(timeout, that.timeout)
        .append(name, that.name)
        .append(alias, that.alias)
        .append(description, that.description)
        .append(role, that.role)
        .append(handler, that.handler)
        .append(api, that.api)
        .append(bindings, that.bindings)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(name)
        .append(alias)
        .append(description)
        .append(memorySize)
        .append(role)
        .append(timeout)
        .append(handler)
        .append(api)
        .append(bindings)
        .toHashCode();
  }

  @Override
  public int compareTo(LambadaFunctionDefinition o) {
    if (this == o) return 0;

    if (null == o) return 1;

    return new CompareToBuilder()
        .append(this.name, o.name)
        .append(this.alias, o.alias)
        .append(this.description, o.description)
        .append(this.memorySize, o.memorySize)
        .append(this.role, o.role)
        .append(this.timeout, o.timeout)
        .append(this.handler, o.handler)
        .append(this.api, o.api)
        .append(this.bindings, o.bindings)
        .toComparison();
  }
}
