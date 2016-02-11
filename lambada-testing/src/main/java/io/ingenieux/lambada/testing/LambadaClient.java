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

public class LambadaClient implements Client {

  final String appPackageName;

  final String appVersionCode;

  final String appVersionName;

  final String appTitle;

  final String installationId;

  private LambadaClient(String appPackageName, String appVersionCode, String appVersionName,
                        String appTitle, String installationId) {
    this.appPackageName = appPackageName;
    this.appVersionCode = appVersionCode;
    this.appVersionName = appVersionName;
    this.appTitle = appTitle;
    this.installationId = installationId;
  }

  @Override
  public String getAppPackageName() {
    return appPackageName;
  }

  @Override
  public String getAppVersionCode() {
    return appVersionCode;
  }

  @Override
  public String getAppVersionName() {
    return appVersionName;
  }

  @Override
  public String getAppTitle() {
    return appTitle;
  }

  @Override
  public String getInstallationId() {
    return installationId;
  }


  public static class Builder {

    private String appPackageName;
    private String appVersionCode;
    private String appVersionName;
    private String appTitle;
    private String installationId;

    private Builder() {
    }

    public static Builder lambadaClient() {
      return new Builder();
    }

    public Builder withAppPackageName(String appPackageName) {
      this.appPackageName = appPackageName;
      return this;
    }

    public Builder withAppVersionCode(String appVersionCode) {
      this.appVersionCode = appVersionCode;
      return this;
    }

    public Builder withAppVersionName(String appVersionName) {
      this.appVersionName = appVersionName;
      return this;
    }

    public Builder withAppTitle(String appTitle) {
      this.appTitle = appTitle;
      return this;
    }

    public Builder withInstallationId(String installationId) {
      this.installationId = installationId;
      return this;
    }

    public LambadaClient build() {
      return new LambadaClient(appPackageName, appVersionCode, appVersionName, appTitle,
                               installationId);
    }
  }
}
