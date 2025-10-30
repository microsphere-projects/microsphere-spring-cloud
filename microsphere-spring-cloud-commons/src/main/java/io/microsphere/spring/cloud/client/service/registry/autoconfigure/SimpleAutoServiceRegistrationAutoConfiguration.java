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
package io.microsphere.spring.cloud.client.service.registry.autoconfigure;

import io.microsphere.annotation.ConfigurationProperty;
import io.microsphere.constants.PropertyConstants;
import io.microsphere.spring.cloud.client.service.registry.DefaultRegistration;
import io.microsphere.spring.cloud.client.service.registry.InMemoryServiceRegistry;
import io.microsphere.spring.cloud.client.service.registry.SimpleAutoServiceRegistration;
import io.microsphere.spring.cloud.client.service.registry.condition.ConditionalOnAutoServiceRegistrationEnabled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationAutoConfiguration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationConfiguration;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.UtilAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static io.microsphere.annotation.ConfigurationProperty.APPLICATION_SOURCE;
import static io.microsphere.spring.cloud.client.service.registry.autoconfigure.SimpleAutoServiceRegistrationAutoConfiguration.ENABLED_PROPERTY_NAME;
import static io.microsphere.spring.cloud.commons.constants.CommonsPropertyConstants.MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX;

/**
 * Auto-Configuration class for {@link SimpleAutoServiceRegistration}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SimpleAutoServiceRegistration
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = ENABLED_PROPERTY_NAME)
@ConditionalOnAutoServiceRegistrationEnabled
@AutoConfigureBefore(value = {
        AutoServiceRegistrationAutoConfiguration.class
})
@AutoConfigureAfter(value = {
        UtilAutoConfiguration.class,
        AutoServiceRegistrationConfiguration.class,
        SimpleDiscoveryClientAutoConfiguration.class
})
@Import(value = {
        SimpleAutoServiceRegistration.class
})
public class SimpleAutoServiceRegistrationAutoConfiguration {

    /**
     * The property name prefix : "microsphere.spring.cloud.service-registry.auto-registration.simple."
     */
    public static final String PROPERTY_NAME_PREFIX = MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX + "service-registry.auto-registration.simple.";

    /**
     * The property name : "microsphere.spring.cloud.service-registry.auto-registration.simple.enabled"
     */
    @ConfigurationProperty(
            type = boolean.class,
            source = APPLICATION_SOURCE
    )
    public static final String ENABLED_PROPERTY_NAME = PROPERTY_NAME_PREFIX + PropertyConstants.ENABLED_PROPERTY_NAME;

    @Bean
    public Registration registration(
            @Value("${spring.application.name:default}") String applicationName,
            ServerProperties serverProperties,
            InetUtils inetUtils
    ) {
        InetUtils.HostInfo hostInfo = inetUtils.findFirstNonLoopbackHostInfo();
        String host = hostInfo.getIpAddress();
        int port = serverProperties.getPort();
        String instanceId = host + ":" + port;
        DefaultRegistration registration = new DefaultRegistration();
        registration.setInstanceId(instanceId);
        registration.setServiceId(applicationName);
        registration.setHost(host);
        registration.setPort(serverProperties.getPort());
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceRegistry<Registration> serviceRegistry() {
        return new InMemoryServiceRegistry();
    }
}