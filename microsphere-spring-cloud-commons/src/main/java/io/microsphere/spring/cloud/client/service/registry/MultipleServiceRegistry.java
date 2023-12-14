package io.microsphere.spring.cloud.client.service.registry;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.springframework.core.io.support.SpringFactoriesLoader.loadFactoryNames;
import static org.springframework.util.ClassUtils.resolveClassName;

/**
 * The Delegating {@link ServiceRegistry} for the multiple service registration
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see MultipleRegistration
 * @see MultipleAutoServiceRegistration
 * @since 1.0.0
 */
public class MultipleServiceRegistry implements ServiceRegistry<MultipleRegistration> {

    private final Map<String, ServiceRegistry> registriesMap;

    /**
     * Spring Bean Name -> Registration.class
     */
    private final Map<String, Class<? extends Registration>> beanNameToRegistrationTypesMap;

    private ServiceRegistry defaultServiceRegistry;
    private String defaultRegistrationBeanName;

    public MultipleServiceRegistry(Map<String, ServiceRegistry> registriesMap) {
        if (CollectionUtils.isEmpty(registriesMap)) {
            throw new IllegalArgumentException("service registry cannot be empty");
        }

        this.registriesMap = registriesMap;
        this.beanNameToRegistrationTypesMap = new HashMap<>(registriesMap.size());

        for (Map.Entry<String, ServiceRegistry> entry : registriesMap.entrySet()) {
            String beanName = entry.getKey();
            ServiceRegistry serviceRegistry = entry.getValue();
            Class<? extends Registration> registrationClass = getRegistrationClass(serviceRegistry.getClass(), entry.getValue());
            beanNameToRegistrationTypesMap.put(beanName, registrationClass);
            defaultServiceRegistry = serviceRegistry;
            defaultRegistrationBeanName = beanName;
        }
    }

    @Override
    public void register(MultipleRegistration registration) {
        iterate(registration, (reg, registry) -> registry.register(reg));
    }

    @Override
    public void deregister(MultipleRegistration registration) {
        iterate(registration, (reg, registry) -> registry.deregister(reg));
    }

    @Override
    public void close() {
        iterate(ServiceRegistry::close);
    }

    @Override
    public void setStatus(MultipleRegistration registration, String status) {
        iterate(registration, (reg, registry) -> registry.setStatus(reg, status));
    }

    private void iterate(MultipleRegistration registration, BiConsumer<Registration, ServiceRegistry> action) {
        registriesMap.forEach((beanName, registry) -> {
            Class<? extends Registration> registrationClass = beanNameToRegistrationTypesMap.get(beanName);
            Registration targetRegistration = registration.special(registrationClass);
            if (targetRegistration != null) {
                action.accept(targetRegistration, registry);
            }
        });
    }

    private void iterate(Consumer<ServiceRegistry> action) {
        registriesMap.values().forEach(action);
    }

    @Override
    public <T> T getStatus(MultipleRegistration registration) {
        Class<? extends Registration> registrationClass = beanNameToRegistrationTypesMap.get(defaultRegistrationBeanName);
        Registration targetRegistration = registration.special(registrationClass);
        return (T) defaultServiceRegistry.getStatus(targetRegistration);
    }

    private static Class<? extends Registration> getRegistrationClass(Class<? extends ServiceRegistry> serviceRegistryClass, ServiceRegistry serviceRegistry) {
        Class<?> registrationClass = ResolvableType.forClass(serviceRegistryClass)
                .as(ServiceRegistry.class)
                .getGeneric(0)
                .resolve();

        if (Registration.class.equals(registrationClass)) { // If the class is not the subclass of Registration
            // The configured class will try to be loaded based on SpringFactoriesLoader
            ClassLoader classLoader = serviceRegistryClass.getClassLoader();
            List<String> registrationClassNames;

            if (AopUtils.isAopProxy(serviceRegistry)) {
                registrationClassNames = loadFactoryNames(AopProxyUtils.ultimateTargetClass(serviceRegistry), classLoader);
            } else {
                registrationClassNames = loadFactoryNames(serviceRegistryClass, classLoader);
            }

            for (String registrationClassName : registrationClassNames) {
                registrationClass = resolveClassName(registrationClassName, classLoader);
            }
        }
        return (Class<? extends Registration>) registrationClass;
    }
}
