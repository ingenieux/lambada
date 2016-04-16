package io.ingenieux.lambada.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;

import io.ingenieux.lambada.runtime.model.BodyFunction;
import io.ingenieux.lambada.runtime.model.PassthoughRequest;

public class LambadaUtils {
    private LambadaUtils() {
    }

    public static <I, O> void wrap(
            InputStream inputStream,
            OutputStream outputStream,
            Class<I> inputClass,
            BodyFunction<PassthoughRequest<I>, O> func) throws Exception {
        wrap(new ObjectMapper(), inputStream, outputStream, inputClass, func);
    }

    public static <I, O> void wrap(
            ObjectMapper mapper,
            InputStream inputStream,
            OutputStream outputStream,
            Class<I> inputClass,
            BodyFunction<PassthoughRequest<I>, O> func) throws Exception {
        PassthoughRequest<I> request = PassthoughRequest.getRequest(mapper, inputClass, inputStream);

        O output = func.execute(request);

        mapper.writeValue(outputStream, output);
    }
}
