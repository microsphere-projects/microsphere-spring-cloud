package io.microsphere.spring.cloud.client.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ServiceInstancesChangedEvent} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see ServiceInstancesChangedEvent
 * @since 1.0.0
 */
class ServiceInstancesChangedEventTest {

    private String serviceName = "testService";

    private ServiceInstancesChangedEvent event;

    private ServiceInstance instance;

    @BeforeEach
    void setUp() {
        this.instance = createInstance(serviceName);
        this.event = new ServiceInstancesChangedEvent(serviceName, Arrays.asList(instance));
    }

    private ServiceInstance createInstance(String serviceName) {
        DefaultServiceInstance instance = new DefaultServiceInstance();
        instance.setServiceId(serviceName);
        instance.setServiceId(UUID.randomUUID().toString());
        instance.setHost("127.0.0.1");
        instance.setPort(8080);
        instance.setUri(URI.create("http://127.0.0.1:8080/info"));
        return instance;
    }

    @Test
    void testGetServiceName() {
        assertEquals(this.serviceName, this.event.getServiceName());
        assertEquals(this.serviceName, this.event.getSource());
    }

    @Test
    void testGetServiceInstances() {
        assertEquals(Arrays.asList(this.instance), this.event.getServiceInstances());
        assertEquals(this.instance, this.event.getServiceInstances().get(0));
        assertSame(this.instance, this.event.getServiceInstances().get(0));
    }

    @Test
    void testProcessed() {
        assertFalse(this.event.isProcessed());
        this.event.processed();
        assertTrue(this.event.isProcessed());
    }
}