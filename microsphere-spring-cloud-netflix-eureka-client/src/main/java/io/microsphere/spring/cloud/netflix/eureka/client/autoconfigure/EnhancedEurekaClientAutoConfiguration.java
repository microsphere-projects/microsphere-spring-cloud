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
package io.microsphere.spring.cloud.netflix.eureka.client.autoconfigure;

import com.netflix.discovery.EurekaClient;
import io.microsphere.spring.cloud.netflix.eureka.client.ConditionalOnEurekaClientEnabled;
import io.microsphere.spring.guice.annotation.EnableGuice;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import static io.microsphere.spring.cloud.netflix.eureka.client.constants.EurekaClientConstants.ENABLED_PROPERTY_NAME;
import static io.microsphere.spring.cloud.netflix.eureka.client.constants.EurekaClientConstants.EUREKA_CLIENT_PROPERTY_PREFIX;

/**
 * Auto-Configuration Class to enhance {@link EurekaClient}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@EnableGuice
@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
@ConditionalOnEurekaClientEnabled
@ConditionalOnProperty(prefix = EUREKA_CLIENT_PROPERTY_PREFIX, name = ENABLED_PROPERTY_NAME, matchIfMissing = true)
@AutoConfigureBefore(EurekaClientAutoConfiguration.class)
public class EnhancedEurekaClientAutoConfiguration {

}
