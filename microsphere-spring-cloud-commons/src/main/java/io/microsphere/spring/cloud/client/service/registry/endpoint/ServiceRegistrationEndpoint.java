package io.microsphere.spring.cloud.client.service.registry.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.microsphere.lang.function.ThrowableSupplier.execute;
import static io.microsphere.reflect.MethodUtils.invokeMethod;

/**
 * The {@link Endpoint @Endpoint} for Service Registration
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see AbstractAutoServiceRegistration
 * @see Endpoint
 * @since 1.0.0
 */
@Endpoint(id = "serviceRegistration")
public class ServiceRegistrationEndpoint extends AbstractServiceRegistrationEndpoint {

    /**
     * Returns metadata about the current service registration, including application name,
     * registration details, port, status, and running state.
     * This is a read operation exposed via the {@code /actuator/serviceRegistration} endpoint.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Via actuator HTTP GET to /actuator/serviceRegistration
     * ServiceRegistrationEndpoint endpoint = context.getBean(ServiceRegistrationEndpoint.class);
     * Map<String, Object> metadata = endpoint.metadata();
     * String appName = (String) metadata.get("application-name");
     * }</pre>
     *
     * @return a {@link Map} containing service registration metadata
     */
    @ReadOperation
    public Map<String, Object> metadata() {
        Map<String, Object> metadata = new LinkedHashMap<>(16);
        metadata.put("application-name", applicationName);
        metadata.put("registration", registration);
        metadata.put("port", port);
        metadata.put("status", serviceRegistry.getStatus(registration));
        metadata.put("running", isRunning());
        metadata.put("enabled", invoke("isEnabled"));
        metadata.put("phase", serviceRegistration.getPhase());
        metadata.put("order", serviceRegistration.getOrder());
        metadata.put("managementRegistration", invoke("getManagementRegistration"));
        metadata.put("config", invoke("getConfiguration"));
        return metadata;
    }

    /**
     * Registers the service with the {@link ServiceRegistry} if it is not already running.
     * This is a write operation exposed via the {@code /actuator/serviceRegistration} endpoint.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Via actuator HTTP POST to /actuator/serviceRegistration
     * ServiceRegistrationEndpoint endpoint = context.getBean(ServiceRegistrationEndpoint.class);
     * boolean wasAlreadyRunning = endpoint.start();
     * }</pre>
     *
     * @return {@code true} if the service was already running, {@code false} if it was newly registered
     */
    @WriteOperation
    public boolean start() {
        boolean isRunning = isRunning();
        if (!isRunning) {
            serviceRegistry.register(registration);
            setRunning(true);
            logger.info("Service[name : '{}'] is registered!", applicationName);
        } else {
            logger.warn("Service[name : '{}'] was registered!", applicationName);
        }
        return isRunning;
    }

    private Object invoke(String methodName) {
        return execute(() -> invokeMethod(serviceRegistration, methodName), e -> null);
    }
}