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
 *
 */

package io.ingenieux.lambada.runtime.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.Test;

import java.io.ByteArrayInputStream;

import io.ingenieux.lambada.runtime.LambadaUtils;

public class PassthroughRequestTest {
  private ObjectMapper mapper = new ObjectMapper();

  @Test
  public void testDeserialization() throws Exception {
    String request =
        "{\"context\":{\"resource-path\":\"/blogs\",\"resource-id\":\"lwcvq6\",\"cognito-identity-id\":\"\",\"cognito-authentication-type\":\"\",\"cognito-authentication-provider\":\"\",\"caller\":\"235368163414\",\"authorizer-principal-id\":\"\",\"api-key\":\"test-invoke-api-key\",\"api-id\":\"sab50l01ob\",\"account-id\":\"235368163414\",\"cognito-identity-pool-id\":\"\",\"http-method\":\"POST\",\"stage\":\"test-invoke-stage\",\"source-ip\":\"test-invoke-source-ip\",\"user\":\"235368163414\",\"user-agent\":\"Apache-HttpClient/4.3.4 (java 1.5)\",\"user-arn\":\"arn:aws:iam::235368163414:root\",\"request-id\":\"test-invoke-request\"},\"stage-variables\":{},\"params\":{\"header\":{},\"querystring\":{},\"path\":{}},\"body-json\":{\"hello\":\"world\"}}";

    PassthroughRequest<ObjectNode> x = LambadaUtils.getRequest(mapper, ObjectNode.class, new ByteArrayInputStream(request.getBytes()));

    int i = 0;

    i++;
  }
}
