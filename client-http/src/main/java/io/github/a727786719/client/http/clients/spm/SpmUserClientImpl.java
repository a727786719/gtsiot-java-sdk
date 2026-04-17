/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.a727786719.client.http.clients.spm;

import io.github.a727786719.client.annotation.DisableAuth;
import io.github.a727786719.client.dto.ResponseDTO;
import io.github.a727786719.client.dto.Token;
import io.github.a727786719.client.service.spm.SpmUserClient;
import io.github.a727786719.client.service.spm.dto.RequestAuthTokenBody;
import org.jspecify.annotations.NonNull;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface SpmUserClientImpl extends SpmUserClient {

    @DisableAuth
    @PostExchange("/spm/token")
    @Override
    ResponseDTO<Token> getToken(@NonNull @RequestBody RequestAuthTokenBody auth);
}
