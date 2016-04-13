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
   * Function alias. Optional.
   */
  String alias() default "";

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

  /**
   * AWS API Gateway Endpoint (Optional)
   *
   * @return endpoint
     */
  ApiGateway[] api() default {};
}
