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

package io.microsphere.spring.cloud.client.discovery.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.ConditionalOnBlockingDiscoveryEnabled;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static io.microsphere.spring.cloud.client.discovery.constants.DiscoveryClientConstants.DISCOVERY_CLIENT_CLASS_NAME;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A condition that checks if Blocking Spring Cloud Discovery is available.
 * <p>
 * This annotation combines {@link ConditionalOnClass} (checking for the presence of {@code DiscoveryClient}),
 * {@link ConditionalOnDiscoveryEnabled}, and {@link ConditionalOnBlockingDiscoveryEnabled} to ensure that
 * the discovery client is both present in the classpath and enabled for blocking operations.
 *
 * <h3>Example Usage</h3>
 * <pre>{@code
 * @Configuration
 * @ConditionalOnDiscoveryAvailable
 * public class DiscoveryClientConfiguration {
 *
 *     @Bean
 *     public MyService myService(DiscoveryClient discoveryClient) {
 *         return new MyService(discoveryClient);
 *     }
 * }
 * }</pre>
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConditionalOnDiscoveryEnabled
 * @see ConditionalOnBlockingDiscoveryEnabled
 * @see ConditionalOnClass
 * @since 1.0.0
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
@Inherited
@ConditionalOnClass(name = {
        DISCOVERY_CLIENT_CLASS_NAME
})
@ConditionalOnDiscoveryEnabled
@ConditionalOnBlockingDiscoveryEnabled
public @interface ConditionalOnBlockingDiscoveryAvailable {
}
