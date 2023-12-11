package io.microsphere.spring.cloud.client.service.registry;

import io.microsphere.spring.cloud.client.service.registry.event.RegistrationDeregisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreDeregisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreRegisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationRegisteredEvent;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.ResolvableType;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0.0
 */
public class MultipleServiceRegistry implements ServiceRegistry<MultipleRegistration>, ApplicationEventPublisherAware {

    private final Collection<ServiceRegistry> serviceRegistries;

    private ServiceRegistry defaultServiceRegistry;

    private final ObjectProvider<RegistrationCustomizer> registrationCustomizers;

    private final Map<Class<? extends Registration>, ServiceRegistry> registryMap = new HashMap<>();

    private ApplicationEventPublisher publisher;

    public MultipleServiceRegistry(Class<? extends ServiceRegistry> defaultServiceRegistryClass,
                                   Collection<ServiceRegistry> serviceRegistries,
                                   ObjectProvider<RegistrationCustomizer> registrationCustomizers) {
        if (CollectionUtils.isEmpty(serviceRegistries))
            throw new IllegalArgumentException("service registry cannot be empty");

        this.serviceRegistries = serviceRegistries;
        this.registrationCustomizers = registrationCustomizers;

        for (ServiceRegistry<? extends Registration> serviceRegistry : serviceRegistries) {
            Class<? extends Registration> registrationClass = getRegistrationClass(serviceRegistry.getClass());
            this.registryMap.put(registrationClass, serviceRegistry);
            if (defaultServiceRegistryClass.isAssignableFrom(serviceRegistry.getClass()))
                defaultServiceRegistry = serviceRegistry;
        }

        if (defaultServiceRegistry == null)
            throw new IllegalArgumentException("default service registry not specified");

    }

    @Override
    public void register(MultipleRegistration registration) {
        this.registryMap.forEach((clazz, serviceRegistry) -> {
            Registration selectedRegistration = registration.special(clazz);
            if (selectedRegistration != null) {
                beforeRegister(serviceRegistry, selectedRegistration);
                serviceRegistry.register(selectedRegistration);
                afterRegister(serviceRegistry, selectedRegistration);
            }

        });
    }

    @Override
    public void deregister(MultipleRegistration registration) {
        this.registryMap.forEach((clazz, serviceRegistry) -> {
            Registration selectedRegistration = registration.special(clazz);
            if (selectedRegistration != null) {
                beforeDeregister(serviceRegistry, selectedRegistration);
                serviceRegistry.deregister(selectedRegistration);
                afterDeregister(serviceRegistry, selectedRegistration);
            }
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
            Registration selectedRegistration = registration.special(clazz);
            if (selectedRegistration != null)
                serviceRegistry.setStatus(selectedRegistration, status);
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

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }


    //intercept register
    protected void beforeRegister(ServiceRegistry registry, Registration registration) {
        this.publisher.publishEvent(new RegistrationPreRegisteredEvent(registry, registration));
        registrationCustomizers.ifAvailable(customizer -> {
            customizer.customize(registration);
        });
    }

    //intercept deregister
    protected void beforeDeregister(ServiceRegistry registry, Registration registration) {
        this.publisher.publishEvent(new RegistrationPreDeregisteredEvent(registry, registration));
    }

    protected void afterRegister(ServiceRegistry registry, Registration registration) {
        this.publisher.publishEvent(new RegistrationRegisteredEvent(registry, registration));
    }

    protected void afterDeregister(ServiceRegistry registry, Registration registration) {
        this.publisher.publishEvent(new RegistrationDeregisteredEvent(registry, registration));
    }
}
