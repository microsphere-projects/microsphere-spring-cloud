package io.microsphere.spring.cloud.client.service.registry;

import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static io.microsphere.reflect.MethodUtils.invokeMethod;
import static io.microsphere.util.Assert.assertNotEmpty;
import static org.springframework.aop.framework.AopProxyUtils.ultimateTargetClass;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0.0
 */
public final class RegistrationMetaData implements Map<String, String> {

    /**
     * The class name of {@link org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration}
     */
    static final String ZOOKEEPER_REGISTRATION_CLASS_NAME = "org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration";

    /**
     * The method name of {@link org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration#getServiceInstance()}
     */
    static final String GET_SERVICE_INSTANCE_METHOD_NAME = "getServiceInstance";

    /**
     * MetaData information manually added by the application,usually specified by configuration
     */
    private final Map<String, String> applicationMetaData;

    private final Collection<Registration> registrations;

    private final Object lock = new Object();

    public RegistrationMetaData(Collection<Registration> registrations) {
        assertNotEmpty(registrations, () -> "registrations cannot be empty");
        this.registrations = registrations;
        this.applicationMetaData = new ConcurrentHashMap<>();
        for (Registration registration : registrations) {
            initializeIfZookeeperRegistrationAvailable(registration);

            Map<String, String> metaData = registration.getMetadata();
            if (!CollectionUtils.isEmpty(metaData)) {
                //check key and value must not be null
                metaData.forEach((k, v) -> {
                    if (k == null || v == null)
                        return;
                    this.applicationMetaData.put(k, v);
                });
            }
        }
    }

    @Override
    public int size() {
        return applicationMetaData.size();
    }

    @Override
    public boolean isEmpty() {
        return this.applicationMetaData.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.applicationMetaData.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.applicationMetaData.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return this.applicationMetaData.get(key);
    }

    @Override
    public String put(String key, String value) {
        synchronized (lock) {
            this.registrations.forEach(registration -> {
                registration.getMetadata().put(key, value);
            });
        }
        return this.applicationMetaData.put(key, value);
    }

    @Override
    public String remove(Object key) {
        synchronized (lock) {
            this.registrations.forEach(registration -> {
                registration.getMetadata().remove(key);
            });
        }
        return this.applicationMetaData.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        synchronized (lock) {
            this.registrations.forEach(registration -> {
                registration.getMetadata().putAll(m);
            });
        }
        this.applicationMetaData.putAll(m);
    }

    @Override
    public void clear() {
        synchronized (lock) {
            this.registrations.forEach(registration -> registration.getMetadata().clear());
        }
        this.applicationMetaData.clear();
    }

    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.applicationMetaData.keySet());
    }

    @Override
    public Collection<String> values() {
        return Collections.unmodifiableCollection(this.applicationMetaData.values());
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return this.applicationMetaData.entrySet();
    }

    private void initializeIfZookeeperRegistrationAvailable(Registration registration) {
        Class<?> registrationClass = ultimateTargetClass(registration);
        if (ZOOKEEPER_REGISTRATION_CLASS_NAME.equals(registrationClass.getName())) {
            // init ServiceInstance<ZookeeperInstance>
            invokeMethod(registration, GET_SERVICE_INSTANCE_METHOD_NAME);
        }
    }
}
