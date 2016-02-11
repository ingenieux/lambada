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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public abstract class UserHandlerFactory {

  public abstract UserHandler getUserHandler(Object o, Method m);

  public static class NoOpUserHandlerFactory extends UserHandlerFactory {

    public UserHandler getUserHandler(Object o, Method m) {
      if (0 == m.getParameterCount() && hasReturnType(m, Void.TYPE)) {
        return new UserHandler(o, m) {
          @Override
          public void invoke(InputStream is, OutputStream os, Context c) throws Exception {
            m.invoke(o);
          }
        };
      }

      if (hasReturnType(m, Void.TYPE) && hasParameterTypes(m, Context.class)) {
        return new UserHandler(o, m) {
          public void invoke(InputStream is, OutputStream os, Context c) throws Exception {
            m.invoke(o, c);
          }
        };
      }

      return null;
    }
  }

  public static class RawUserHandlerFactory extends UserHandlerFactory {

    public UserHandler getUserHandler(Object o, Method m) {
      if (1 == m.getParameterCount() && hasReturnType(m, Void.TYPE) && hasParameterTypes(m, InputStream.class)) {
        return new UserHandler(o, m) {
          @Override
          public void invoke(InputStream is, OutputStream os, Context c) throws Exception {
            m.invoke(o, is);
          }
        };
      }

      if (2 == m.getParameterCount() && hasReturnType(m, Void.TYPE) && hasParameterTypes(m, InputStream.class, Context.class)) {
        return new UserHandler(o, m) {
          @Override
          public void invoke(InputStream is, OutputStream os, Context c) throws Exception {
            m.invoke(o, is, c);
          }
        };
      }

      if (1 == m.getParameterCount() && hasReturnType(m, Void.TYPE) && hasParameterTypes(m, OutputStream.class)) {
        return new UserHandler(o, m) {
          @Override
          public void invoke(InputStream is, OutputStream os, Context c) throws Exception {
            m.invoke(o, os);
          }
        };
      }

      if (2 == m.getParameterCount() && hasReturnType(m, Void.TYPE) && hasParameterTypes(m, OutputStream.class, Context.class)) {
        return new UserHandler(o, m) {
          @Override
          public void invoke(InputStream is, OutputStream os, Context c) throws Exception {
            m.invoke(o, os, c);
          }
        };
      }

      if (2 == m.getParameterCount() &&
          hasReturnType(m, Void.TYPE) &&
          hasParameterTypes(m, InputStream.class, OutputStream.class)) {
        return new UserHandler(o, m) {
          @Override
          public void invoke(InputStream is, OutputStream os, Context c) throws Exception {
            m.invoke(o, is, os);
          }
        };
      }

      if (3 == m.getParameterCount() &&
                 hasReturnType(m, Void.TYPE) &&
                 hasParameterTypes(m, InputStream.class, OutputStream.class, Context.class)) {
        return new UserHandler(o, m) {
          public void invoke(InputStream is, OutputStream os, Context c) throws Exception {
            m.invoke(o, is, os, c);
          }
        };
      }

      return null;
    }
  }

  public static class JacksonAbleUserHandlerFactory extends UserHandlerFactory {

    final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public UserHandler getUserHandler(Object o, final Method m) {
      if (1 == m.getParameterCount() &&
          hasJacksonAbleParameters(m, m.getParameterTypes()[0]) && Void.TYPE.equals(m.getReturnType())) {
        return new UserHandler(o, m) {
          @Override
          public void invoke(InputStream is, OutputStream os, Context ctx) throws Exception {
            Object inputArgument = OBJECT_MAPPER.readValue(is, m.getParameterTypes()[0]);

            m.invoke(o, inputArgument);
          }
        };
      }

      if (1 == m.getParameterCount() && hasJacksonAbleParameters(m, m.getParameterTypes()[0],
                                                                 m.getReturnType())) {
        return new UserHandler(o, m) {
          @Override
          public void invoke(InputStream is, OutputStream os, Context ctx) throws Exception {
            Object inputArgument = OBJECT_MAPPER.readValue(is, m.getParameterTypes()[0]);

            Object result = m.invoke(o, inputArgument);

            OBJECT_MAPPER.writeValue(os, result);
          }
        };
      }

      if (2 == m.getParameterCount() &&
                 m.getParameterTypes()[1] == Context.class &&
                 hasJacksonAbleParameters(m, m.getParameterTypes()[0], m.getReturnType())) {
        return new UserHandler(o, m) {
          @Override
          public void invoke(InputStream is, OutputStream os, Context ctx) throws Exception {
            Object inputArgument = OBJECT_MAPPER.readValue(is, m.getParameterTypes()[0]);

            Object result = m.invoke(o, inputArgument, ctx);

            OBJECT_MAPPER.writeValue(os, result);
          }
        };
      }

      return null;
    }

    protected boolean hasJacksonAbleParameters(Method m, Type... types) {
      for (Type t : types) {
        try {
          JavaType javaType = (JavaType) OBJECT_MAPPER.getTypeFactory().constructType(t);

          boolean serializeP = OBJECT_MAPPER.canSerialize((Class<?>) t);
          boolean deserializeP = OBJECT_MAPPER.canDeserialize(javaType);

          if (! (serializeP && deserializeP))
            return false;
        } catch (IllegalArgumentException e) {
          return false;
        }
      }

      return true;
    }
  }

  protected boolean hasReturnType(Method m, Class<?> t) {
    return m.getReturnType().equals(t);
  }

  protected boolean hasParameterTypes(Method m, Class<?>... types) {
    int i = 0;

    for (Class<?> t : types) {
      if (!t.equals(m.getParameterTypes()[i])) {
        return false;
      }

      i++;
    }
    return true;
  }

  private static final UserHandlerFactory[] factories = new UserHandlerFactory[] {
      new NoOpUserHandlerFactory(),
      new RawUserHandlerFactory(),
      new JacksonAbleUserHandlerFactory(),
      };


  public static UserHandler findUserFactory(Object o, Method m) {
    UserHandler h = null;

    for (UserHandlerFactory f : factories) {
      h = f.getUserHandler(o, m);

      if (null != h) {
        break;
      }
    }

    return h;
  }
}
