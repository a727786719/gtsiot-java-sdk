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

package io.github.a727786719.client.service.spm;

import io.github.a727786719.client.annotation.NonProject;
import io.github.a727786719.client.dto.ResponseDTO;
import io.github.a727786719.client.dto.Token;
import io.github.a727786719.client.service.PlatformClient;
import io.github.a727786719.client.service.spm.dto.RequestAuthTokenBody;

import javax.annotation.Nonnull;

/**
 * 空间管理-用户客(租户)户端
 */
@NonProject
public interface SpmUserClient extends PlatformClient {

    /**
     * 使用 {@code appKey} 和 {@code appSecret} 获取租户级 token
     *
     * @param appKey    扩展应用标识
     * @param appSecret 扩展应用密钥
     * @return token
     */
    default ResponseDTO<Token> getToken(@Nonnull String appKey, @Nonnull String appSecret) {
        RequestAuthTokenBody body = new RequestAuthTokenBody();
        body.setAk(appKey);
        body.setSk(appSecret);
        return getToken(body);
    }

    /**
     * 使用 {@code appKey} 和 {@code appSecret} 获取租户级 token
     * @param auth 认证信息
     * @return token
     */
    ResponseDTO<Token> getToken(@Nonnull RequestAuthTokenBody auth);

}
