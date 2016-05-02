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

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * <pre>{
 * "callbackWaitsForEmptyEventLoop": true,
 * "logGroupName": "/aws/lambda/fp_echo",
 * "logStreamName": "2016/04/23/[$LATEST]ad837f6486c5408cbcc06919f517299c",
 * "functionName": "fp_echo",
 * "memoryLimitInMB": "128",
 * "functionVersion": "$LATEST",
 * "invokeid": "4b15692d-091b-11e6-bf20-7fe08e57ecf0",
 * "awsRequestId": "4b15692d-091b-11e6-bf20-7fe08e57ecf0",
 * "invokedFunctionArn": "arn:aws:lambda:us-east-1:235368163414:function:fp_echo"
 * }</pre>
 */
@Builder
public class LambadaContext implements Context {
  @Getter final String awsRequestId = UUID.randomUUID().toString().toLowerCase();

  final long timeoutsAt;

  @Getter final String functionName;

  @Override
  public String getLogGroupName() {
    return String.format("/aws/lambda/%s", functionName);
  }

  @Override
  public int getRemainingTimeInMillis() {
    return (int) (timeoutsAt - System.currentTimeMillis());
  }

  @Getter final int memoryLimitInMB;

  @Getter String logStreamName;

  @Getter LambdaLogger logger = msg -> System.err.println(msg);

  @Getter String functionVersion = "$LATEST";

  String invokedFunctionArn;

  @Override
  public String getInvokedFunctionArn() {
    if (null != invokedFunctionArn) {
      return invokedFunctionArn;
    } else {
      return "arn:aws:lambda:us-east-1:235368163414:function:" + functionName;
    }
  }

  @Getter CognitoIdentity identity;

  @Getter ClientContext clientContext;
}
