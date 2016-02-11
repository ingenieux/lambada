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

package br.com.ingenieux.lambada.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;

import io.ingenieux.lambada.runtime.ApiGateway;
import io.ingenieux.lambada.runtime.LambadaFunction;

public class ExampleApiGateway1 {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LambadaFunction(timeout=300)
    @ApiGateway(path="/sayhello/1")
    public void sayHelloLowLevel(InputStream is, OutputStream os, Context ctx) throws Exception {
        String whom = OBJECT_MAPPER.readValue(is, String.class);

        IOUtils.write(String.format("Hello, %s!", whom), os);
    }

    @LambadaFunction(timeout=300)
    @ApiGateway(path="/sayhello/2")
    public void sayHelloLowLevel2(OutputStream os, Context ctx) throws Exception {
        IOUtils.write("Hello, World!", os);
    }

    @LambadaFunction(timeout=300)
    @ApiGateway(path="/sayhello/3")
    public void sayHelloLowLevel3(Context ctx) throws Exception {
        System.err.println("Hello, World!");
    }

    @LambadaFunction(timeout=300)
    @ApiGateway(path="/sayhello/4")
    public void sayHello4(ObjectNode objectNode) throws Exception {
        System.err.println("objectNode: " + objectNode);
    }
}
