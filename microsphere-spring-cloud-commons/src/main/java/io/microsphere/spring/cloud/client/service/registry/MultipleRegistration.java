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
     * Constructs a new {@link MultipleRegistration} wrapping the given collection of
     * {@link Registration} instances. Builds an internal mapping from registration types
     * to their instances and initializes aggregated {@link RegistrationMetaData}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleRegistration registration = new MultipleRegistration(registrations);
     * }</pre>
     *
     * @param registrations the collection of {@link Registration} instances to wrap; must not be empty
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
     * Returns the instance ID of the default {@link Registration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * String instanceId = registration.getInstanceId();
     * }</pre>
     *
     * @return the instance ID
     */
    @Override
    public String getInstanceId() {
        return getDefaultRegistration().getInstanceId();
    }

    /**
     * Returns the service ID of the default {@link Registration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * String serviceId = registration.getServiceId();
     * }</pre>
     *
     * @return the service ID
     */
    @Override
    public String getServiceId() {
        return getDefaultRegistration().getServiceId();
    }

    /**
     * Returns the host of the default {@link Registration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * String host = registration.getHost();
     * }</pre>
     *
     * @return the host name or IP address
     */
    @Override
    public String getHost() {
        return getDefaultRegistration().getHost();
    }

    /**
     * Returns the port of the default {@link Registration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * int port = registration.getPort();
     * }</pre>
     *
     * @return the port number
     */
    @Override
    public int getPort() {
        return getDefaultRegistration().getPort();
    }

    /**
     * Returns whether the default {@link Registration} uses a secure (HTTPS) connection.
     *
     * <p>Example Usage:
     * <pre>{@code
     * boolean secure = registration.isSecure();
     * }</pre>
     *
     * @return {@code true} if the connection is secure
     */
    @Override
    public boolean isSecure() {
        return getDefaultRegistration().isSecure();
    }

    /**
     * Returns the URI of the default {@link Registration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * URI uri = registration.getUri();
     * }</pre>
     *
     * @return the {@link java.net.URI} of the service instance
     */
    @Override
    public URI getUri() {
        return getDefaultRegistration().getUri();
    }

    /**
     * Returns the aggregated {@link RegistrationMetaData} across all wrapped registrations.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Map<String, String> metadata = registration.getMetadata();
     * }</pre>
     *
     * @return the metadata map
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
     * Registration defaultReg = registration.getDefaultRegistration();
     * }</pre>
     *
     * @return the default {@link Registration}
     */
    public Registration getDefaultRegistration() {
        return defaultRegistration;
    }

    public <T extends Registration> T special(Class<T> specialClass) {
        if (Registration.class.equals(specialClass))
            return (T) this;
        return (T) this.registrationMap.getOrDefault(specialClass, null);
    }
}