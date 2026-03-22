package io.microsphere.spring.cloud.client.service.registry;

import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static io.microsphere.reflect.MethodUtils.invokeMethod;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtils.removeMetadata;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtils.setMetadata;
import static io.microsphere.util.Assert.assertNotEmpty;
import static org.springframework.aop.framework.AopProxyUtils.ultimateTargetClass;

/**
 * A {@link Map}-based metadata container for {@link Registration} instances that synchronizes
 * metadata changes across all underlying registrations. This class wraps one or more
 * {@link Registration} objects and ensures that any metadata modifications (put, remove, clear)
 * are propagated to every registration in a thread-safe manner.
 *
 * <p>Example Usage:
 * <pre>{@code
 * RegistrationMetaData metaData = new RegistrationMetaData(ofList(defaultRegistration));
 * metaData.put("key", "value");
 * String value = metaData.get("key");
 * metaData.remove("key");
 * }</pre>
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @see Registration
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

    /**
     * Returns the number of metadata entries.
     *
     * <p>Example Usage:
     * <pre>{@code
     * int count = metaData.size();
     * }</pre>
     *
     * @return the number of key-value mappings in this metadata
     */
    @Override
    public int size() {
        return applicationMetaData.size();
    }

    /**
     * Returns {@code true} if this metadata contains no entries.
     *
     * <p>Example Usage:
     * <pre>{@code
     * boolean empty = metaData.isEmpty();
     * }</pre>
     *
     * @return {@code true} if this metadata contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        return this.applicationMetaData.isEmpty();
    }

    /**
     * Returns {@code true} if this metadata contains a mapping for the specified key.
     *
     * <p>Example Usage:
     * <pre>{@code
     * boolean exists = metaData.containsKey("key");
     * }</pre>
     *
     * @param key the key whose presence in this metadata is to be tested
     * @return {@code true} if this metadata contains a mapping for the specified key
     */
    @Override
    public boolean containsKey(Object key) {
        return this.applicationMetaData.containsKey(key);
    }

    /**
     * Returns {@code true} if this metadata maps one or more keys to the specified value.
     *
     * <p>Example Usage:
     * <pre>{@code
     * boolean hasValue = metaData.containsValue("value");
     * }</pre>
     *
     * @param value the value whose presence in this metadata is to be tested
     * @return {@code true} if this metadata maps one or more keys to the specified value
     */
    @Override
    public boolean containsValue(Object value) {
        return this.applicationMetaData.containsValue(value);
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if this
     * metadata contains no mapping for the key.
     *
     * <p>Example Usage:
     * <pre>{@code
     * String value = metaData.get("key");
     * }</pre>
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or {@code null}
     */
    @Override
    public String get(Object key) {
        return this.applicationMetaData.get(key);
    }

    /**
     * Associates the specified value with the specified key in this metadata and
     * propagates the change to all underlying {@link Registration} instances.
     *
     * <p>Example Usage:
     * <pre>{@code
     * metaData.put("key", "value");
     * }</pre>
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with the key, or {@code null}
     */
    @Override
    public String put(String key, String value) {
        synchronized (lock) {
            this.registrations.forEach(registration -> {
                setMetadata(registration, key, value);
            });
        }
        return this.applicationMetaData.put(key, value);
    }

    /**
     * Removes the mapping for the specified key from this metadata and propagates the
     * removal to all underlying {@link Registration} instances.
     *
     * <p>Example Usage:
     * <pre>{@code
     * metaData.remove("key");
     * }</pre>
     *
     * @param key the key whose mapping is to be removed
     * @return the previous value associated with the key, or {@code null}
     */
    @Override
    public String remove(Object key) {
        synchronized (lock) {
            this.registrations.forEach(registration -> {
                removeMetadata(registration, (String) key);
            });
        }
        return this.applicationMetaData.remove(key);
    }

    /**
     * Copies all of the mappings from the specified map to this metadata and propagates
     * the changes to all underlying {@link Registration} instances.
     *
     * <p>Example Usage:
     * <pre>{@code
     * metaData.putAll(Map.of("key1", "value1", "key2", "value2"));
     * }</pre>
     *
     * @param m the mappings to be stored in this metadata
     */
    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        synchronized (lock) {
            this.registrations.forEach(registration -> {
                registration.getMetadata().putAll(m);
            });
        }
        this.applicationMetaData.putAll(m);
    }

    /**
     * Removes all metadata entries and clears metadata from all underlying
     * {@link Registration} instances.
     *
     * <p>Example Usage:
     * <pre>{@code
     * metaData.clear();
     * }</pre>
     */
    @Override
    public void clear() {
        synchronized (lock) {
            this.registrations.forEach(registration -> registration.getMetadata().clear());
        }
        this.applicationMetaData.clear();
    }

    /**
     * Returns an unmodifiable {@link Set} view of the keys contained in this metadata.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Set<String> keys = metaData.keySet();
     * }</pre>
     *
     * @return an unmodifiable set view of the keys contained in this metadata
     */
    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.applicationMetaData.keySet());
    }

    /**
     * Returns an unmodifiable {@link Collection} view of the values contained in this metadata.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Collection<String> vals = metaData.values();
     * }</pre>
     *
     * @return an unmodifiable collection view of the values contained in this metadata
     */
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