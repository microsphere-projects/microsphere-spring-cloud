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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.cloud.commons.util.UtilAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static io.microsphere.collection.Lists.ofList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link UnionDiscoveryClient} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        UtilAutoConfiguration.class,
        SimpleDiscoveryClientAutoConfiguration.class,
        CompositeDiscoveryClientAutoConfiguration.class,
        UnionDiscoveryClient.class,
        UnionDiscoveryClientTest.DummyDiscoveryClient.class,
        UnionDiscoveryClientTest.class
})
@TestPropertySource(
        properties = {
                "spring.cloud.discovery.client.simple.order=-1",
                "spring.cloud.discovery.client.simple.instances.test[0].instanceId=1",
                "spring.cloud.discovery.client.simple.instances.test[0].serviceId=test",
                "spring.cloud.discovery.client.simple.instances.test[0].host=127.0.0.1",
                "spring.cloud.discovery.client.simple.instances.test[0].port=8080",
                "spring.cloud.discovery.client.simple.instances.test[0].metadata.key-1=value-1"
        }
)
class UnionDiscoveryClientTest {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private UnionDiscoveryClient unionDiscoveryClient;

    @Test
    void test() {
        assertEquals(CompositeDiscoveryClient.class, discoveryClient.getClass());
        CompositeDiscoveryClient compositeDiscoveryClient = CompositeDiscoveryClient.class.cast(discoveryClient);
        List<DiscoveryClient> discoveryClients = compositeDiscoveryClient.getDiscoveryClients();
        assertEquals(3, discoveryClients.size());
        assertEquals(UnionDiscoveryClient.class, discoveryClients.get(0).getClass());
        assertEquals(SimpleDiscoveryClient.class, discoveryClients.get(1).getClass());
        assertEquals(DummyDiscoveryClient.class, discoveryClients.get(2).getClass());
    }

    @Test
    void testDescription() {
        assertEquals("Composite Discovery Client", this.discoveryClient.description());
        assertEquals("Union Discovery Client", this.unionDiscoveryClient.description());
    }

    @Test
    void testGetInstances() {
        assertServiceInstances(this.discoveryClient.getInstances("test"));
        assertServiceInstances(this.unionDiscoveryClient.getInstances("test"));

        assertTrue(this.discoveryClient.getInstances("unknown").isEmpty());
        assertTrue(this.unionDiscoveryClient.getInstances("unknown").isEmpty());
    }

    @Test
    void testGetServices() {
        assertServices(this.discoveryClient.getServices());
        assertServices(this.unionDiscoveryClient.getServices());
    }

    void assertServiceInstances(List<ServiceInstance> serviceInstances) {
        assertEquals(1, serviceInstances.size());
        ServiceInstance serviceInstance = serviceInstances.get(0);
        assertEquals("test", serviceInstance.getServiceId());
        assertEquals("1", serviceInstance.getInstanceId());
        assertEquals("127.0.0.1", serviceInstance.getHost());
        assertEquals(8080, serviceInstance.getPort());
        assertEquals("value-1", serviceInstance.getMetadata().get("key-1"));
    }

    void assertServices(List<String> services) {
        assertEquals(ofList("test"), services);
    }

    static class DummyDiscoveryClient implements DiscoveryClient {

        @Override
        public String description() {
            return "Dummy Discovery Client";
        }

        @Override
        public List<ServiceInstance> getInstances(String serviceId) {
            return emptyList();
        }

        @Override
        public List<String> getServices() {
            return emptyList();
        }
    }
}