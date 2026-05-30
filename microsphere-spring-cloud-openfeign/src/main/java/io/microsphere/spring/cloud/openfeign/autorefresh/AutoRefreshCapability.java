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
import io.microsphere.spring.cloud.openfeign.components.DecoratedFeignComponent;
import io.microsphere.spring.cloud.openfeign.components.DecoratedQueryMapEncoder;
import io.microsphere.spring.cloud.openfeign.components.DecoratedRetryer;
import org.springframework.beans.BeansException;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class AutoRefreshCapability implements Capability, ApplicationContextAware {

    private final FeignComponentRegistry componentRegistry;

    private final FeignContext feignContext;

    private final FeignClientProperties clientProperties;

    private String contextId;

    private static final String CONTEXT_ID_PROPERTY_NAME = "feign.client.name";

    /**
     * Constructs a new {@link AutoRefreshCapability} with the required dependencies
     * for enriching Feign components with auto-refresh support.
     *
     * <p>Example Usage:
     * <pre>{@code
     * AutoRefreshCapability capability =
     *     new AutoRefreshCapability(clientProperties, feignContext, componentRegistry);
     * }</pre>
     *
     * @param clientProperties the {@link FeignClientProperties} containing Feign client configuration
     * @param feignContext the {@link FeignContext} for resolving Feign component instances
     * @param componentRegistry the {@link FeignComponentRegistry} for registering refreshable components
     */
    public AutoRefreshCapability(FeignClientProperties clientProperties, FeignContext feignContext, FeignComponentRegistry componentRegistry) {
        this.clientProperties = clientProperties;
        this.feignContext = feignContext;
        this.componentRegistry = componentRegistry;
    }

    /**
     * Sets the {@link ApplicationContext} and resolves the Feign client context ID
     * from the environment property {@code feign.client.name}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Invoked automatically by Spring's ApplicationContextAware mechanism
     * capability.setApplicationContext(applicationContext);
     * }</pre>
     *
     * @param applicationContext the {@link ApplicationContext} in which this capability operates
     * @throws BeansException if context initialization fails
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.contextId = applicationContext.getEnvironment().getProperty(CONTEXT_ID_PROPERTY_NAME);
    }

    /**
     * Enriches the given {@link Retryer} by wrapping it in a {@link DecoratedRetryer}
     * that supports auto-refresh, and registers it with the {@link FeignComponentRegistry}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Retryer enriched = capability.enrich(Retryer.NEVER_RETRY);
     * }</pre>
     *
     * @param retryer the original {@link Retryer} to enrich, or {@code null}
     * @return the decorated retryer, or {@code null} if the input was {@code null}
     */
    @Override
    public Retryer enrich(Retryer retryer) {
        if (retryer == null) {
            return null;
        }

        DecoratedRetryer decoratedRetryer = DecoratedFeignComponent.instantiate(DecoratedRetryer.class, Retryer.class,
                contextId, feignContext, clientProperties, retryer);
        this.componentRegistry.register(contextId, decoratedRetryer);
        return decoratedRetryer;
    }

    /**
     * Enriches the given {@link Contract} by wrapping it in a {@link DecoratedContract}
     * that supports auto-refresh, and registers it with the {@link FeignComponentRegistry}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Contract enriched = capability.enrich(new SpringMvcContract());
     * }</pre>
     *
     * @param contract the original {@link Contract} to enrich, or {@code null}
     * @return the decorated contract, or {@code null} if the input was {@code null}
     */
    @Override
    public Contract enrich(Contract contract) {
        if (contract == null) {
            return null;
        }

        DecoratedContract decoratedContract = DecoratedFeignComponent.instantiate(DecoratedContract.class, Contract.class,
                contextId, feignContext, clientProperties, contract);
        this.componentRegistry.register(contextId, decoratedContract);
        return decoratedContract;
    }

    /**
     * Enriches the given {@link Decoder} by wrapping it in a {@link DecoratedDecoder}
     * that supports auto-refresh, and registers it with the {@link FeignComponentRegistry}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Decoder enriched = capability.enrich(new ResponseEntityDecoder(decoder));
     * }</pre>
     *
     * @param decoder the original {@link Decoder} to enrich, or {@code null}
     * @return the decorated decoder, or {@code null} if the input was {@code null}
     */
    @Override
    public Decoder enrich(Decoder decoder) {
        if (decoder == null) {
            return null;
        }

        DecoratedDecoder decoratedDecoder = DecoratedFeignComponent.instantiate(DecoratedDecoder.class, Decoder.class,
                contextId, feignContext, clientProperties, decoder);
        this.componentRegistry.register(contextId, decoratedDecoder);
        return decoratedDecoder;
    }

    /**
     * Enriches the given {@link Encoder} by wrapping it in a {@link DecoratedEncoder}
     * that supports auto-refresh, and registers it with the {@link FeignComponentRegistry}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Encoder enriched = capability.enrich(new SpringEncoder(messageConverters));
     * }</pre>
     *
     * @param encoder the original {@link Encoder} to enrich, or {@code null}
     * @return the decorated encoder, or {@code null} if the input was {@code null}
     */
    @Override
    public Encoder enrich(Encoder encoder) {
        if (encoder == null) {
            return null;
        }

        DecoratedEncoder decoratedEncoder = DecoratedFeignComponent.instantiate(DecoratedEncoder.class, Encoder.class,
                contextId, feignContext, clientProperties, encoder);
        this.componentRegistry.register(contextId, decoratedEncoder);
        return decoratedEncoder;
    }

    /**
     * Enriches the given {@link ErrorDecoder} by wrapping it in a {@link DecoratedErrorDecoder}
     * that supports auto-refresh, and registers it with the {@link FeignComponentRegistry}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * ErrorDecoder enriched = capability.enrich(new ErrorDecoder.Default());
     * }</pre>
     *
     * @param decoder the original {@link ErrorDecoder} to enrich, or {@code null}
     * @return the decorated error decoder, or {@code null} if the input was {@code null}
     */
    public ErrorDecoder enrich(ErrorDecoder decoder) {
        if (decoder == null) {
            return null;
        }

        DecoratedErrorDecoder decoratedErrorDecoder = DecoratedFeignComponent.instantiate(DecoratedErrorDecoder.class, ErrorDecoder.class,
                contextId, feignContext, clientProperties, decoder);
        this.componentRegistry.register(contextId, decoratedErrorDecoder);
        return decoratedErrorDecoder;
    }

    /**
     * Enriches the given {@link RequestInterceptor} by registering it with the
     * {@link FeignComponentRegistry} as part of a {@link io.microsphere.spring.cloud.openfeign.components.CompositedRequestInterceptor}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RequestInterceptor enriched = capability.enrich(template -> template.header("X-Custom", "value"));
     * }</pre>
     *
     * @param requestInterceptor the original {@link RequestInterceptor} to enrich, or {@code null}
     * @return the composited request interceptor, or {@code null} if the input was {@code null}
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
     * that supports auto-refresh, and registers it with the {@link FeignComponentRegistry}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * QueryMapEncoder enriched = capability.enrich(new BeanQueryMapEncoder());
     * }</pre>
     *
     * @param queryMapEncoder the original {@link QueryMapEncoder} to enrich, or {@code null}
     * @return the decorated query map encoder, or {@code null} if the input was {@code null}
     */
    @Override
    public QueryMapEncoder enrich(QueryMapEncoder queryMapEncoder) {
        if (queryMapEncoder == null) {
            return null;
        }

        DecoratedQueryMapEncoder decoratedQueryMapEncoder = DecoratedFeignComponent.instantiate(DecoratedQueryMapEncoder.class, QueryMapEncoder.class,
                contextId, feignContext, clientProperties, queryMapEncoder);

        this.componentRegistry.register(contextId, decoratedQueryMapEncoder);
        return decoratedQueryMapEncoder;
    }
}