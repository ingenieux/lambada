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

package io.ingenieux.lambada.invoker;

import org.junit.Test;

public class InvokerTest extends InvokerTestBase {

  @Test
  public void testDoSomethingAsANoOp() throws Exception {
    givenMethod("doSomethingAsANoOp");

    whenInvoked();

    thenMustSilentlyContain("doSomethingAsANoOp");
  }

  @Test
  public void doSomethingAsANoOpButWithContext() throws Exception {
    givenMethod("doSomethingAsANoOpButWithContext");

    whenInvoked();

    thenMustContain("doSomethingAsANoOpButWithContext");
  }

  @Test
  public void testSomethingRaw() throws Exception {
    givenMethod("doSomethingRaw");

    withInput("1");

    whenInvoked();

    thenMustHaveWrittenOnOutputStream("2");
  }

  @Test
  public void testSomethingRawWithContext() throws Exception {
    givenMethod("doSomethingRawWithContext");

    withInput("1");

    whenInvoked();

    thenMustContain("4");
    thenMustHaveWrittenOnOutputStream("2");
  }

  @Test
  public void testSomethingWithJackson() throws Exception {
    givenMethod("doSomethingWithJackson");

    withInput("\"doSomethingWithJackson\"");

    whenInvoked();

    thenMustSilentlyContain("doSomethingWithJackson");
  }
}
