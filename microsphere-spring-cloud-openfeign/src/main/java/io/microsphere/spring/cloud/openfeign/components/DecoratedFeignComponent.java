package io.microsphere.spring.cloud.openfeign.components;

import io.microsphere.logging.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClientSpecification;
import org.springframework.lang.NonNull;

import java.lang.reflect.Constructor;
import java.util.function.Function;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.reflect.ConstructorUtils.findConstructor;
import static org.springframework.beans.BeanUtils.instantiateClass;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public abstract class DecoratedFeignComponent<T> implements Refreshable {

    protected final Logger logger = getLogger(getClass());

    private final NamedContextFactory<FeignClientSpecification> contextFactory;

    private final String contextId;

    private final FeignClientProperties clientProperties;

    protected volatile T delegate;

    /**
     * Constructs a {@link DecoratedFeignComponent} wrapping the given delegate.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Typically invoked via a subclass constructor
     * super(contextId, contextFactory, clientProperties, delegate);
     * }</pre>
     *
     * @param contextId        the Feign client context ID
     * @param contextFactory   the {@link NamedContextFactory} for resolving per-client contexts
     * @param clientProperties the {@link FeignClientProperties} for configuration lookup
     * @param delegate         the original component to delegate to
     */
    public DecoratedFeignComponent(String contextId, NamedContextFactory<FeignClientSpecification> contextFactory, FeignClientProperties clientProperties, T delegate) {
        this.contextId = contextId;
        this.contextFactory = contextFactory;
        this.clientProperties = clientProperties;
        this.delegate = delegate;
    }

    /**
     * Returns the current delegate instance, lazily loading it from the context factory
     * if it was previously cleared by a {@link #refresh()} call.
     *
     * <p>Example Usage:
     * <pre>{@code
     * T component = decoratedFeignComponent.delegate();
     * }</pre>
     *
     * @return the current delegate instance
     */
    public T delegate() {
        T delegate = this.delegate;
        if (delegate == null) {
            delegate = loadInstance();
            logger.trace("the component[{}] - Creating delegate instance[{}] for contextId: '{}'", componentType(), delegate, contextId);
            this.delegate = delegate;
        }
        return delegate;
    }

    /**
     * Loads a component instance of the given type from the {@link NamedContextFactory},
     * falling back to direct instantiation if the bean is not available.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Decoder decoder = decoratedFeignComponent.loadInstanceFromContextFactory("my-client", Decoder.class);
     * }</pre>
     *
     * @param <T>           the component type
     * @param contextId     the Feign client context ID
     * @param componentType the class of the component to load
     * @return the loaded component instance
     */
    @NonNull
    public <T> T loadInstanceFromContextFactory(String contextId, Class<T> componentType) {
        ObjectProvider<T> beanProvider = this.contextFactory.getProvider(contextId, componentType);
        return beanProvider.getIfAvailable(() -> instantiateClass(componentType));
    }

    /**
     * Returns the Feign client context ID associated with this decorated component.
     *
     * <p>Example Usage:
     * <pre>{@code
     * String id = decoratedFeignComponent.contextId();
     * }</pre>
     *
     * @return the context ID string
     */
    @NonNull
    public String contextId() {
        return this.contextId;
    }

    /**
     * Refreshes this component by clearing the delegate, causing the next call to
     * {@link #delegate()} to reload the instance from the context factory.
     *
     * <p>Example Usage:
     * <pre>{@code
     * decoratedFeignComponent.refresh();
     * }</pre>
     */
    public void refresh() {
        logger.trace("the component[{}] - Refreshing delegate instance[{}] for contextId : '{}'", componentType(), this.delegate, contextId);
        this.delegate = null;
    }

    /**
     * Returns the Feign component type class used to resolve the delegate implementation.
     * Subclasses must implement this to return the appropriate configuration class.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<? extends T> type = decoratedFeignComponent.componentType();
     * }</pre>
     *
     * @return the component type class
     */
    protected abstract Class<? extends T> componentType();

    /**
     * Returns the default {@link FeignClientConfiguration} as defined by the
     * {@link FeignClientProperties#getDefaultConfig()} key.
     *
     * <p>Example Usage:
     * <pre>{@code
     * FeignClientConfiguration defaultConfig = decoratedFeignComponent.getDefaultConfiguration();
     * }</pre>
     *
     * @return the default {@link FeignClientConfiguration}, or {@code null} if not present
     */
    public FeignClientConfiguration getDefaultConfiguration() {
        return this.clientProperties.getConfig().get(this.clientProperties.getDefaultConfig());
    }

    /**
     * Returns the {@link FeignClientConfiguration} for the current Feign client context ID.
     *
     * <p>Example Usage:
     * <pre>{@code
     * FeignClientConfiguration currentConfig = decoratedFeignComponent.getCurrentConfiguration();
     * }</pre>
     *
     * @return the current {@link FeignClientConfiguration}, or {@code null} if not present
     */
    public FeignClientConfiguration getCurrentConfiguration() {
        return this.clientProperties.getConfig().get(contextId);
    }

    /**
     * Retrieves a value from the {@link FeignClientConfiguration} using the provided function,
     * checking the default configuration first and then the current context configuration.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<Decoder> decoderClass = get(FeignClientConfiguration::getDecoder);
     * }</pre>
     *
     * @param <T>                   the value type
     * @param configurationFunction the function to extract a value from the configuration
     * @return the extracted value, or {@code null} if not found in either configuration
     */
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

    /**
     * Loads the delegate instance from the {@link NamedContextFactory} using the
     * component type returned by {@link #componentType()}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * T instance = decoratedFeignComponent.loadInstance();
     * }</pre>
     *
     * @return the loaded delegate instance
     */
    protected T loadInstance() {
        Class<? extends T> componentType = componentType();
        String contextId = contextId();
        return loadInstanceFromContextFactory(contextId, componentType);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return delegate().hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        return delegate().equals(obj);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return delegate().toString();
    }

    /**
     * Factory method to instantiate a {@link DecoratedFeignComponent} subclass by locating
     * the appropriate constructor via reflection.
     *
     * <p>Example Usage:
     * <pre>{@code
     * DecoratedContract contract = DecoratedFeignComponent.instantiate(
     *     DecoratedContract.class, Contract.class,
     *     "my-client", contextFactory, clientProperties, originalContract);
     * }</pre>
     *
     * @param <W>              the decorated component type
     * @param <T>              the Feign component type
     * @param decoratedClass   the {@link DecoratedFeignComponent} subclass to instantiate
     * @param componentClass   the Feign component interface class
     * @param contextId        the Feign client context ID
     * @param contextFactory   the {@link NamedContextFactory} for context resolution
     * @param clientProperties the {@link FeignClientProperties} for configuration
     * @param delegate         the original delegate instance
     * @return a new instance of the decorated component
     */
    public static <W extends DecoratedFeignComponent<T>, T> W instantiate(Class<W> decoratedClass,
                                                                          Class<? extends T> componentClass,
                                                                          String contextId,
                                                                          NamedContextFactory<FeignClientSpecification> contextFactory,
                                                                          FeignClientProperties clientProperties,
                                                                          T delegate) {
        Constructor<W> constructor = findConstructor(decoratedClass, String.class, NamedContextFactory.class, FeignClientProperties.class, componentClass);
        return instantiateClass(constructor, contextId, contextFactory, clientProperties, delegate);
    }
}