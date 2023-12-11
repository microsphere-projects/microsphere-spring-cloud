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

import com.alibaba.cloud.nacos.NacosServiceAutoConfiguration;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration;
import com.alibaba.cloud.nacos.registry.NacosServiceRegistryAutoConfiguration;
import com.alibaba.cloud.nacos.util.UtilIPv6AutoConfiguration;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import io.microsphere.spring.cloud.client.service.registry.CompositeAutoServiceRegistration;
import io.microsphere.spring.cloud.client.service.registry.MultipleAutoServiceRegistration;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreRegisteredEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationAutoConfiguration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.cloud.commons.util.UtilAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.context.ApplicationListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AutoServiceRegistrationAutoConfiguration.class,
        ServiceRegistryAutoConfiguration.class,
        CommonsClientAutoConfiguration.class,
        UtilIPv6AutoConfiguration.class,
        UtilAutoConfiguration.class,
        // Eureka Client
        EurekaClientAutoConfiguration.class,
        DiscoveryClientOptionalArgsConfiguration.class,
        // Nacos Discovery
        NacosServiceRegistryAutoConfiguration.class,
        NacosServiceAutoConfiguration.class,
        NacosDiscoveryAutoConfiguration.class,
        // Simple
        SimpleAutoServiceRegistrationAutoConfiguration.class,
        // Composite
        CompositeServiceRegistryAutoConfiguration.class,
        CompositeServiceRegistryAutoConfigurationTest.class,
})
@TestPropertySource(
        properties = {
                "spring.application.name=test",
                "spring.cloud.service-registry.auto-registration.enabled=true",

                "microsphere.spring.cloud.composite-registration.enabled=true",
                // Nacos Discovery
                "spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848",
                "spring.cloud.nacos.discovery.username=nacos",
                "spring.cloud.nacos.discovery.password=nacos",
                "spring.cloud.nacos.discovery.metadata.key=value",
                // Eureka Client
                "eureka.client.service-url.defaultZone=http://127.0.0.1:12345/eureka",
        }
)

public class CompositeServiceRegistryAutoConfigurationTest implements ApplicationListener<RegistrationPreRegisteredEvent> {


    @Autowired
    private ServiceRegistry serviceRegistry;
    @Autowired
    private AutoServiceRegistrationProperties properties;
    @Autowired
    private Registration registration;

    @Autowired
    private AbstractAutoServiceRegistration autoServiceRegistration;

    @Override
    public void onApplicationEvent(RegistrationPreRegisteredEvent event) {
        this.registration.getMetadata().put("my-key", "my-value");
        if (event.getRegistration() instanceof EurekaRegistration) {
            EurekaRegistration eurekaRegistration = (EurekaRegistration) event.getRegistration();
            ApplicationInfoManager applicationInfoManager = eurekaRegistration.getApplicationInfoManager();
            InstanceInfo instanceInfo = applicationInfoManager.getInfo();
            Map<String, String> metadata = registration.getMetadata();
            // Sync metadata from Registration to InstanceInfo
            instanceInfo.getMetadata().putAll(metadata);
        }
    }

    @Test
    public void test() throws Exception {
        assertNotNull(serviceRegistry);
        assertNotNull(registration);
        autoServiceRegistration.start();
        Thread.sleep(60 * 1000);

        autoServiceRegistration.stop();
    }

    @Test
    public void testMetaData() throws Exception {
        assertNotNull(registration);

        autoServiceRegistration.start();

        assertEquals(registration.getMetadata().get("my-key"), "my-value");
        Thread.sleep(60 * 1000);

        autoServiceRegistration.stop();
    }
}
