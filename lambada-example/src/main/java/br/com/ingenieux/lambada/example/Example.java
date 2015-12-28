package br.com.ingenieux.lambada.example;

import com.amazonaws.services.lambda.runtime.Context;
import io.ingenieux.lambada.ann.LambadaFunction;

public class Example {
    @LambadaFunction(timeout=5)
    public String sayHello(String whom, Context ctx) {
        return String.format("Hello, %s!", whom);
    }
}
