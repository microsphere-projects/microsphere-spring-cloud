package io.microsphere.spring.cloud.openfeign.autorefresh;

import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import io.microsphere.spring.cloud.openfeign.components.*;
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

    public AutoRefreshCapability(FeignClientProperties clientProperties, FeignContext feignContext, FeignComponentRegistry componentRegistry) {
        this.clientProperties = clientProperties;
        this.feignContext = feignContext;
        this.componentRegistry = componentRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String contextId = applicationContext.getDisplayName().split("-")[1];
        this.contextId = contextId;
    }


    @Override
    public Retryer enrich(Retryer retryer) {
        if (retryer == null)
            return null;

        DecoratedRetryer decoratedRetryer = new DecoratedRetryer(contextId, feignContext, clientProperties, retryer);
        this.componentRegistry.register(contextId, decoratedRetryer);
        return decoratedRetryer;
    }

    @Override
    public Contract enrich(Contract contract) {
        if (contract == null)
            return null;

        DecoratedContract decoratedContract = new DecoratedContract(contextId, feignContext, clientProperties, contract);
        this.componentRegistry.register(contextId, decoratedContract);
        return decoratedContract;
    }

    @Override
    public Decoder enrich(Decoder decoder) {
        if (decoder == null)
            return null;

        DecoratedDecoder decoratedDecoder = new DecoratedDecoder(contextId, feignContext, clientProperties, decoder);
        this.componentRegistry.register(contextId, decoratedDecoder);
        return decoratedDecoder;
    }

    @Override
    public Encoder enrich(Encoder encoder) {
        if (encoder == null)
            return null;

        DecoratedEncoder decoratedEncoder = new DecoratedEncoder(contextId, feignContext, clientProperties, encoder);
        this.componentRegistry.register(contextId, decoratedEncoder);
        return decoratedEncoder;
    }

    @Override
    public ErrorDecoder enrich(ErrorDecoder decoder) {
        if (decoder == null)
            return null;

        DecoratedErrorDecoder decoratedErrorDecoder = new DecoratedErrorDecoder(contextId, feignContext, clientProperties, decoder);
        this.componentRegistry.register(contextId, decoratedErrorDecoder);
        return decoratedErrorDecoder;
    }

    @Override
    public RequestInterceptor enrich(RequestInterceptor requestInterceptor) {
        if (this.componentRegistry.registerRequestInterceptor(contextId, requestInterceptor)) {
            return this.componentRegistry.getRequestInterceptor(contextId);
        } else return NoOpRequestInterceptor.INSTANCE;
    }

    @Override
    public QueryMapEncoder enrich(QueryMapEncoder queryMapEncoder) {
        if (queryMapEncoder == null)
            return null;

        DecoratedQueryMapEncoder decoratedQueryMapEncoder = new DecoratedQueryMapEncoder(contextId, feignContext, clientProperties, queryMapEncoder);
        this.componentRegistry.register(contextId, decoratedQueryMapEncoder);
        return decoratedQueryMapEncoder;
    }

}
