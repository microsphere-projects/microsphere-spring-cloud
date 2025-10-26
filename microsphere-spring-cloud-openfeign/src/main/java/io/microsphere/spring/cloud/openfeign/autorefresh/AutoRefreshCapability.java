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

    public AutoRefreshCapability(FeignClientProperties clientProperties,
                                 NamedContextFactory<FeignClientSpecification> contextFactory,
                                 FeignComponentRegistry componentRegistry) {
        this.clientProperties = clientProperties;
        this.contextFactory = contextFactory;
        this.componentRegistry = componentRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.contextId = applicationContext.getEnvironment().getProperty("spring.cloud.openfeign.client.name");
    }

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

    public ErrorDecoder enrich(ErrorDecoder decoder) {
        if (decoder == null) {
            return null;
        }

        DecoratedErrorDecoder decoratedErrorDecoder = instantiate(DecoratedErrorDecoder.class, ErrorDecoder.class,
                contextId, contextFactory, clientProperties, decoder);
        this.componentRegistry.register(contextId, decoratedErrorDecoder);
        return decoratedErrorDecoder;
    }

    @Override
    public RequestInterceptor enrich(RequestInterceptor requestInterceptor) {
        if (requestInterceptor == null) {
            return null;
        }
        return this.componentRegistry.registerRequestInterceptor(contextId, requestInterceptor);
    }

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