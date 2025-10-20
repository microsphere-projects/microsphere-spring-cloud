package io.microsphere.spring.cloud.client.service.registry.autoconfigure;

import io.microsphere.spring.cloud.client.service.registry.SimpleAutoServiceRegistration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link SimpleAutoServiceRegistrationAutoConfiguration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see SimpleAutoServiceRegistrationAutoConfiguration
 * @since 1.0.0
 */
@SpringBootTest(
        classes = {
                SimpleAutoServiceRegistrationAutoConfigurationTest.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "microsphere.spring.cloud.service-registry.auto-registration.simple.enabled=true",
                "spring.application.name=test-service"
        }
)
@EnableAutoConfiguration
class SimpleAutoServiceRegistrationAutoConfigurationTest {

    @Autowired
    private Registration registration;

    @Autowired
    private ServiceRegistry<Registration> serviceRegistry;

    @Autowired
    private SimpleAutoServiceRegistration simpleAutoServiceRegistration;

    @Test
    void test() {
        assertEquals("test-service", registration.getServiceId());
        assertNotNull(registration.getHost());
        assertNotNull(registration.getPort());
        assertNotNull(registration.getUri());
        assertNotNull(registration.getInstanceId());
        assertNotNull(registration.getMetadata());
    }

}