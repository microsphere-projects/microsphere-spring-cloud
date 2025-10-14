package io.microsphere.spring.cloud.client.service.registry;

import com.alibaba.cloud.nacos.NacosServiceAutoConfiguration;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration;
import com.alibaba.cloud.nacos.registry.NacosServiceRegistryAutoConfiguration;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import io.microsphere.spring.cloud.client.service.registry.autoconfigure.ServiceRegistryAutoConfiguration;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreRegisteredEvent;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
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

@Disabled
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AutoServiceRegistrationAutoConfiguration.class,
        CommonsClientAutoConfiguration.class,
        EurekaClientAutoConfiguration.class,
        DiscoveryClientOptionalArgsConfiguration.class,
        NacosServiceRegistryAutoConfiguration.class,
        NacosServiceAutoConfiguration.class,
        NacosDiscoveryAutoConfiguration.class,
        UtilAutoConfiguration.class,
        MultipleServiceRegistryTest.class,
        ServiceRegistryAutoConfiguration.class,
})
@TestPropertySource(
        properties = {
                "spring.application.name=test",
                "microsphere.spring.cloud.multiple-registration.enabled=true",
                "microsphere.spring.cloud.default-registration.type=com.alibaba.cloud.nacos.registry.NacosRegistration",
                "microsphere.spring.cloud.default-service-registry.type=com.alibaba.cloud.nacos.registry.NacosServiceRegistry",
                "spring.cloud.service-registry.auto-registration.enabled=true",
                "spring.cloud.nacos.discovery.namespace=f7ad23e0-f581-4516-9420-8c50aa6a7b89",
                "spring.cloud.nacos.discovery.metadata.key=value",
                "eureka.client.service-url.defaultZone=http://127.0.0.1:8080/eureka",
        }
)
@EnableAutoConfiguration
class MultipleServiceRegistryTest implements ApplicationListener<RegistrationPreRegisteredEvent> {

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
    private AutoServiceRegistrationProperties properties;

    @Autowired
    private Registration registration;

    @Autowired
    private MultipleAutoServiceRegistration autoServiceRegistration;

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
    void test() throws Exception {
        assertNotNull(serviceRegistry);
        assertNotNull(registration);
        autoServiceRegistration.start();
        Thread.sleep(60 * 1000);

        autoServiceRegistration.stop();
    }

    @Test
    void testMetaData() throws Exception {
        assertNotNull(registration);

        autoServiceRegistration.start();

        assertEquals(registration.getMetadata().get("my-key"), "my-value");
        Thread.sleep(60 * 1000);

        autoServiceRegistration.stop();
    }


}