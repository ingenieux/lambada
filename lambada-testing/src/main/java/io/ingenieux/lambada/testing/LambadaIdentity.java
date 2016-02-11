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

import com.amazonaws.services.lambda.runtime.CognitoIdentity;

public class LambadaIdentity implements CognitoIdentity {
  protected String identityPoolId;

  protected String identityId;

  protected LambadaIdentity(String identityPoolId, String identityId) {
    this.identityPoolId = identityPoolId;
    this.identityId = identityId;
  }

  @Override
  public String getIdentityPoolId() {
    return identityPoolId;
  }

  @Override
  public String getIdentityId() {
    return identityId;
  }


  public static class Builder {

    private String identityPoolId;
    private String identityId;

    private Builder() {
    }

    public static Builder lambadaIdentity() {
      return new Builder();
    }

    public Builder withIdentityPoolId(String identityPoolId) {
      this.identityPoolId = identityPoolId;
      return this;
    }

    public Builder withIdentityId(String identityId) {
      this.identityId = identityId;
      return this;
    }

    public LambadaIdentity build() {
      return new LambadaIdentity(identityPoolId, identityId);
    }
  }
}
