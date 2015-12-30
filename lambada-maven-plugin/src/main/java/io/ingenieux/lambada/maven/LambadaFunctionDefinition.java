package io.ingenieux.lambada.maven;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.io.Serializable;

public class LambadaFunctionDefinition implements Serializable, Comparable<LambadaFunctionDefinition> {
    String name;

    String description;

    int memorySize;

    String role;

    int timeout;

    String handler;

    public LambadaFunctionDefinition(String name, String description, int memorySize, String role, int timeout, String handler) {
        this.name = name;
        this.description = description;
        this.memorySize = memorySize;
        this.role = role;
        this.timeout = timeout;
        this.handler = handler;
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

        return new CompareToBuilder().append(this.name, o.name).toComparison();
    }
}
