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
 *
 */

package io.ingenieux.lambada.runtime;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import io.ingenieux.lambada.runtime.model.BodyFunction;
import io.ingenieux.lambada.runtime.model.PassthroughRequest;

public class LambadaUtils {
  private LambadaUtils() {}

  public static <I, O> void wrap(InputStream inputStream, OutputStream outputStream, Class<I> inputClass, BodyFunction<PassthroughRequest<I>, O> func)
      throws Exception {
    wrap(new ObjectMapper(), inputStream, outputStream, inputClass, func);
  }

  public static <I, O> void wrap(
      ObjectMapper mapper, InputStream inputStream, OutputStream outputStream, Class<I> inputClass, BodyFunction<PassthroughRequest<I>, O> func)
      throws Exception {
    PassthroughRequest<I> request = getRequest(mapper, inputClass, inputStream);

    O output = func.execute(request);

    mapper.writeValue(outputStream, output);
  }

  public static <T> JavaType getReferenceFor(ObjectMapper mapper, Class<T> clazz) {
    final TypeFactory typeFactory = mapper.getTypeFactory();

    return typeFactory.constructParametrizedType(PassthroughRequest.class, PassthroughRequest.class, clazz);
  }

  public static <T> PassthroughRequest<T> getRequest(ObjectMapper mapper, Class<T> clazz, Reader node) throws IOException {
    final JavaType typeReference = getReferenceFor(mapper, clazz);

    return mapper.convertValue(node, typeReference);
  }

  public static PassthroughRequest<ObjectNode> getRequest(ObjectMapper mapper, InputStream inputStream) throws IOException {
    return getRequest(mapper, ObjectNode.class, inputStream);
  }

  public static <T> PassthroughRequest<T> getRequest(ObjectMapper mapper, Class<T> clazz, InputStream inputStream) throws IOException {
    final JavaType typeReference = getReferenceFor(mapper, clazz);

    return mapper.readValue(inputStream, typeReference);
  }

  public static <T> PassthroughRequest<T> getRequest(ObjectMapper mapper, Class<T> clazz, JsonNode node) throws IOException {
    final JavaType typeReference = getReferenceFor(mapper, clazz);

    return mapper.convertValue(node, typeReference);
  }

  public static <T> PassthroughRequest<T> getRequest(ObjectMapper mapper, Class<T> clazz, String nodeJsonContent) throws IOException {
    final JavaType typeReference = getReferenceFor(mapper, clazz);

    return mapper.convertValue(nodeJsonContent, typeReference);
  }
}
