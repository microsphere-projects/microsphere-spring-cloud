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

package io.microsphere.spring.cloud.client.service.registry.endpoint;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.cloud.commons.util.UtilAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * {@link ServiceRegistrationEndpoint} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ServiceRegistrationEndpoint
 * @since 1.0.0
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        UtilAutoConfiguration.class,
        SimpleDiscoveryClientAutoConfiguration.class,
        CompositeDiscoveryClientAutoConfiguration.class
})
@TestPropertySource(
        properties = {
                "spring.cloud.discovery.client.simple.instances.test[0].instanceId=1",
                "spring.cloud.discovery.client.simple.instances.test[0].serviceId=test",
                "spring.cloud.discovery.client.simple.instances.test[0].host=127.0.0.1",
                "spring.cloud.discovery.client.simple.instances.test[0].port=8080",
                "spring.cloud.discovery.client.simple.instances.test[0].metadata.key-1=value-1"
        }
)
class ServiceRegistrationEndpointTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void testMetadata() {
    }

    @Test
    void testStart() {
    }
}