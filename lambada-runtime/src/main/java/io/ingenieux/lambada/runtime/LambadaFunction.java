package io.ingenieux.lambada.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tag Annotation for Lambada (Lambda) Functions
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LambadaFunction {

  /**
   * Function name. Defaults to method name
   */
  String name() default "";

  /**
   * Function description.
   */
  String description() default "";

  /**
   * Memory Size, in MB
   */
  int memorySize() default 0;

  /**
   * AWS Role
   */
  String role() default "";

  /**
   * Timeout
   */
  int timeout() default 0;
}
