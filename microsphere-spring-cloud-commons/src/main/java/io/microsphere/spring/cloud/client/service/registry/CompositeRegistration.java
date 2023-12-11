package io.microsphere.spring.cloud.client.service.registry;

import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The Composite {@link Registration}
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class CompositeRegistration implements Registration {

    private final Map<String, Registration> registrationsMap;

    private final Map<Class<? extends Registration>, Registration> typeToInstancesMap;

    private final RegistrationMetaData metaData;

    private Registration defaultRegistration;

    public CompositeRegistration(Map<String, Registration> registrationsMap) {
        if (CollectionUtils.isEmpty(registrationsMap))
            throw new IllegalArgumentException("registrations cannot be empty");
        //init map
        this.registrationsMap = registrationsMap;
        this.typeToInstancesMap = new HashMap<>(registrationsMap.size());
        this.metaData = new RegistrationMetaData(registrationsMap.values());
        for (Map.Entry<String, Registration> entry : registrationsMap.entrySet()) {
            String beanName = entry.getKey();
            Registration registration = entry.getValue();
            Class<? extends Registration> registrationClass = registration.getClass();
            typeToInstancesMap.put(registrationClass, registration);
            this.defaultRegistration = registration;
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
        return metaData;
    }

    public Registration getDefaultRegistration() {
        return defaultRegistration;
    }

    public <T extends Registration> T special(Class<T> targetRegistrationClass) {
        if (targetRegistrationClass.equals(Registration.class))
            return (T) this;

        return (T) this.typeToInstancesMap.get(targetRegistrationClass);
    }
}
