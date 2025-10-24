package io.microsphere.spring.cloud.openfeign.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClientSpecification;
import org.springframework.lang.NonNull;

import java.lang.reflect.Constructor;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public abstract class DecoratedFeignComponent<T> implements Refreshable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    //private final FeignClientFactory feignClientFactory;
    private final NamedContextFactory<FeignClientSpecification> contextFactory;
    private final String contextId;

    private final FeignClientProperties clientProperties;


    protected volatile T delegate;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public DecoratedFeignComponent(String contextId, NamedContextFactory<FeignClientSpecification> contextFactory, FeignClientProperties clientProperties, T delegate) {
        this.contextId = contextId;
        this.contextFactory = contextFactory;
        this.clientProperties = clientProperties;
        this.delegate = delegate;
    }

    public T delegate() {
        readLock.lock();
        if (delegate == null) {
            log.trace("the component {} - Creating delegate instance for contextId: {}", componentType().getSimpleName(), contextId);
            readLock.unlock();
            return loadInstance();
        }

        readLock.unlock();
        return this.delegate;
    }

    @NonNull
    public <T> T loadInstanceFromContextFactory(String contextId, Class<T> componentType) {
        T component = this.contextFactory.getInstance(contextId, componentType);
        if (component == null)
            return this.contextFactory.getParent().getAutowireCapableBeanFactory().createBean(componentType);
        return component;
    }

    @NonNull
    public String contextId() {
        return this.contextId;
    }

    public void refresh() {
        writeLock.lock();
        log.debug("the component {} - Refreshing delegate instance for contextId: {}", componentType().getSimpleName(), contextId);
        this.delegate = null;
        writeLock.unlock();
    }

    protected abstract Class<T> componentType();

    public FeignClientConfiguration getDefaultConfiguration() {
        return this.clientProperties.getConfig().get(this.clientProperties.getDefaultConfig());
    }

    public FeignClientConfiguration getCurrentConfiguration() {
        return this.clientProperties.getConfig().get(contextId);
    }

    protected <T> T get(Function<FeignClientConfiguration, T> configurationFunction) {
        FeignClientConfiguration config = getDefaultConfiguration();
        T value = null;
        if (config != null) {
            value = configurationFunction.apply(config);
        }
        if (value == null) {
            config = getCurrentConfiguration();
            if (config != null) {
                value = configurationFunction.apply(config);
            }
        }
        return value;
    }

    protected T loadInstance() {
        Class<T> componentType = componentType();
        String contextId = contextId();
        writeLock.lock();
        try {
            T component = loadInstanceFromContextFactory(contextId, componentType);
            this.delegate = component;
            return component;
        } catch (Throwable ex) {
            this.delegate = BeanUtils.instantiateClass(componentType);
            return delegate;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public int hashCode() {
        return delegate().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate().equals(obj);
    }

    @Override
    public String toString() {
        return delegate().toString();
    }

    public static <W extends DecoratedFeignComponent<T>, T> W instantiate(Class<W> decoratedClass, Class<T> componentClass,
                                                                          String contextId, NamedContextFactory<FeignClientSpecification> contextFactory, FeignClientProperties clientProperties, T delegate) {
        try {
            Constructor<W> constructor = decoratedClass.getConstructor(String.class, NamedContextFactory.class, FeignClientProperties.class, componentClass);
            return BeanUtils.instantiateClass(constructor, contextId, contextFactory, clientProperties, delegate);
        } catch (NoSuchMethodException noSuchMethodException) {
            throw new BeanInstantiationException(decoratedClass, noSuchMethodException.getLocalizedMessage());
        }

    }
}
