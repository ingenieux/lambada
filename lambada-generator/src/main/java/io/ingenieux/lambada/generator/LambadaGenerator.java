package io.ingenieux.lambada.generator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.ingenieux.lambada.ann.LambadaFunction;
import io.ingenieux.lambada.model.LambadaFunctionDefinition;
import org.reflections.Reflections;
import org.reflections.serializers.XmlSerializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class LambadaGenerator {
    private final InputStream is;

    private final OutputStream os;

    public LambadaGenerator(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }


    public void execute() throws Exception {
        try {
            executeInternal();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    private void executeInternal() throws Exception {
        Reflections ref = new XmlSerializer().read(is);

        final TreeSet<LambadaFunctionDefinition> definitionTreeSet = ref.getMethodsAnnotatedWith(LambadaFunction.class).stream().map(m -> {
            LambadaFunction lF = m.getAnnotation(LambadaFunction.class);

            final LambadaFunctionDefinition result = new LambadaFunctionDefinition();

            final String name = defaultIfBlank(lF.name(), m.getName());

            result.setName(name); // #1

            final String handler = m.getDeclaringClass().getCanonicalName() + "::" + m.getName();

            result.setHandler(handler);

            result.setMemorySize(lF.memorySize()); // #2

            if (isNotBlank(lF.role())) {
                result.setRole(lF.role()); // #3
            }

            result.setTimeout(lF.timeout());  // #4

            if (isNotBlank(lF.description())) {
                result.setDescription(lF.description());  // #5
            }

            return result;
        }).collect(Collectors.toCollection(TreeSet::new));

        final List<LambadaFunctionDefinition> defList = new ArrayList<>(definitionTreeSet);

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);

        objectMapper.writeValue(os, defList);
    }

}
