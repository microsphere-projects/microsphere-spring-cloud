package io.microsphere.spring.cloud.client.service.registry.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;

/**
 * The {@link Endpoint @Endpoint} for Service Deregistration
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see AbstractAutoServiceRegistration
 * @see Endpoint
 * @since 1.0.0
 */
@Endpoint(id = "serviceDeregistration")
public class ServiceDeregistrationEndpoint extends AbstractServiceRegistrationEndpoint {

    /**
     * Deregisters the service from the {@link ServiceRegistry} and returns whether
     * the service was previously running. If the service is not running, the
     * deregistration is skipped and a warning is logged.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Via actuator endpoint: POST /actuator/serviceDeregistration
     * boolean wasRunning = endpoint.stop();
     * }</pre>
     *
     * @return {@code true} if the service was running and has been deregistered,
     *         {@code false} if the service was not running
     */
    @WriteOperation
    public boolean stop() {
        boolean isRunning = isRunning();
        if (isRunning) {
            serviceRegistry.deregister(registration);
            logger.info("Service[name : '{}'] is deregistered!", applicationName);
            setRunning(false);
        } else {
            logger.warn("Service[name : '{}'] is not registered, deregistration can't be executed!", applicationName);
        }
        return isRunning;
    }
}