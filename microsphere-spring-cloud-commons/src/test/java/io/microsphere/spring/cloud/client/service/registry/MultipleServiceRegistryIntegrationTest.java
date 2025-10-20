package io.microsphere.spring.cloud.client.service.registry;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreRegisteredEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.EnabledIfDockerAvailable;

import java.io.File;
import java.net.URL;
import java.util.Map;

import static io.microsphere.lang.function.ThrowableAction.execute;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.containers.wait.strategy.Wait.forLogMessage;

/**
 * {@link MultipleServiceRegistry} Integration Test
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see MultipleServiceRegistry
 * @since 1.0.0
 */
@EnabledIfSystemProperty(named = "testcontainers.enabled", matches = "true")
@EnabledIfDockerAvailable
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        MultipleServiceRegistryIntegrationTest.class,
})
@TestPropertySource(
        properties = {
                "spring.application.name=test",

                "spring.cloud.service-registry.auto-registration.enabled=true",
                "spring.cloud.nacos.discovery.namespace=f7ad23e0-f581-4516-9420-8c50aa6a7b89",
                "spring.cloud.nacos.discovery.metadata.key=value",

                "microsphere.spring.cloud.multiple-registration.enabled=true",
                "microsphere.spring.cloud.default-registration.type=com.alibaba.cloud.nacos.registry.NacosRegistration",
                "microsphere.spring.cloud.default-service-registry.type=com.alibaba.cloud.nacos.registry.NacosServiceRegistry",
        }
)
@EnableAutoConfiguration
public class MultipleServiceRegistryIntegrationTest implements ApplicationListener<RegistrationPreRegisteredEvent> {

    private static ComposeContainer composeContainer;

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
    private AutoServiceRegistrationProperties properties;

    @Autowired
    private Registration registration;

    @Autowired
    private MultipleAutoServiceRegistration autoServiceRegistration;

    @Autowired
    private ConfigurableApplicationContext context;

    @BeforeAll
    static void beforeAll() throws Exception {
        ClassLoader classLoader = MultipleServiceRegistryIntegrationTest.class.getClassLoader();
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

    @BeforeEach
    void setUp() {
        context.addApplicationListener(this);
    }

    @Override
    public void onApplicationEvent(RegistrationPreRegisteredEvent event) {
        onPreRegisteredEvent(event.getRegistration());
    }

    void onPreRegisteredEvent(Registration registration) {
        this.registration.getMetadata().put("my-key", "my-value");
        if (registration instanceof EurekaRegistration) {
            EurekaRegistration eurekaRegistration = (EurekaRegistration) registration;
            ApplicationInfoManager applicationInfoManager = eurekaRegistration.getApplicationInfoManager();
            InstanceInfo instanceInfo = applicationInfoManager.getInfo();
            Map<String, String> metadata = registration.getMetadata();
            // Sync metadata from Registration to InstanceInfo
            instanceInfo.getMetadata().putAll(metadata);
        }
    }

    @Test
    void test() throws Exception {
        assertNotNull(registration);
        execute(autoServiceRegistration::start, e -> {

        });
        assertEquals(registration.getMetadata().get("my-key"), "my-value");
        autoServiceRegistration.stop();
    }
}