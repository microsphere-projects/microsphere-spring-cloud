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
package io.microsphere.spring.cloud.client.discovery.autoconfigure;

import io.microsphere.annotation.ConfigurationProperty;
import io.microsphere.spring.cloud.client.discovery.UnionDiscoveryClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.ConditionalOnBlockingDiscoveryEnabled;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.microsphere.annotation.ConfigurationProperty.APPLICATION_SOURCE;
import static io.microsphere.spring.cloud.client.discovery.constants.DiscoveryClientConstants.COMMONS_CLIENT_AUTO_CONFIGURATION_CLASS_NAME;
import static io.microsphere.spring.cloud.client.discovery.constants.DiscoveryClientConstants.DISCOVERY_CLIENT_CLASS_NAME;
import static io.microsphere.spring.cloud.commons.constants.CommonsPropertyConstants.MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX;

/**
 * {@link UnionDiscoveryClient} Auto-Configuration Class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see UnionDiscoveryClient
 * @see CommonsClientAutoConfiguration
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = {
        DISCOVERY_CLIENT_CLASS_NAME
})
@ConditionalOnDiscoveryEnabled
@ConditionalOnBlockingDiscoveryEnabled
@AutoConfigureBefore(name = {
        COMMONS_CLIENT_AUTO_CONFIGURATION_CLASS_NAME
})
public class DiscoveryClientAutoConfiguration {

    /**
     * The property prefix of {@link DiscoveryClient} : "microsphere.spring.cloud.client.discovery."
     */
    public static final String DISCOVERY_CLIENT_PROPERTY_PREFIX = MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX + "client.discovery.";

    /**
     * The property name of mode : "mode"
     */
    public static final String MODE_PROPERTY_NAME = "mode";

    /**
     * The {@link DiscoveryClient} "mode" for {@link UnionDiscoveryClient} : "union"
     */
    public static final String UNION_DISCOVERY_CLIENT_MODE = "union";

    /**
     * The property name of DiscoveryClient mode : "microsphere.spring.cloud.client.discovery.mode"
     */
    @ConfigurationProperty(
            source = APPLICATION_SOURCE
    )
    public static final String DISCOVERY_CLIENT_MODE_PROPERTY_NAME = DISCOVERY_CLIENT_PROPERTY_PREFIX + MODE_PROPERTY_NAME;


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = DISCOVERY_CLIENT_MODE_PROPERTY_NAME, havingValue = UNION_DISCOVERY_CLIENT_MODE)
    public static class UnionConfiguration {

        @Bean
        public UnionDiscoveryClient unionDiscoveryClient(ObjectProvider<DiscoveryClient> discoveryClientsProvider) {
            return new UnionDiscoveryClient(discoveryClientsProvider);
        }
    }
}
