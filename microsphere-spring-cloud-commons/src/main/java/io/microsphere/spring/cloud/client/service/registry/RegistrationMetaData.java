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

    /**
     * Constructs a new {@link RegistrationMetaData} that aggregates metadata from the given
     * collection of {@link Registration} instances. Metadata changes are synchronized across
     * all registrations.
     *
     * <p>Example Usage:
     * <pre>{@code
     * DefaultRegistration registration = new DefaultRegistration();
     * registration.getMetadata().put("key1", "value1");
     * RegistrationMetaData metaData = new RegistrationMetaData(List.of(registration));
     * }</pre>
     *
     * @param registrations the collection of {@link Registration} instances, must not be empty
     */
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
     * RegistrationMetaData metaData = new RegistrationMetaData(registrations);
     * int count = metaData.size(); // e.g. 3
     * }</pre>
     *
     * @return the number of key-value mappings in this metadata
     */
    @Override
    public int size() {
        return applicationMetaData.size();
    }

    /**
     * Returns whether this metadata map is empty.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RegistrationMetaData metaData = new RegistrationMetaData(registrations);
     * boolean empty = metaData.isEmpty(); // false if registrations have metadata
     * }</pre>
     *
     * @return {@code true} if this metadata contains no entries
     */
    @Override
    public boolean isEmpty() {
        return this.applicationMetaData.isEmpty();
    }

    /**
     * Returns whether this metadata contains the specified key.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RegistrationMetaData metaData = new RegistrationMetaData(registrations);
     * boolean hasKey = metaData.containsKey("key1"); // true
     * boolean missing = metaData.containsKey("unknown"); // false
     * }</pre>
     *
     * @param key the key to check for
     * @return {@code true} if this metadata contains the specified key
     */
    @Override
    public boolean containsKey(Object key) {
        return this.applicationMetaData.containsKey(key);
    }

    /**
     * Returns whether this metadata contains the specified value.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RegistrationMetaData metaData = new RegistrationMetaData(registrations);
     * boolean hasValue = metaData.containsValue("value1"); // true
     * boolean missing = metaData.containsValue("unknown"); // false
     * }</pre>
     *
     * @param value the value to check for
     * @return {@code true} if this metadata contains the specified value
     */
    @Override
    public boolean containsValue(Object value) {
        return this.applicationMetaData.containsValue(value);
    }

    /**
     * Returns the metadata value associated with the specified key.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RegistrationMetaData metaData = new RegistrationMetaData(registrations);
     * String value = metaData.get("key1"); // "value1"
     * String missing = metaData.get("unknown"); // null
     * }</pre>
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the key, or {@code null} if not found
     */
    @Override
    public String get(Object key) {
        return this.applicationMetaData.get(key);
    }

    /**
     * Puts a metadata entry and synchronizes it across all underlying {@link Registration}
     * instances.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RegistrationMetaData metaData = new RegistrationMetaData(registrations);
     * metaData.put("key4", "value4");
     * String value = metaData.get("key4"); // "value4"
     * }</pre>
     *
     * @param key   the metadata key
     * @param value the metadata value
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
     * Removes the metadata entry for the specified key and synchronizes the removal
     * across all underlying {@link Registration} instances.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RegistrationMetaData metaData = new RegistrationMetaData(registrations);
     * metaData.remove("key1");
     * String value = metaData.get("key1"); // null
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
     * Copies all entries from the specified map into this metadata and synchronizes
     * them across all underlying {@link Registration} instances.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RegistrationMetaData metaData = new RegistrationMetaData(registrations);
     * metaData.putAll(Map.of("key4", "value4", "key5", "value5"));
     * }</pre>
     *
     * @param m the map of entries to add
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
     * Clears all metadata entries and synchronizes the clearing across all underlying
     * {@link Registration} instances.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RegistrationMetaData metaData = new RegistrationMetaData(registrations);
     * metaData.clear();
     * int size = metaData.size(); // 0
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
     * Returns an unmodifiable {@link Set} view of the metadata keys.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RegistrationMetaData metaData = new RegistrationMetaData(registrations);
     * Set<String> keys = metaData.keySet();
     * boolean hasKey = keys.contains("key1"); // true
     * }</pre>
     *
     * @return an unmodifiable set of metadata keys
     */
    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.applicationMetaData.keySet());
    }

    /**
     * Returns an unmodifiable {@link Collection} view of the metadata values.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RegistrationMetaData metaData = new RegistrationMetaData(registrations);
     * Collection<String> values = metaData.values();
     * boolean hasValue = values.contains("value1"); // true
     * }</pre>
     *
     * @return an unmodifiable collection of metadata values
     */
    @Override
    public Collection<String> values() {
        return Collections.unmodifiableCollection(this.applicationMetaData.values());
    }

    /**
     * Returns a modifiable {@link Set} view of the metadata entries. Unlike
     * {@link #keySet()} and {@link #values()}, the returned set is not wrapped
     * in an unmodifiable view.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RegistrationMetaData metaData = new RegistrationMetaData(registrations);
     * Set<Map.Entry<String, String>> entries = metaData.entrySet();
     * }</pre>
     *
     * @return a set of metadata entries
     */
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