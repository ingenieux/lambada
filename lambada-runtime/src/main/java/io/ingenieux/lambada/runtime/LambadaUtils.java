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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;

import io.ingenieux.lambada.runtime.model.BodyFunction;
import io.ingenieux.lambada.runtime.model.PassthroughRequest;

public class LambadaUtils {
    private LambadaUtils() {
    }

    public static <I, O> void wrap(
            InputStream inputStream,
            OutputStream outputStream,
            Class<I> inputClass,
            BodyFunction<PassthroughRequest<I>, O> func) throws Exception {
        wrap(new ObjectMapper(), inputStream, outputStream, inputClass, func);
    }

    public static <I, O> void wrap(
            ObjectMapper mapper,
            InputStream inputStream,
            OutputStream outputStream,
            Class<I> inputClass,
            BodyFunction<PassthroughRequest<I>, O> func) throws Exception {
        PassthroughRequest<I>
            request = PassthroughRequest.getRequest(mapper, inputClass, inputStream);

        O output = func.execute(request);

        mapper.writeValue(outputStream, output);
    }
}
