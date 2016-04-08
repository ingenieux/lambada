package io.ingenieux.lambada.maven;

/**
 * Created by aldrin on 08/04/16.
 */
public class APIGatewayDefinition {
    String path;

    LambadaFunctionDefinition.MethodType methodType;

    String template;

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
}
