package io.ingenieux.lambada.maven;

public class APIGatewayDefinition {
    String path;

    LambadaFunctionDefinition.MethodType methodType;

    String template;

    boolean isCorsEnabled;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LambadaFunctionDefinition.MethodType getMethodType() {
        return methodType;
    }

    public void setMethodType(LambadaFunctionDefinition.MethodType methodType) {
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
}
