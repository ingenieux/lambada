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

import com.amazonaws.services.lambda.runtime.Context;

import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import io.ingenieux.lambada.invoker.fixtures.Fixture;
import io.ingenieux.lambada.invoker.fixtures.LoggerFixture;
import io.ingenieux.lambada.invoker.fixtures.Notifier;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class InvokerTestBase {

  Object instance;

  Invoker invoker;

  Context context;

  InputStream inputStream;

  ByteArrayOutputStream outputStream;

  LoggerFixture lambdaLogger;

  Method m;

  @Before
  public void setUp() {
    this.instance = new Fixture();
    this.invoker = new Invoker();
    this.inputStream = Mockito.mock(InputStream.class);
    this.outputStream = new ByteArrayOutputStream();
    this.context = Mockito.mock(Context.class);

    this.lambdaLogger = new LoggerFixture();

    Mockito.when(this.context.getLogger()).
        thenReturn(this.lambdaLogger);
  }

  @After
  public void tearDown() {
    Notifier.clear();
  }

  public void givenMethod(String methodName) {
    m = getMethod(methodName);

    invoker.setUserHandler(UserHandlerFactory.findUserFactory(instance, m));
  }

  public void whenInvoked() throws Exception {
    invoker.invoke(inputStream, outputStream, context);
  }

  public void thenMustSilentlyContain(String message) {
    MatcherAssert
        .assertThat("Must contain '" + message + "'", Notifier.getMessages(), CoreMatchers
            .hasItem(message));
  }

  public void thenMustContain(String message) {
    MatcherAssert
        .assertThat("Must have called log with '" + message + "'", lambdaLogger.getMessageList(),
                    CoreMatchers
                        .hasItem(message));
  }

  public Method getMethod(String methodName) {
    for (Method m : this.instance.getClass().getDeclaredMethods()) {
      if (m.getName().equals(methodName)) {
        return m;
      }
    }

    throw new IllegalStateException("Method not found: " + methodName);
  }

  public void withInput(String string) throws Exception {
      this.inputStream = IOUtils.toInputStream(string);
  }

  public void thenMustHaveWrittenOnOutputStream(String str) throws Exception {
      assertThat(outputStream.toString("utf-8"), is(equalTo(str)));
  }
}
