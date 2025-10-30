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
package io.microsphere.spring.cloud.client.discovery.constants;

import org.junit.jupiter.api.Test;

import static io.microsphere.spring.cloud.client.discovery.constants.DiscoveryClientConstants.COMMONS_CLIENT_AUTO_CONFIGURATION_CLASS_NAME;
import static io.microsphere.spring.cloud.client.discovery.constants.DiscoveryClientConstants.COMPOSITE_DISCOVERY_CLIENT_CLASS_NAME;
import static io.microsphere.spring.cloud.client.discovery.constants.DiscoveryClientConstants.DISCOVERY_CLIENT_CLASS_NAME;
import static io.microsphere.spring.cloud.client.discovery.constants.DiscoveryClientConstants.REACTIVE_COMMONS_CLIENT_AUTO_CONFIGURATION_CLASS_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link DiscoveryClientConstants} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see DiscoveryClientConstants
 * @since 1.0.0
 */
class DiscoveryClientConstantsTest {

    @Test
    void testConstants() {
        assertEquals("org.springframework.cloud.client.discovery.DiscoveryClient", DISCOVERY_CLIENT_CLASS_NAME);
        assertEquals("org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient", COMPOSITE_DISCOVERY_CLIENT_CLASS_NAME);
        assertEquals("org.springframework.cloud.client.CommonsClientAutoConfiguration", COMMONS_CLIENT_AUTO_CONFIGURATION_CLASS_NAME);
        assertEquals("org.springframework.cloud.client.ReactiveCommonsClientAutoConfiguration", REACTIVE_COMMONS_CLIENT_AUTO_CONFIGURATION_CLASS_NAME);
    }
}