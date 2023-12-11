package io.microsphere.spring.cloud.client.service.registry;

import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0.0
 */
public class MultipleRegistration implements Registration {

    private Map<Class<? extends Registration>, Registration> registrationMap = new HashMap<>();
    private Registration defaultRegistration;
    private final RegistrationMetaData metaData;

    public MultipleRegistration(Class<? extends Registration> defaultRegistrationClass, Collection<Registration> registrations) {
        if (CollectionUtils.isEmpty(registrations))
            throw new IllegalArgumentException("registrations cannot be empty");
        //init map
        for (Registration registration : registrations) {
            Class<? extends Registration> clazz = registration.getClass();
            if (defaultRegistrationClass.isAssignableFrom(clazz))
                this.defaultRegistration = registration;
            this.registrationMap.put(clazz, registration);
        }

        if (defaultRegistration == null)
            throw new IllegalArgumentException("default registration not specified");
        this.metaData = new RegistrationMetaData(registrations);
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
        if (specialClass.equals(Registration.class))
            return (T) this;


        return (T) this.registrationMap.getOrDefault(specialClass, null);
    }
}
