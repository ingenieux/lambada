package io.ingenieux.lambada.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.io.Serializable;

@Data
public class LambadaFunctionDefinition implements Serializable, Comparable<LambadaFunctionDefinition> {
    String name;

    String description;

    int memorySize;

    String role;

    int timeout;

    String handler;

    @Override
    public int compareTo(LambadaFunctionDefinition o) {
        if (null == o)
            return -1;

        if (this == o)
            return 0;

        return new CompareToBuilder().append(this.name, o.name).toComparison();
    }
}
