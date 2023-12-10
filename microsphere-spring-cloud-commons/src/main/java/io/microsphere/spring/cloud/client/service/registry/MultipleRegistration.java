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
 * @since 1.0
 */
public class MultipleRegistration implements Registration {

    private Map<Class<? extends Registration>, Registration> registrationMap = new HashMap<>();
    private Registration defaultRegistration;

    public MultipleRegistration(Class<? extends Registration> defaultRegistrationClass, Collection<Registration> registrations) {
        if (CollectionUtils.isEmpty(registrations))
            throw new IllegalArgumentException("registrations cannot be empty");
        //init map
        for (Registration registration : registrations) {
            Class<? extends Registration> clazz = registration.getClass();
            if (clazz.equals(defaultRegistrationClass))
                this.defaultRegistration = registration;
            this.registrationMap.put(clazz, registration);
        }

        if (defaultRegistration == null)
            throw new IllegalArgumentException("default registration not specified");
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
        //组合metadata

        Map<String, String> metaData = new HashMap<>();

        for (Registration registration : this.registrationMap.values()) {
            Map<String, String> map = registration.getMetadata();
            //todo 不同实现考虑不同前缀?
            if (!CollectionUtils.isEmpty(map))
                metaData.putAll(map);
        }

        return metaData;
    }

    public Registration getDefaultRegistration() {
        return defaultRegistration;
    }

    public Optional<Registration> special(Class<? extends Registration> specialClass) {
        if (specialClass.equals(Registration.class))
            return Optional.of(this);


        return Optional.ofNullable(this.registrationMap.get(specialClass));
    }
}
