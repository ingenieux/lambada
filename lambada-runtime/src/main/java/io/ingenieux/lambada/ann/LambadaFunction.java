package io.ingenieux.lambada.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LambadaFunction {
    String name() default "";

    String description() default "";

    int memorySize() default 0;

    String role() default "";

    int timeout() default 0;
}
