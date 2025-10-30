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
package io.microsphere.spring.cloud.client.discovery;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.EnabledIfDockerAvailable;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.containers.wait.strategy.Wait.forLogMessage;

/**
 * {@link UnionDiscoveryClient} Integration Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@EnabledIfSystemProperty(named = "testcontainers.enabled", matches = "true")
@EnabledIfDockerAvailable
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        UnionDiscoveryClientIntegrationTest.class
})
@TestPropertySource(properties = {
        "spring.application.name=test",
        "spring.cloud.service-registry.auto-registration.enabled=true",
        "microsphere.spring.cloud.client.discovery.mode=union",
        "microsphere.spring.cloud.multiple-registration.enabled=true"
})
@EnableAutoConfiguration
class UnionDiscoveryClientIntegrationTest {

    private static ComposeContainer composeContainer;

    @Autowired
    private DiscoveryClient discoveryClient;

    @BeforeAll
    static void beforeAll() throws Exception {
        ClassLoader classLoader = UnionDiscoveryClientIntegrationTest.class.getClassLoader();
        URL resource = classLoader.getResource("META-INF/docker/service-registry-servers.yml");
        File dockerComposeFile = new File(resource.toURI());
        composeContainer = new ComposeContainer(dockerComposeFile);
        composeContainer.waitingFor("nacos", forLogMessage(".*Nacos started successfully.*", 1))
                .waitingFor("eureka", forLogMessage(".*Started EurekaServerApplication.*", 1))
                .start();
    }

    @AfterAll
    static void afterAll() {
        composeContainer.stop();
    }

    @Test
    void test() {
        assertEquals(CompositeDiscoveryClient.class, discoveryClient.getClass());
        CompositeDiscoveryClient compositeDiscoveryClient = CompositeDiscoveryClient.class.cast(discoveryClient);
        List<DiscoveryClient> discoveryClients = compositeDiscoveryClient.getDiscoveryClients();
        assertEquals(7, discoveryClients.size());
        assertEquals(UnionDiscoveryClient.class, discoveryClients.get(0).getClass());
        List<String> services = compositeDiscoveryClient.getServices();
        assertTrue(services.size() > 1);
    }
}
