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


import io.microsphere.spring.test.web.controller.TestController;
import io.microsphere.spring.web.metadata.WebEndpointMapping;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.serviceregistry.Registration;

import java.util.Collection;
import java.util.Map;

import static io.microsphere.spring.cloud.client.service.registry.constants.InstanceConstants.WEB_CONTEXT_PATH_METADATA_NAME;
import static io.microsphere.spring.cloud.client.service.registry.constants.InstanceConstants.WEB_MAPPINGS_METADATA_NAME;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtils.getWebEndpointMappings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * {@link WebServiceRegistryAutoConfiguration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see WebServiceRegistryAutoConfiguration
 * @since 1.0.0
 */
@SpringBootTest(
        classes = TestController.class,
        properties = {
                "microsphere.spring.cloud.service-registry.auto-registration.simple.enabled=true",
                "spring.cloud.service-registry.auto-registration.enabled=true"
        },
        webEnvironment = RANDOM_PORT
)
abstract class WebServiceRegistryAutoConfigurationTest {

    @Autowired
    private Registration registration;

    @Test
    void test() {
        Map<String, String> metadata = registration.getMetadata();
        assertEquals("", metadata.get(WEB_CONTEXT_PATH_METADATA_NAME));
        assertNotNull(metadata.get(WEB_MAPPINGS_METADATA_NAME));

        Collection<WebEndpointMapping> webEndpointMappings = getWebEndpointMappings(registration);
        assertTrue(webEndpointMappings.size() >= 6);
    }
}