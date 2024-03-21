package io.microsphere.spring.cloud.client.service.registry.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ReadOperation
    public Map<String, Object> metadata() {

        Map<String, Object> metadata = new LinkedHashMap<>();

        metadata.put("application-name", applicationName);
        metadata.put("registration", registration);
        metadata.put("port", port);
        metadata.put("status", serviceRegistry.getStatus(registration));
        metadata.put("running", isRunning());

        if (serviceRegistration != null) {
            metadata.put("enabled", invoke("isEnabled"));
            metadata.put("phase", serviceRegistration.getPhase());
            metadata.put("order", serviceRegistration.getOrder());
            if (Boolean.TRUE.equals(invoke("shouldRegisterManagement"))) {
                metadata.put("managementRegistration", invoke("getManagementRegistration"));
            }
            metadata.put("config", invoke("getConfiguration"));
        }

        return metadata;
    }

    @WriteOperation
    public boolean start() {
        boolean isRunning = isRunning();
        if (!isRunning) {
            serviceRegistry.register(registration);
            setRunning(true);
            logger.debug("Service[name : '{}'] is registered!", applicationName);
        } else {
            logger.warn("Service[name : '{}'] was registered!", applicationName);
        }
        return isRunning;
    }

    private Object invoke(String methodName) {
        Object returnValue = null;
        try {
            Class<?> serviceRegistrationClass = AbstractAutoServiceRegistration.class;
            Method method = serviceRegistrationClass.getDeclaredMethod(methodName);
            ReflectionUtils.makeAccessible(method);
            returnValue = method.invoke(serviceRegistration);
        } catch (Throwable e) {
            logger.error("Invocation on method ：" + methodName + "is failed", e);
        }
        return returnValue;
    }
}
