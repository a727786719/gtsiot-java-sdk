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

package io.github.a727786719.driver.configuration;


import io.github.a727786719.driver.DriverApp;
import io.github.a727786719.driver.GlobalContext;
import io.github.a727786719.driver.ai.AIServer;
import io.github.a727786719.driver.configuration.properties.*;
import io.github.a727786719.driver.data.*;
import io.github.a727786719.driver.data.handlers.TagValueCache;
import io.github.a727786719.driver.data.impl.AmqpDataSender;
import io.github.a727786719.driver.data.impl.KafkaDataSender;
import io.github.a727786719.driver.data.impl.LocalDataSender;
import io.github.a727786719.driver.data.impl.MQTTDataSender;
import io.github.a727786719.driver.grpc.driver.DriverServiceGrpc;
import io.github.a727786719.driver.listener.DriverEventListener;
import io.github.a727786719.driver.listener.GrpcDriverEventListener;
import io.github.a727786719.logger.LoggerContexts;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 驱动配置类
 */
@Configuration
@ImportRuntimeHints(GraalvmRuntimeHits.class)
@EnableConfigurationProperties({DriverAppProperties.class,
        DriverDataProperties.class,
        DriverMQProperties.class,
        DriverListenerProperties.class,
        DriverHttpProperties.class,
        DriverDataFileProperties.class,
        DriverLicenseProperties.class
})
public class DriverAutoConfiguration {

    public DriverAutoConfiguration(DriverAppProperties properties) {
        LoggerContexts.setDefaultProjectId(properties.getProjectId());
        LoggerContexts.setDefaultService(properties.getProjectId() + "-" + properties.getInstanceId() + "-" + properties.getId());
    }

    @Bean
    public TagValueCache tagValueCache() {
        return new TagValueCache();
    }

    @Bean
    @ConditionalOnMissingBean(DataHandlerChain.class)
    public DataHandlerChain dataHandlerChain(TagValueCache tagValueCache, ObjectProvider<DataHandler> handlers) {
        List<DataHandler> dataHandlers = handlers.stream().collect(Collectors.toList());
        return new DefaultDataHandlerChain(tagValueCache, dataHandlers);
    }

    @Bean
    public GlobalContext globalContext() {
        return new GlobalContext();
    }

    @Bean
    public AIServer aiServer(DriverAppProperties properties,
                             DriverHttpProperties httpProperties,
                             DriverDataFileProperties dataFileProperties,
                             DriverLicenseProperties licenseProperties,
                             GlobalContext globalContext,
                             ObjectProvider<DriverApp> driverApp,
                             DataDispatchers dispatchers) {
        DriverApp<Object, Object, Object> app = driverApp.getIfUnique();
        if (app == null) {
            throw new BeanCreationException("未找到或找到多个 DriverApp 实例");
        }
        return new AIServer(properties, httpProperties, dataFileProperties, licenseProperties, globalContext, app, dispatchers);
    }

    @Bean
    public DriverEventListener driverEventListener(DriverListenerProperties properties,
                                                   DriverAppProperties driverProperties,
                                                   ObjectProvider<DriverApp> driverApp,
                                                   GlobalContext globalContext) {
        DriverApp<Object, Object, Object> app = driverApp.getIfUnique();
        if (app == null) {
            throw new BeanCreationException("未找到或找到多个 DriverApp 实例");
        }

        Channel channel = ManagedChannelBuilder.forAddress(properties.getHost(), properties.getPort())
                .usePlaintext()
                .maxInboundMessageSize(properties.getMaxInboundMessageSize())
                .disableRetry()
                .build();
        DriverServiceGrpc.DriverServiceBlockingStub driverGrpcClient = DriverServiceGrpc.newBlockingStub(channel);

        return new GrpcDriverEventListener(
                driverProperties, properties,
                globalContext, app, driverGrpcClient
        );
    }

    @Bean
    public DataSender dataSender(DriverAppProperties driverAppProperties,
                                 DataHandlerChain dataHandlerChain,
                                 GlobalContext globalContext,
                                 ObjectProvider<DriverServiceGrpc.DriverServiceBlockingStub> driverGrpcClient,
                                 DataWriter dataWriter,
                                 DataDispatchers dispatchers) {
        return new DefaultDataSender(driverAppProperties, globalContext, dataHandlerChain, driverGrpcClient.getIfAvailable(), dataWriter, dispatchers);
    }

    @Bean
    public DataWriter dataWriter(DriverDataProperties driverDataProperties,
                                 DriverAppProperties driverAppProperties,
                                 DriverMQProperties properties) {
        String projectId = driverAppProperties.getProjectId();
        return switch (properties.getType()) {
            case MQTT -> new MQTTDataSender(projectId, driverDataProperties, driverAppProperties, properties.getMqtt());
            case RABBIT -> new AmqpDataSender(projectId, driverDataProperties, properties.getAmqp());
            case KAFKA ->
                    new KafkaDataSender(projectId, driverDataProperties, driverAppProperties, properties.getKafka());
            case LOCAL -> new LocalDataSender(projectId, driverDataProperties);
        };
    }

    @Bean
    public DataDispatchers dispatchers() {
        return new DataDispatchers();
    }
}

