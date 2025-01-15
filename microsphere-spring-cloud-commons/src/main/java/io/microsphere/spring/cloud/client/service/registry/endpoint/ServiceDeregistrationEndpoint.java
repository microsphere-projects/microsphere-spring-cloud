package io.microsphere.spring.cloud.client.service.registry.endpoint;

import io.microsphere.logging.Logger;
import io.microsphere.logging.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @WriteOperation
    public boolean stop() {
        boolean isRunning = isRunning();
        if (isRunning) {
            serviceRegistry.deregister(registration);
            if(logger.isInfoEnabled()) {
                logger.info("Service[name : '{}'] is deregistered!", applicationName);
            }
            setRunning(false);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("Service[name : '{}'] is not registered, deregistration can't be executed!", applicationName);
            }
        }
        return isRunning;
    }
}
