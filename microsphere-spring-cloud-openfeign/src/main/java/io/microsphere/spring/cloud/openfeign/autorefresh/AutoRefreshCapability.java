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
 * @since 0.0.1
 */
public class AutoRefreshCapability implements Capability, ApplicationContextAware {

    private final FeignComponentRegistry componentRegistry;
    private final FeignContext feignContext;
    private final FeignClientProperties clientProperties;

    private String contextId;

    private static final String CONTEXT_ID_PROPERTY_NAME = "feign.client.name";

    public AutoRefreshCapability(FeignClientProperties clientProperties, FeignContext feignContext, FeignComponentRegistry componentRegistry) {
        this.clientProperties = clientProperties;
        this.feignContext = feignContext;
        this.componentRegistry = componentRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.contextId = applicationContext.getEnvironment().getProperty(CONTEXT_ID_PROPERTY_NAME);
    }


    @Override
    public Retryer enrich(Retryer retryer) {
        if (retryer == null)
            return null;

        DecoratedRetryer decoratedRetryer = DecoratedFeignComponent.instantiate(DecoratedRetryer.class, Retryer.class,
                contextId, feignContext, clientProperties, retryer);

        this.componentRegistry.register(contextId, decoratedRetryer);
        return decoratedRetryer;
    }

    @Override
    public Contract enrich(Contract contract) {
        if (contract == null)
            return null;

        DecoratedContract decoratedContract = DecoratedFeignComponent.instantiate(DecoratedContract.class, Contract.class,
                contextId, feignContext, clientProperties, contract);
        this.componentRegistry.register(contextId, decoratedContract);
        return decoratedContract;
    }

    @Override
    public Decoder enrich(Decoder decoder) {
        if (decoder == null)
            return null;

        DecoratedDecoder decoratedDecoder = DecoratedFeignComponent.instantiate(DecoratedDecoder.class, Decoder.class,
                contextId, feignContext, clientProperties, decoder);
        this.componentRegistry.register(contextId, decoratedDecoder);
        return decoratedDecoder;
    }

    @Override
    public Encoder enrich(Encoder encoder) {
        if (encoder == null)
            return null;

        DecoratedEncoder decoratedEncoder = DecoratedFeignComponent.instantiate(DecoratedEncoder.class, Encoder.class,
                contextId, feignContext, clientProperties, encoder);
        this.componentRegistry.register(contextId, decoratedEncoder);
        return decoratedEncoder;
    }

    @Override
    public ErrorDecoder enrich(ErrorDecoder decoder) {
        if (decoder == null)
            return null;

        DecoratedErrorDecoder decoratedErrorDecoder = DecoratedFeignComponent.instantiate(DecoratedErrorDecoder.class, ErrorDecoder.class,
                contextId, feignContext, clientProperties, decoder);
        this.componentRegistry.register(contextId, decoratedErrorDecoder);
        return decoratedErrorDecoder;
    }

    @Override
    public RequestInterceptor enrich(RequestInterceptor requestInterceptor) {
        return this.componentRegistry.registerRequestInterceptor(contextId, requestInterceptor);
    }

    @Override
    public QueryMapEncoder enrich(QueryMapEncoder queryMapEncoder) {
        if (queryMapEncoder == null)
            return null;

        DecoratedQueryMapEncoder decoratedQueryMapEncoder = DecoratedFeignComponent.instantiate(DecoratedQueryMapEncoder.class, QueryMapEncoder.class,
                contextId, feignContext, clientProperties, queryMapEncoder);

        this.componentRegistry.register(contextId, decoratedQueryMapEncoder);
        return decoratedQueryMapEncoder;
    }

}
