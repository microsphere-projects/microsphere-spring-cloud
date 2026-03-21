package io.microsphere.spring.cloud.client.service.registry;

import org.springframework.cloud.client.serviceregistry.Registration;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.microsphere.util.Assert.assertNotEmpty;
import static io.microsphere.util.ClassUtils.findAllClasses;
import static io.microsphere.util.ClassUtils.isAssignableFrom;
import static org.springframework.aop.framework.AopProxyUtils.ultimateTargetClass;

/**
 * The Delegating {@link Registration} for the multiple service registration
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @see MultipleAutoServiceRegistration
 * @see MultipleServiceRegistry
 * @since 1.0.0
 */
public class MultipleRegistration implements Registration {

    private Map<Class<? extends Registration>, Registration> registrationMap = new HashMap<>();

    private Registration defaultRegistration;

    private final RegistrationMetaData metaData;

    /**
     * Constructs a new {@link MultipleRegistration} from the given collection of
     * {@link Registration} instances. The last registration in the collection becomes
     * the default registration.
     *
     * <p>Example Usage:
     * <pre>{@code
     * DefaultRegistration registration = new DefaultRegistration();
     * registration.setServiceId("test-service");
     * MultipleRegistration multipleRegistration =
     *     new MultipleRegistration(List.of(registration));
     * }</pre>
     *
     * @param registrations the collection of {@link Registration} instances, must not be empty
     */
    public MultipleRegistration(Collection<Registration> registrations) {
        assertNotEmpty(registrations, () -> "registrations cannot be empty");
        //init map
        for (Registration registration : registrations) {
            Class<? extends Registration> clazz = (Class) ultimateTargetClass(registration);
            List<Class<? extends Registration>> classes = (List) findAllClasses(clazz, type -> isAssignableFrom(Registration.class, type) && !Registration.class.equals(type));
            classes.forEach(type -> registrationMap.put(type, registration));
            this.defaultRegistration = registration;
        }
        this.metaData = new RegistrationMetaData(registrations);
    }

    /**
     * Returns the instance ID from the default {@link Registration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleRegistration multipleRegistration = new MultipleRegistration(registrations);
     * String instanceId = multipleRegistration.getInstanceId();
     * }</pre>
     *
     * @return the instance ID of the default registration
     */
    @Override
    public String getInstanceId() {
        return getDefaultRegistration().getInstanceId();
    }

    /**
     * Returns the service ID from the default {@link Registration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleRegistration multipleRegistration = new MultipleRegistration(registrations);
     * String serviceId = multipleRegistration.getServiceId();
     * }</pre>
     *
     * @return the service ID of the default registration
     */
    @Override
    public String getServiceId() {
        return getDefaultRegistration().getServiceId();
    }

    /**
     * Returns the host from the default {@link Registration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleRegistration multipleRegistration = new MultipleRegistration(registrations);
     * String host = multipleRegistration.getHost();
     * }</pre>
     *
     * @return the host of the default registration
     */
    @Override
    public String getHost() {
        return getDefaultRegistration().getHost();
    }

    /**
     * Returns the port from the default {@link Registration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleRegistration multipleRegistration = new MultipleRegistration(registrations);
     * int port = multipleRegistration.getPort();
     * }</pre>
     *
     * @return the port of the default registration
     */
    @Override
    public int getPort() {
        return getDefaultRegistration().getPort();
    }

    /**
     * Returns whether the default {@link Registration} is secure.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleRegistration multipleRegistration = new MultipleRegistration(registrations);
     * boolean secure = multipleRegistration.isSecure();
     * }</pre>
     *
     * @return {@code true} if the default registration is secure
     */
    @Override
    public boolean isSecure() {
        return getDefaultRegistration().isSecure();
    }

    /**
     * Returns the {@link URI} from the default {@link Registration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleRegistration multipleRegistration = new MultipleRegistration(registrations);
     * URI uri = multipleRegistration.getUri();
     * }</pre>
     *
     * @return the URI of the default registration
     */
    @Override
    public URI getUri() {
        return getDefaultRegistration().getUri();
    }

    /**
     * Returns the aggregated {@link RegistrationMetaData} that synchronizes metadata
     * across all underlying {@link Registration} instances.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleRegistration multipleRegistration = new MultipleRegistration(registrations);
     * Map<String, String> metadata = multipleRegistration.getMetadata();
     * }</pre>
     *
     * @return the aggregated metadata map
     */
    @Override
    public Map<String, String> getMetadata() {
        return metaData;
    }

    /**
     * Returns the default {@link Registration} instance, which is the last registration
     * provided during construction.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleRegistration multipleRegistration = new MultipleRegistration(registrations);
     * Registration defaultReg = multipleRegistration.getDefaultRegistration();
     * }</pre>
     *
     * @return the default {@link Registration}
     */
    public Registration getDefaultRegistration() {
        return defaultRegistration;
    }

    /**
     * Retrieves a specific {@link Registration} by its class type. If the specified
     * class is {@link Registration} itself, returns this {@link MultipleRegistration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleRegistration multipleRegistration = new MultipleRegistration(registrations);
     * DefaultRegistration specific = multipleRegistration.special(DefaultRegistration.class);
     * Registration self = multipleRegistration.special(Registration.class);
     * }</pre>
     *
     * @param specialClass the specific {@link Registration} subclass to look up
     * @param <T>          the type of the registration
     * @return the matching registration, or {@code null} if not found
     */
    public <T extends Registration> T special(Class<T> specialClass) {
        if (Registration.class.equals(specialClass))
            return (T) this;
        return (T) this.registrationMap.getOrDefault(specialClass, null);
    }
}