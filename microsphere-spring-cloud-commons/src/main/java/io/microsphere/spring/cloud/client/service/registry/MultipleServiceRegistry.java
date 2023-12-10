package io.microsphere.spring.cloud.client.service.registry;

import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
public class MultipleServiceRegistry implements ServiceRegistry<MultipleRegistration> {

    private final Collection<ServiceRegistry> serviceRegistries;

    private ServiceRegistry defaultServiceRegistry;

    private final Map<Class<? extends Registration>, ServiceRegistry> registryMap = new HashMap<>();

    public MultipleServiceRegistry(Class<? extends ServiceRegistry> defaultServiceRegistryClass, Collection<ServiceRegistry> serviceRegistries) {
        if (CollectionUtils.isEmpty(serviceRegistries))
            throw new IllegalArgumentException("service registry cannot be empty");

        this.serviceRegistries = serviceRegistries;

        for (ServiceRegistry<? extends Registration> serviceRegistry : serviceRegistries) {
            Class<? extends Registration> registrationClass = getRegistrationClass(serviceRegistry.getClass());
            this.registryMap.put(registrationClass, serviceRegistry);
            if (defaultServiceRegistryClass.equals(serviceRegistry.getClass()))
                defaultServiceRegistry = serviceRegistry;
        }

        if (defaultServiceRegistry == null)
            throw new IllegalArgumentException("default service registry not specified");

    }

    @Override
    public void register(MultipleRegistration registration) {
        this.registryMap.forEach((clazz, serviceRegistry) -> {
            Optional<Registration> selectedRegistration = registration.special(clazz);
            selectedRegistration.ifPresent(serviceRegistry::register);
        });
    }

    @Override
    public void deregister(MultipleRegistration registration) {
        this.registryMap.forEach((clazz, serviceRegistry) -> {
            Optional<Registration> selectedRegistration = registration.special(clazz);
            selectedRegistration.ifPresent(serviceRegistry::deregister);
        });
    }

    @Override
    public void close() {
        for (ServiceRegistry serviceRegistry : this.serviceRegistries)
            serviceRegistry.close();
    }

    @Override
    public void setStatus(MultipleRegistration registration, String status) {
        this.registryMap.forEach((clazz, serviceRegistry) -> {
            Optional<Registration> selectedRegistration = registration.special(clazz);
            if (selectedRegistration.isPresent())
                serviceRegistry.setStatus(registration, status);
        });
    }

    @Override
    public <T> T getStatus(MultipleRegistration registration) {
        return (T) defaultServiceRegistry.getStatus(registration.getDefaultRegistration());
    }

    private static Class<? extends Registration> getRegistrationClass(Class<? extends ServiceRegistry> serviceRegistryClass) {
        ResolvableType resolvableType = ResolvableType.forClass(serviceRegistryClass);
        return (Class<? extends Registration>)resolvableType.getInterfaces()[0].getGeneric(0).getRawClass();
    }
}
