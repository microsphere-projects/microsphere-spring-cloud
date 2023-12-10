package io.microsphere.spring.cloud.client.service.registry;

import com.alibaba.cloud.nacos.NacosServiceAutoConfiguration;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration;
import com.alibaba.cloud.nacos.registry.NacosServiceRegistryAutoConfiguration;
import com.alibaba.cloud.nacos.util.UtilIPv6AutoConfiguration;
import io.microsphere.spring.cloud.client.service.registry.autoconfigure.ServiceRegistryAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.serviceregistry.*;
import org.springframework.cloud.commons.util.UtilAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AutoServiceRegistrationConfiguration.class,
        AutoServiceRegistrationAutoConfiguration.class,
        NacosServiceRegistryAutoConfiguration.class,
        NacosServiceAutoConfiguration.class,
        NacosDiscoveryAutoConfiguration.class,
        UtilIPv6AutoConfiguration.class,
        UtilAutoConfiguration.class,
        MultipleServiceRegistryTest.class,
        ServiceRegistryAutoConfiguration.class
})
@TestPropertySource(
        properties = {
                "spring.application.name=test",
                "microsphere.spring.cloud.multiple-registration.enabled=true",
                "spring.cloud.service-registry.auto-registration.enabled=true",
                "spring.cloud.nacos.discovery.namespace=f7ad23e0-f581-4516-9420-8c50aa6a7b89",
        }
)
class MultipleServiceRegistryTest {


    @Autowired
    private ServiceRegistry serviceRegistry;
    @Autowired
    private AutoServiceRegistrationProperties properties;
    @Autowired
    private Registration registration;

    @Test
    public void test() {
        assertNotNull(serviceRegistry);
        assertNotNull(registration);

        serviceRegistry.register(registration);

        while (true) {

        }
    }


}