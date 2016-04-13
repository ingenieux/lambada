package io.ingenieux.lambada.maven;

public class APIGatewayDefinition {
    String path;

    MethodType methodType;

    String template;

    boolean isCorsEnabled;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public MethodType getMethodType() {
        return methodType;
    }

    public void setMethodType(MethodType methodType) {
        this.methodType = methodType;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public boolean isCorsEnabled() {
        return isCorsEnabled;
    }

    public void setCorsEnabled(boolean corsEnabled) {
        isCorsEnabled = corsEnabled;
    }

    public enum MethodType {
      GET,
      POST,
      DELETE,
      HEAD,
      PATCH,
      PUT
    }
}
