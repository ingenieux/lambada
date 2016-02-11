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

package io.ingenieux.lambada.testing;

import com.amazonaws.services.lambda.runtime.Client;
import com.amazonaws.services.lambda.runtime.ClientContext;

import java.util.HashMap;
import java.util.Map;

public class LambadaClientContext implements ClientContext {
  final Client client;

  final Map<String, String> custom;

  final Map<String, String> environment;

  private LambadaClientContext(Client client,
                              Map<String, String> custom,
                              Map<String, String> environment) {
    this.client = client;
    this.custom = custom;
    this.environment = environment;
  }

  @Override
  public Client getClient() {
    return client;
  }

  @Override
  public Map<String, String> getCustom() {
    return custom;
  }

  @Override
  public Map<String, String> getEnvironment() {
    return environment;
  }


  public static class Builder {
    private Client client = LambadaClient.Builder.lambadaClient().build();

    private Map<java.lang.String, java.lang.String> custom = new HashMap<>();

    private Map<java.lang.String, java.lang.String> environment = new HashMap<>();

    private Builder() {
    }

    public static Builder lambadaClientContext() {
      return new Builder();
    }

    public Builder withClient(Client client) {
      this.client = client;
      return this;
    }

    public Builder withCustom(Map<java.lang.String, java.lang.String> custom) {
      this.custom = custom;
      return this;
    }

    public Builder withEnvironment(Map<java.lang.String, java.lang.String> environment) {
      this.environment = environment;
      return this;
    }

    public LambadaClientContext build() {
      return new LambadaClientContext(client, custom, environment);
    }
  }
}
