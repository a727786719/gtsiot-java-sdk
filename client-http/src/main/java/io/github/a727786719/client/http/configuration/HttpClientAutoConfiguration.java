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

package io.github.a727786719.client.http.configuration;


import io.github.a727786719.client.http.CustomQueryMethodArgumentResolver;
import io.github.a727786719.client.http.CustomWebClientHttpServiceGroupConfigurer;
import io.github.a727786719.client.http.clients.HttpAuthorizationClientImpl;
import io.github.a727786719.client.http.clients.common.HttpCommonClient;
import io.github.a727786719.client.http.clients.core.TableDataClientFactoryImpl;
import io.github.a727786719.client.http.clients.core.TableDataClientImpl;
import io.github.a727786719.client.http.clients.core.TableDataCommonClient;
import io.github.a727786719.client.http.config.ServiceConfig;
import io.github.a727786719.client.properties.AuthorizationProperties;
import io.github.a727786719.client.service.AuthorizationClient;
import io.github.a727786719.client.service.core.AppClient;
import io.github.a727786719.client.service.core.TableDataClientFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.web.reactive.function.client.support.WebClientHttpServiceGroupConfigurer;
import org.springframework.web.service.invoker.HttpServiceArgumentResolver;
import org.springframework.web.service.registry.HttpServiceGroup;
import org.springframework.web.service.registry.ImportHttpServices;

import java.util.List;

@Configuration
@ImportRuntimeHints(GraalvmRuntimeHits.class)
@EnableConfigurationProperties({HttpClientProperties.class, AuthorizationProperties.class})
@ImportHttpServices(group = "core", clientType = HttpServiceGroup.ClientType.WEB_CLIENT, basePackages = "io.github.a727786719.client.http.clients.core")
@ImportHttpServices(group = "driver", clientType = HttpServiceGroup.ClientType.WEB_CLIENT, basePackages = "io.github.a727786719.client.http.clients.driver")
@ImportHttpServices(group = "data-service", clientType = HttpServiceGroup.ClientType.WEB_CLIENT, basePackages = "io.github.a727786719.client.http.clients.ds")
@ImportHttpServices(group = "warning", clientType = HttpServiceGroup.ClientType.WEB_CLIENT, basePackages = "io.github.a727786719.client.http.clients.warn")
@ImportHttpServices(group = "spm", clientType = HttpServiceGroup.ClientType.WEB_CLIENT, basePackages = "io.github.a727786719.client.http.clients.spm")
public class HttpClientAutoConfiguration {

    @Bean
    public CustomQueryMethodArgumentResolver customQueryMethodArgumentResolver() {
        return new CustomQueryMethodArgumentResolver();
    }

    @Bean
    WebClientHttpServiceGroupConfigurer customWebClientHttpServiceGroupConfigurer(HttpClientProperties properties,
                                                                                  ObjectProvider<AuthorizationClient> authorizationClient,
                                                                                  List<HttpServiceArgumentResolver> httpServiceArgumentResolvers) {
        return new CustomWebClientHttpServiceGroupConfigurer(properties, authorizationClient, httpServiceArgumentResolvers);
    }

    @Bean
    public AuthorizationClient authorizationClient(AuthorizationProperties properties,
                                                   AppClient httpAppClient) {
        return new HttpAuthorizationClientImpl(httpAppClient, properties);
    }

    @Bean
    public HttpCommonClient commonHttpClient(HttpClientProperties properties, AuthorizationClient authorizationClient) {
        ServiceConfig config = properties.getDefaultConfig();
        return new HttpCommonClient(properties.getHost(), authorizationClient,
                config.getConnectTimeout(), config.getReadTimeout(), config.getReadTimeout());
    }

    @Bean
    public TableDataCommonClient tableDataCommonClient(HttpCommonClient client) {
        return new TableDataCommonClient(client);
    }

    @Bean
    public TableDataClientFactory tableDataClientFactory(TableDataClientImpl tableDataClient) {
        return new TableDataClientFactoryImpl(tableDataClient);
    }
}
