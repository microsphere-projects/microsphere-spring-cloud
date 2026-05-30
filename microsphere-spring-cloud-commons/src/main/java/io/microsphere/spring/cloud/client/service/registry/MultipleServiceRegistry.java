package io.microsphere.spring.cloud.client.service.registry;

import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.microsphere.util.Assert.assertNotEmpty;
import static org.springframework.aop.framework.AopProxyUtils.ultimateTargetClass;
import static org.springframework.core.ResolvableType.forClass;
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

    /**
     * Constructs a new {@link MultipleServiceRegistry} from the given map of bean names
     * to {@link ServiceRegistry} instances. Each registry is mapped to its corresponding
     * {@link Registration} type.
     *
     * <p>Example Usage:
     * <pre>{@code
     * ServiceRegistry<DefaultRegistration> simpleRegistry = new InMemoryServiceRegistry();
     * MultipleServiceRegistry registry =
     *     new MultipleServiceRegistry(Map.of("default", simpleRegistry));
     * }</pre>
     *
     * @param registriesMap the map of Spring bean names to {@link ServiceRegistry} instances,
     *                      must not be empty
     */
    public MultipleServiceRegistry(Map<String, ServiceRegistry> registriesMap) {
        assertNotEmpty(registriesMap, () -> "registrations cannot be empty");

        this.registriesMap = registriesMap;
        this.beanNameToRegistrationTypesMap = new HashMap<>(registriesMap.size());

        for (Map.Entry<String, ServiceRegistry> entry : registriesMap.entrySet()) {
            String beanName = entry.getKey();
            ServiceRegistry serviceRegistry = entry.getValue();
            Class<? extends Registration> registrationClass = getRegistrationClass(ultimateTargetClass(serviceRegistry));
            beanNameToRegistrationTypesMap.put(beanName, registrationClass);
            defaultServiceRegistry = serviceRegistry;
            defaultRegistrationBeanName = beanName;
        }
    }

    /**
     * Registers the given {@link MultipleRegistration} by delegating to each underlying
     * {@link ServiceRegistry} with the corresponding specific {@link Registration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleServiceRegistry registry = new MultipleServiceRegistry(registriesMap);
     * MultipleRegistration registration = new MultipleRegistration(registrations);
     * registry.register(registration);
     * }</pre>
     *
     * @param registration the {@link MultipleRegistration} to register
     */
    @Override
    public void register(MultipleRegistration registration) {
        iterate(registration, (reg, registry) -> registry.register(reg));
    }

    /**
     * Deregisters the given {@link MultipleRegistration} by delegating to each underlying
     * {@link ServiceRegistry} with the corresponding specific {@link Registration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleServiceRegistry registry = new MultipleServiceRegistry(registriesMap);
     * MultipleRegistration registration = new MultipleRegistration(registrations);
     * registry.register(registration);
     * registry.deregister(registration);
     * }</pre>
     *
     * @param registration the {@link MultipleRegistration} to deregister
     */
    @Override
    public void deregister(MultipleRegistration registration) {
        iterate(registration, (reg, registry) -> registry.deregister(reg));
    }

    /**
     * Closes all underlying {@link ServiceRegistry} instances.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleServiceRegistry registry = new MultipleServiceRegistry(registriesMap);
     * registry.close();
     * }</pre>
     */
    @Override
    public void close() {
        iterate(ServiceRegistry::close);
    }

    /**
     * Sets the status of the given {@link MultipleRegistration} by delegating to each
     * underlying {@link ServiceRegistry} with the corresponding specific {@link Registration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleServiceRegistry registry = new MultipleServiceRegistry(registriesMap);
     * registry.register(registration);
     * registry.setStatus(registration, "UP");
     * }</pre>
     *
     * @param registration the {@link MultipleRegistration} whose status is to be set
     * @param status       the status value to set
     */
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

    /**
     * Retrieves the status of the given {@link MultipleRegistration} from the default
     * {@link ServiceRegistry}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleServiceRegistry registry = new MultipleServiceRegistry(registriesMap);
     * registry.register(registration);
     * registry.setStatus(registration, "UP");
     * Object status = registry.getStatus(registration); // "UP"
     * }</pre>
     *
     * @param registration the {@link MultipleRegistration} whose status is to be retrieved
     * @param <T>          the type of the status value
     * @return the status from the default service registry
     */
    @Override
    public <T> T getStatus(MultipleRegistration registration) {
        Class<? extends Registration> registrationClass = beanNameToRegistrationTypesMap.get(defaultRegistrationBeanName);
        Registration targetRegistration = registration.special(registrationClass);
        return (T) defaultServiceRegistry.getStatus(targetRegistration);
    }

    /**
     * Resolves the {@link Registration} class for the given {@link ServiceRegistry} class
     * using generic type resolution. Falls back to {@code SpringFactoriesLoader} when the
     * generic type resolves to {@link Registration} itself.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<? extends Registration> regClass =
     *     MultipleServiceRegistry.getRegistrationClass(NacosServiceRegistry.class);
     * // returns NacosRegistration.class
     * }</pre>
     *
     * @param serviceRegistryClass the {@link ServiceRegistry} implementation class
     * @return the resolved {@link Registration} subclass
     */
    static Class<? extends Registration> getRegistrationClass(Class<?> serviceRegistryClass) {
        Class<?> registrationClass = forClass(serviceRegistryClass)
                .as(ServiceRegistry.class)
                .getGeneric(0)
                .resolve();

        if (Registration.class.equals(registrationClass)) { // If the class is not the subclass of Registration
            // The configured class will try to be loaded based on SpringFactoriesLoader
            ClassLoader classLoader = serviceRegistryClass.getClassLoader();
            List<String> registrationClassNames;

            registrationClassNames = loadFactoryNames(serviceRegistryClass, classLoader);

            for (String registrationClassName : registrationClassNames) {
                registrationClass = resolveClassName(registrationClassName, classLoader);
            }
        }
        return (Class<? extends Registration>) registrationClass;
    }
}