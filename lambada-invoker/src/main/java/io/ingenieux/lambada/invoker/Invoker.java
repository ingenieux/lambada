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

import java.io.InputStream;
import java.io.OutputStream;

public class Invoker {
  UserHandler userHandler;

  public UserHandler getUserHandler() {
    return userHandler;
  }

  public void setUserHandler(UserHandler userHandler) {
    this.userHandler = userHandler;
  }

  public void invoke(InputStream is, OutputStream os, Context context) throws Exception {
    userHandler.invoke(is, os, context);
  }
}
