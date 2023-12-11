package io.microsphere.spring.cloud.client.service.registry;

import io.microsphere.spring.cloud.client.service.registry.event.RegistrationDeregisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreDeregisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreRegisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationRegisteredEvent;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.ResolvableType;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Composite {@link ServiceRegistry}
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class CompositeServiceRegistry implements ServiceRegistry<CompositeRegistration>, ApplicationEventPublisherAware {

    private final ObjectProvider<RegistrationCustomizer> registrationCustomizers;

    private final Map<String, ServiceRegistry> registriesMap;

    /**
     * Spring Bean Name -> Registration.class
     */
    private final Map<String, Class<? extends Registration>> beanNameToRegistrationTypesMap;

    private ApplicationEventPublisher publisher;

    public CompositeServiceRegistry(Map<String, ServiceRegistry> registriesMap,
                                    ObjectProvider<RegistrationCustomizer> registrationCustomizers) {
        if (CollectionUtils.isEmpty(registriesMap))
            throw new IllegalArgumentException("service registry cannot be empty");
        this.registriesMap = registriesMap;
        this.beanNameToRegistrationTypesMap = new HashMap<>();
        this.registriesMap.forEach((beanName, serviceRegistry) -> {
            Class<? extends ServiceRegistry> serviceRegistryClass = serviceRegistry.getClass();
            Class<? extends Registration> registrationClass = getRegistrationClass(serviceRegistryClass);
            beanNameToRegistrationTypesMap.put(beanName, registrationClass);
        });
        this.registrationCustomizers = registrationCustomizers;
    }

    @Override
    public void register(CompositeRegistration registration) {
        this.registriesMap.forEach((beanName, serviceRegistry) -> {
            Class<? extends Registration> registrationClass = beanNameToRegistrationTypesMap.get(beanName);
            Registration selectedRegistration = registration.special(registrationClass);
            if (selectedRegistration != null) {
                beforeRegister(serviceRegistry, selectedRegistration);
                serviceRegistry.register(selectedRegistration);
                afterRegister(serviceRegistry, selectedRegistration);
            }
        });
    }

    @Override
    public void deregister(CompositeRegistration registration) {
        this.registriesMap.forEach((beanName, serviceRegistry) -> {
            Class<? extends Registration> registrationClass = beanNameToRegistrationTypesMap.get(beanName);
            Registration selectedRegistration = registration.special(registrationClass);
            if (selectedRegistration != null) {
                beforeDeregister(serviceRegistry, selectedRegistration);
                serviceRegistry.deregister(selectedRegistration);
                afterDeregister(serviceRegistry, selectedRegistration);
            }
        });
    }

    @Override
    public void close() {
        for (ServiceRegistry serviceRegistry : this.registriesMap.values())
            serviceRegistry.close();
    }

    @Override
    public void setStatus(CompositeRegistration registration, String status) {
        this.registriesMap.forEach((beanName, serviceRegistry) -> {
            Class<? extends Registration> registrationClass = (Class) beanNameToRegistrationTypesMap.get(beanName);
            Registration selectedRegistration = registration.special(registrationClass);
            if (selectedRegistration != null)
                serviceRegistry.setStatus(selectedRegistration, status);
        });
    }

    @Override
    public <T> T getStatus(CompositeRegistration registration) {
        // TODO;
        return null;
        // return (T) defaultServiceRegistry.getStatus(registration.getDefaultRegistration());
    }

    private static Class<? extends Registration> getRegistrationClass(Class<? extends ServiceRegistry> serviceRegistryClass) {
        return (Class<? extends Registration>) ResolvableType.forClass(serviceRegistryClass)
                .as(ServiceRegistry.class)
                .getGeneric(0)
                .resolve();
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
