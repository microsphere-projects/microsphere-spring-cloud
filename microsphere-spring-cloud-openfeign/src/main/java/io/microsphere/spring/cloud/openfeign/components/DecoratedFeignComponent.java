package io.microsphere.spring.cloud.openfeign.components;

import io.microsphere.logging.Logger;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.lang.NonNull;

import java.lang.reflect.Constructor;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.function.Function;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static org.springframework.beans.BeanUtils.instantiateClass;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public abstract class DecoratedFeignComponent<T> implements Refreshable {

    protected final Logger logger = getLogger(getClass());

    private final FeignContext feignContext;

    private final String contextId;

    private final FeignClientProperties clientProperties;

    protected volatile T delegate;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final ReadLock readLock = lock.readLock();

    private final WriteLock writeLock = lock.writeLock();

    public DecoratedFeignComponent(String contextId, FeignContext feignContext, FeignClientProperties clientProperties, T delegate) {
        this.contextId = contextId;
        this.feignContext = feignContext;
        this.clientProperties = clientProperties;
        this.delegate = delegate;
    }

    public T delegate() {
        readLock.lock();
        if (delegate == null) {
            logger.trace("the component {} - Creating delegate instance for contextId: {}", componentType().getSimpleName(), contextId);
            readLock.unlock();
            return loadInstance();
        }

        readLock.unlock();
        return this.delegate;
    }

    @NonNull
    public <T> T loadInstanceFromContextFactory(String contextId, Class<T> componentType) {
        ObjectProvider<T> beanProvider = this.feignContext.getProvider(contextId, componentType);
        return beanProvider.getIfAvailable(() -> instantiateClass(componentType));
    }

    @NonNull
    public FeignContext getFeignContext() {
        return this.feignContext;
    }

    @NonNull
    public String contextId() {
        return this.contextId;
    }

    public void refresh() {
        writeLock.lock();
        logger.debug("the component {} - Refreshing delegate instance for contextId: {}", componentType().getSimpleName(), contextId);
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
        T bean = null;
        writeLock.lock();
        try {
            bean = loadInstanceFromContextFactory(contextId, componentType);
        } finally {
            this.delegate = bean;
            writeLock.unlock();
        }
        return bean;
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
                                                                          String contextId, FeignContext feignContext, FeignClientProperties clientProperties, T delegate) {
        try {
            Constructor<W> constructor = decoratedClass.getConstructor(String.class, FeignContext.class, FeignClientProperties.class, componentClass);
            return instantiateClass(constructor, contextId, feignContext, clientProperties, delegate);
        } catch (NoSuchMethodException noSuchMethodException) {
            throw new BeanInstantiationException(decoratedClass, noSuchMethodException.getLocalizedMessage());
        }
    }
}