package br.com.ingenieux.lambada.example;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class APIGatewayRequest {
    String body;

    Map<String, String> paramsPath = new LinkedHashMap<>();

    Map<String, String> paramsHeader = new LinkedHashMap<>();

    Map<String, String> paramsQuerystring = new LinkedHashMap<>();

    Map<String, String> stageVariables = new LinkedHashMap<>();

    Map<String, String> context = new LinkedHashMap<>();

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getParamsPath() {
        return paramsPath;
    }

    public void setParamsPath(Map<String, String> paramsPath) {
        this.paramsPath = paramsPath;
    }

    public Map<String, String> getParamsHeader() {
        return paramsHeader;
    }

    public void setParamsHeader(Map<String, String> paramsHeader) {
        this.paramsHeader = paramsHeader;
    }

    public Map<String, String> getParamsQuerystring() {
        return paramsQuerystring;
    }

    public void setParamsQuerystring(Map<String, String> paramsQuerystring) {
        this.paramsQuerystring = paramsQuerystring;
    }

    public Map<String, String> getStageVariables() {
        return stageVariables;
    }

    public void setStageVariables(Map<String, String> stageVariables) {
        this.stageVariables = stageVariables;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }
}
