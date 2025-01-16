package io.microsphere.spring.cloud.client.service.registry.autoconfigure;

import io.microsphere.spring.cloud.client.service.registry.DefaultRegistration;
import io.microsphere.spring.cloud.client.service.registry.InMemoryServiceRegistry;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationDeregisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreDeregisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreRegisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import static io.microsphere.spring.cloud.client.service.registry.event.RegistrationEvent.Type.DEREGISTERED;
import static io.microsphere.spring.cloud.client.service.registry.event.RegistrationEvent.Type.PRE_DEREGISTERED;
import static io.microsphere.spring.cloud.client.service.registry.event.RegistrationEvent.Type.PRE_REGISTERED;
import static io.microsphere.spring.cloud.client.service.registry.event.RegistrationEvent.Type.REGISTERED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.aop.support.AopUtils.getTargetClass;

/**
 * {@link ServiceRegistryAutoConfiguration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see ServiceRegistryAutoConfiguration
 * @since 1.0.0
 */
@SpringBootTest(
        classes = {
                InMemoryServiceRegistry.class,
                ServiceRegistryAutoConfiguration.class,
                ServiceRegistryAutoConfigurationTest.class
        }
)
@EnableAspectJAutoProxy
public class ServiceRegistryAutoConfigurationTest {

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private ServiceRegistry serviceRegistry;

    private Registration registration;

    private int count;

    @BeforeEach
    public void init() {
        DefaultRegistration registration = new DefaultRegistration();
        registration.setServiceId("test-service");
        registration.setInstanceId("127.0.0.1:8080");
        this.registration = registration;
    }

    @Test
    public void testEventPublishingRegistrationAspect() {

        context.addApplicationListener(this::onApplicationEvent);

        serviceRegistry.register(registration);

        assertEquals(2, count);

        serviceRegistry.deregister(registration);

        assertEquals(4, count);
    }

    private void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof RegistrationPreRegisteredEvent) {
            onRegistrationPreRegisteredEvent((RegistrationPreRegisteredEvent) event);
        } else if (event instanceof RegistrationRegisteredEvent) {
            onRegistrationRegisteredEvent((RegistrationRegisteredEvent) event);
        } else if (event instanceof RegistrationPreDeregisteredEvent) {
            onRegistrationPreDeregisteredEvent((RegistrationPreDeregisteredEvent) event);
        } else if (event instanceof RegistrationDeregisteredEvent) {
            onRegistrationDeregisteredEvent((RegistrationDeregisteredEvent) event);
        }
    }

    private void onRegistrationPreRegisteredEvent(RegistrationPreRegisteredEvent event) {
        assertRegistrationEvent(event);
        assertTrue(event.isPreRegistered());
        assertFalse(event.isRegistered());
        assertFalse(event.isPreDeregistered());
        assertFalse(event.isDeregistered());
        assertEquals(PRE_REGISTERED, event.getType());
    }

    private void onRegistrationRegisteredEvent(RegistrationRegisteredEvent event) {
        assertRegistrationEvent(event);
        assertFalse(event.isPreRegistered());
        assertTrue(event.isRegistered());
        assertFalse(event.isPreDeregistered());
        assertFalse(event.isDeregistered());
        assertEquals(REGISTERED, event.getType());
    }

    private void onRegistrationPreDeregisteredEvent(RegistrationPreDeregisteredEvent event) {
        assertRegistrationEvent(event);
        assertFalse(event.isPreRegistered());
        assertFalse(event.isRegistered());
        assertTrue(event.isPreDeregistered());
        assertFalse(event.isDeregistered());
        assertEquals(PRE_DEREGISTERED, event.getType());
    }

    private void onRegistrationDeregisteredEvent(RegistrationDeregisteredEvent event) {
        assertRegistrationEvent(event);
        assertFalse(event.isPreRegistered());
        assertFalse(event.isRegistered());
        assertFalse(event.isPreDeregistered());
        assertTrue(event.isDeregistered());
        assertEquals(DEREGISTERED, event.getType());
    }

    private void assertRegistrationEvent(RegistrationEvent event) {
        Registration registration = event.getRegistration();
        assertEquals(this.registration, registration);
        assertSame(this.registration, registration);
        assertSame(getTargetClass(this.serviceRegistry), getTargetClass(event.getRegistry()));
        assertNotNull(event.getSource());
        assertNotNull(event.getType());
        count++;
    }
}