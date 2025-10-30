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