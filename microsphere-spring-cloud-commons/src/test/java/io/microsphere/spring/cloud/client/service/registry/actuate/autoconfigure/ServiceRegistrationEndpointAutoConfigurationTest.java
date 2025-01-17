package io.microsphere.spring.cloud.client.service.registry.actuate.autoconfigure;

import io.microsphere.spring.cloud.client.service.registry.endpoint.ServiceDeregistrationEndpoint;
import io.microsphere.spring.cloud.client.service.registry.endpoint.ServiceRegistrationEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link ServiceRegistrationEndpointAutoConfiguration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see ServiceRegistrationEndpointAutoConfiguration
 * @since 1.0.0
 */
@SpringBootTest(
        classes = {
                ServiceRegistrationEndpointAutoConfigurationTest.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "microsphere.spring.cloud.service-registry.auto-registration.simple.enabled=true",
                "management.endpoint.serviceRegistration.enabled=true",
                "management.endpoint.serviceDeregistration.enabled=true",
        }
)
@EnableAutoConfiguration
public class ServiceRegistrationEndpointAutoConfigurationTest {

    @Autowired
    private ObjectProvider<ServiceRegistrationEndpoint> serviceRegistrationEndpoint;

    @Autowired
    private ObjectProvider<ServiceDeregistrationEndpoint> serviceDeregistrationEndpoint;

    @Test
    public void testEndpoints() {
        assertNotNull(serviceRegistrationEndpoint);
    }

}