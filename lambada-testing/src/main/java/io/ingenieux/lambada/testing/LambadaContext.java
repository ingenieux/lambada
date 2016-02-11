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

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class LambadaContext implements Context {

  protected String awsRequestId;

  protected String logGroupName;

  protected String logStreamName;

  protected String functionName;

  protected String functionVersion;

  protected String invokedFunctionArn;

  protected CognitoIdentity identity;

  protected int remainingTimeInMillis;

  protected int memoryLimitInMB;

  protected LambdaLogger logger;

  protected ClientContext clientContext;

  protected LambadaContext(String awsRequestId, String logGroupName, String logStreamName,
                         String functionName, String functionVersion,
                         String invokedFunctionArn,
                         CognitoIdentity identity, int remainingTimeInMillis, int memoryLimitInMB,
                         LambdaLogger logger,
                         ClientContext clientContext) {
    this.awsRequestId = awsRequestId;
    this.logGroupName = logGroupName;
    this.logStreamName = logStreamName;
    this.functionName = functionName;
    this.functionVersion = functionVersion;
    this.invokedFunctionArn = invokedFunctionArn;
    this.identity = identity;
    this.remainingTimeInMillis = remainingTimeInMillis;
    this.memoryLimitInMB = memoryLimitInMB;
    this.logger = logger;
    this.clientContext = clientContext;
  }

  @Override
  public String getAwsRequestId() {
    return awsRequestId;
  }

  @Override
  public String getLogGroupName() {
    return logGroupName;
  }

  @Override
  public String getLogStreamName() {
    return logStreamName;
  }

  @Override
  public String getFunctionName() {
    return functionName;
  }

  @Override
  public String getFunctionVersion() {
    return functionVersion;
  }

  @Override
  public String getInvokedFunctionArn() {
    return invokedFunctionArn;
  }

  @Override
  public CognitoIdentity getIdentity() {
    return identity;
  }

  @Override
  public ClientContext getClientContext() {
    return clientContext;
  }

  @Override
  public int getRemainingTimeInMillis() {
    return remainingTimeInMillis;
  }

  @Override
  public int getMemoryLimitInMB() {
    return memoryLimitInMB;
  }

  @Override
  public LambdaLogger getLogger() {
    return logger;
  }

  public static class Builder {

    private String awsRequestId;

    private String logGroupName;

    private String logStreamName;

    private String functionName;

    private String functionVersion;

    private String invokedFunctionArn;

    private CognitoIdentity identity = LambadaIdentity.Builder.lambadaIdentity().build();

    private int remainingTimeInMillis;

    private int memoryLimitInMB;

    private LambdaLogger logger = new LambadaLogger();

    private ClientContext clientContext = LambadaClientContext.Builder.lambadaClientContext().build();

    private Builder() {
    }

    public static Builder lambadaContext() {
      return new Builder();
    }

    public Builder withAwsRequestId(String awsRequestId) {
      this.awsRequestId = awsRequestId;
      return this;
    }

    public Builder withLogGroupName(String logGroupName) {
      this.logGroupName = logGroupName;
      return this;
    }

    public Builder withLogStreamName(String logStreamName) {
      this.logStreamName = logStreamName;
      return this;
    }

    public Builder withFunctionName(String functionName) {
      this.functionName = functionName;
      return this;
    }

    public Builder withFunctionVersion(String functionVersion) {
      this.functionVersion = functionVersion;
      return this;
    }

    public Builder withInvokedFunctionArn(String invokedFunctionArn) {
      this.invokedFunctionArn = invokedFunctionArn;
      return this;
    }

    public Builder withIdentity(CognitoIdentity identity) {
      this.identity = identity;
      return this;
    }

    public Builder withRemainingTimeInMillis(int remainingTimeInMillis) {
      this.remainingTimeInMillis = remainingTimeInMillis;
      return this;
    }

    public Builder withMemoryLimitInMB(int memoryLimitInMB) {
      this.memoryLimitInMB = memoryLimitInMB;
      return this;
    }

    public Builder withLogger(LambdaLogger logger) {
      this.logger = logger;
      return this;
    }

    public Builder withClientContext(ClientContext clientContext) {
      this.clientContext = clientContext;
      return this;
    }

    public LambadaContext build() {
      return new LambadaContext(awsRequestId, logGroupName, logStreamName, functionName,
                                functionVersion, invokedFunctionArn, identity,
                                remainingTimeInMillis, memoryLimitInMB, logger, clientContext);
    }
  }
}
