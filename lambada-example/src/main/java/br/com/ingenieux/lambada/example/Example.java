package br.com.ingenieux.lambada.example;

import com.amazonaws.services.lambda.runtime.Context;
import io.ingenieux.lambada.maven.ann.LambadaFunction;

public class Example {
    @LambadaFunction(timeout=15)
    public String sayHello(String whom, Context ctx) {
        return String.format("Hello, %s!", whom);
    }
}
