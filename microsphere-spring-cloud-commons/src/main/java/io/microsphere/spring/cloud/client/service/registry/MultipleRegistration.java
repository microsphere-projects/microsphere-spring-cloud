package io.microsphere.spring.cloud.client.service.registry;

import org.springframework.cloud.client.serviceregistry.Registration;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static io.microsphere.util.Assert.assertNotEmpty;

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

    public MultipleRegistration(Collection<Registration> registrations) {
        assertNotEmpty(registrations, () -> "registrations cannot be empty");
        //init map
        for (Registration registration : registrations) {
            Class<? extends Registration> clazz = registration.getClass();
            this.registrationMap.put(clazz, registration);
            this.defaultRegistration = registration;
        }
        this.metaData = new RegistrationMetaData(registrations);
    }

    @Override
    public String getInstanceId() {
        return getDefaultRegistration().getInstanceId();
    }

    @Override
    public String getServiceId() {
        return getDefaultRegistration().getServiceId();
    }

    @Override
    public String getHost() {
        return getDefaultRegistration().getHost();
    }

    @Override
    public int getPort() {
        return getDefaultRegistration().getPort();
    }

    @Override
    public boolean isSecure() {
        return getDefaultRegistration().isSecure();
    }

    @Override
    public URI getUri() {
        return getDefaultRegistration().getUri();
    }

    @Override
    public Map<String, String> getMetadata() {
        return metaData;
    }

    public Registration getDefaultRegistration() {
        return defaultRegistration;
    }

    public <T extends Registration> T special(Class<T> specialClass) {
        if (Registration.class.equals(specialClass))
            return (T) this;
        return (T) this.registrationMap.getOrDefault(specialClass, null);
    }
}
