package io.microsphere.spring.cloud.openfeign.autorefresh;

import feign.Capability;
import feign.Contract;
import feign.QueryMapEncoder;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import io.microsphere.spring.cloud.openfeign.components.DecoratedContract;
import io.microsphere.spring.cloud.openfeign.components.DecoratedDecoder;
import io.microsphere.spring.cloud.openfeign.components.DecoratedEncoder;
import io.microsphere.spring.cloud.openfeign.components.DecoratedErrorDecoder;
import io.microsphere.spring.cloud.openfeign.components.DecoratedQueryMapEncoder;
import io.microsphere.spring.cloud.openfeign.components.DecoratedRetryer;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientSpecification;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static io.microsphere.spring.cloud.openfeign.components.DecoratedFeignComponent.instantiate;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class AutoRefreshCapability implements Capability, ApplicationContextAware {

    private final FeignComponentRegistry componentRegistry;

    private final NamedContextFactory<FeignClientSpecification> contextFactory;

    private final FeignClientProperties clientProperties;

    private String contextId;

    /**
     * Constructs an {@link AutoRefreshCapability} with the required dependencies.
     *
     * <p>Example Usage:
     * <pre>{@code
     * AutoRefreshCapability capability = new AutoRefreshCapability(
     *     clientProperties, contextFactory, componentRegistry);
     * }</pre>
     *
     * @param clientProperties  the {@link FeignClientProperties} providing Feign client configuration
     * @param contextFactory    the {@link NamedContextFactory} for resolving per-client contexts
     * @param componentRegistry the {@link FeignComponentRegistry} to register decorated components
     */
    public AutoRefreshCapability(FeignClientProperties clientProperties,
                                 NamedContextFactory<FeignClientSpecification> contextFactory,
                                 FeignComponentRegistry componentRegistry) {
        this.clientProperties = clientProperties;
        this.contextFactory = contextFactory;
        this.componentRegistry = componentRegistry;
    }

    /**
     * Sets the {@link ApplicationContext} and extracts the Feign client context ID
     * from the {@code spring.cloud.openfeign.client.name} property.
     *
     * <p>Example Usage:
     * <pre>{@code
     * AutoRefreshCapability capability = new AutoRefreshCapability(props, factory, registry);
     * capability.setApplicationContext(applicationContext);
     * }</pre>
     *
     * @param applicationContext the {@link ApplicationContext} for this Feign client
     * @throws BeansException if the context cannot be set
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.contextId = applicationContext.getEnvironment().getProperty("spring.cloud.openfeign.client.name");
    }

    /**
     * Enriches the given {@link Retryer} by wrapping it in a {@link DecoratedRetryer}
     * that supports auto-refresh on configuration changes.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Retryer original = new Retryer.Default();
     * Retryer enriched = capability.enrich(original);
     * }</pre>
     *
     * @param retryer the original {@link Retryer} to enrich, or {@code null}
     * @return the decorated {@link Retryer}, or {@code null} if the input is {@code null}
     */
    @Override
    public Retryer enrich(Retryer retryer) {
        if (retryer == null) {
            return null;
        }

        DecoratedRetryer decoratedRetryer = instantiate(DecoratedRetryer.class, Retryer.class,
                contextId, contextFactory, clientProperties, retryer);
        this.componentRegistry.register(contextId, decoratedRetryer);
        return decoratedRetryer;
    }

    /**
     * Enriches the given {@link Contract} by wrapping it in a {@link DecoratedContract}
     * that supports auto-refresh on configuration changes.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Contract original = new Contract.Default();
     * Contract enriched = capability.enrich(original);
     * }</pre>
     *
     * @param contract the original {@link Contract} to enrich, or {@code null}
     * @return the decorated {@link Contract}, or {@code null} if the input is {@code null}
     */
    @Override
    public Contract enrich(Contract contract) {
        if (contract == null) {
            return null;
        }

        DecoratedContract decoratedContract = instantiate(DecoratedContract.class, Contract.class,
                contextId, contextFactory, clientProperties, contract);
        this.componentRegistry.register(contextId, decoratedContract);
        return decoratedContract;
    }

    /**
     * Enriches the given {@link Decoder} by wrapping it in a {@link DecoratedDecoder}
     * that supports auto-refresh on configuration changes.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Decoder original = new Decoder.Default();
     * Decoder enriched = capability.enrich(original);
     * }</pre>
     *
     * @param decoder the original {@link Decoder} to enrich, or {@code null}
     * @return the decorated {@link Decoder}, or {@code null} if the input is {@code null}
     */
    @Override
    public Decoder enrich(Decoder decoder) {
        if (decoder == null) {
            return null;
        }

        DecoratedDecoder decoratedDecoder = instantiate(DecoratedDecoder.class, Decoder.class,
                contextId, contextFactory, clientProperties, decoder);
        this.componentRegistry.register(contextId, decoratedDecoder);
        return decoratedDecoder;
    }

    /**
     * Enriches the given {@link Encoder} by wrapping it in a {@link DecoratedEncoder}
     * that supports auto-refresh on configuration changes.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Encoder original = new Encoder.Default();
     * Encoder enriched = capability.enrich(original);
     * }</pre>
     *
     * @param encoder the original {@link Encoder} to enrich, or {@code null}
     * @return the decorated {@link Encoder}, or {@code null} if the input is {@code null}
     */
    @Override
    public Encoder enrich(Encoder encoder) {
        if (encoder == null) {
            return null;
        }

        DecoratedEncoder decoratedEncoder = instantiate(DecoratedEncoder.class, Encoder.class,
                contextId, contextFactory, clientProperties, encoder);
        this.componentRegistry.register(contextId, decoratedEncoder);
        return decoratedEncoder;
    }

    /**
     * Enriches the given {@link ErrorDecoder} by wrapping it in a {@link DecoratedErrorDecoder}
     * that supports auto-refresh on configuration changes.
     *
     * <p>Example Usage:
     * <pre>{@code
     * ErrorDecoder original = new ErrorDecoder.Default();
     * ErrorDecoder enriched = capability.enrich(original);
     * }</pre>
     *
     * @param decoder the original {@link ErrorDecoder} to enrich, or {@code null}
     * @return the decorated {@link ErrorDecoder}, or {@code null} if the input is {@code null}
     */
    public ErrorDecoder enrich(ErrorDecoder decoder) {
        if (decoder == null) {
            return null;
        }

        DecoratedErrorDecoder decoratedErrorDecoder = instantiate(DecoratedErrorDecoder.class, ErrorDecoder.class,
                contextId, contextFactory, clientProperties, decoder);
        this.componentRegistry.register(contextId, decoratedErrorDecoder);
        return decoratedErrorDecoder;
    }

    /**
     * Enriches the given {@link RequestInterceptor} by registering it in the
     * {@link FeignComponentRegistry} as part of a {@link io.microsphere.spring.cloud.openfeign.components.CompositedRequestInterceptor}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RequestInterceptor original = template -> template.header("X-Custom", "value");
     * RequestInterceptor enriched = capability.enrich(original);
     * }</pre>
     *
     * @param requestInterceptor the original {@link RequestInterceptor} to enrich, or {@code null}
     * @return the composited {@link RequestInterceptor}, or {@code null} if the input is {@code null}
     */
    @Override
    public RequestInterceptor enrich(RequestInterceptor requestInterceptor) {
        if (requestInterceptor == null) {
            return null;
        }
        return this.componentRegistry.registerRequestInterceptor(contextId, requestInterceptor);
    }

    /**
     * Enriches the given {@link QueryMapEncoder} by wrapping it in a {@link DecoratedQueryMapEncoder}
     * that supports auto-refresh on configuration changes.
     *
     * <p>Example Usage:
     * <pre>{@code
     * QueryMapEncoder original = new QueryMapEncoder.Default();
     * QueryMapEncoder enriched = capability.enrich(original);
     * }</pre>
     *
     * @param queryMapEncoder the original {@link QueryMapEncoder} to enrich, or {@code null}
     * @return the decorated {@link QueryMapEncoder}, or {@code null} if the input is {@code null}
     */
    @Override
    public QueryMapEncoder enrich(QueryMapEncoder queryMapEncoder) {
        if (queryMapEncoder == null) {
            return null;
        }

        DecoratedQueryMapEncoder decoratedQueryMapEncoder = instantiate(DecoratedQueryMapEncoder.class, QueryMapEncoder.class,
                contextId, contextFactory, clientProperties, queryMapEncoder);

        this.componentRegistry.register(contextId, decoratedQueryMapEncoder);
        return decoratedQueryMapEncoder;
    }
}