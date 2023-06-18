package io.microsphere.spring.cloud.openfeign;

import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import io.microsphere.spring.cloud.openfeign.autorefresh.FeignComponentRegistry;
import io.microsphere.spring.cloud.openfeign.omponents.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class RefreshableBuilder extends Feign.Builder{

    private final Feign.Builder delegateBuilder;

    private final FeignContext feignContext;
    private final BeanFactory beanFactory;

    private final FeignComponentRegistry registry;

    private String clientName;

    public RefreshableBuilder(BeanFactory beanFactory, FeignComponentRegistry registry, Feign.Builder delegateBuilder, FeignContext feignContext) {
        this.beanFactory = beanFactory;
        this.registry = registry;
        this.delegateBuilder = delegateBuilder;
        this.feignContext = feignContext;
    }

    @Override
    public Feign.Builder logLevel(Logger.Level logLevel) {
        delegateBuilder.logLevel(logLevel);
        return this;
    }

    @Override
    public Feign.Builder contract(Contract contract) {
        delegateBuilder.contract(contract);
        return this;
    }

    @Override
    public Feign.Builder client(Client client) {
        delegateBuilder.client(client);
        return this;
    }

    @Override
    public Feign.Builder retryer(Retryer retryer) {
        delegateBuilder.retryer(retryer);
        return this;
    }

    @Override
    public Feign.Builder logger(Logger logger) {
        delegateBuilder.logger(logger);
        return this;
    }

    @Override
    public Feign.Builder encoder(Encoder encoder) {
        delegateBuilder.encoder(encoder);
        return this;
    }

    @Override
    public Feign.Builder decoder(Decoder decoder) {
        delegateBuilder.decoder(decoder);
        return this;
    }

    @Override
    public Feign.Builder queryMapEncoder(QueryMapEncoder queryMapEncoder) {
        delegateBuilder.queryMapEncoder(queryMapEncoder);
        return this;
    }

    @Override
    public Feign.Builder mapAndDecode(ResponseMapper mapper, Decoder decoder) {
        delegateBuilder.mapAndDecode(mapper, decoder);
        return this;
    }

    @Override
    @Deprecated
    public Feign.Builder decode404() {
        delegateBuilder.decode404();
        return this;
    }

    @Override
    public Feign.Builder errorDecoder(ErrorDecoder errorDecoder) {
        delegateBuilder.errorDecoder(errorDecoder);
        return this;
    }

    @Override
    public Feign.Builder options(Request.Options options) {
        delegateBuilder.options(options);
        return this;
    }

    @Override
    public Feign.Builder requestInterceptor(RequestInterceptor requestInterceptor) {
        return this;
    }

    @Override
    public Feign.Builder requestInterceptors(Iterable<RequestInterceptor> requestInterceptors) {
        return this;
    }

    @Override
    public Feign.Builder invocationHandlerFactory(InvocationHandlerFactory invocationHandlerFactory) {
        delegateBuilder.invocationHandlerFactory(invocationHandlerFactory);
        return this;
    }

    @Override
    public Feign.Builder doNotCloseAfterDecode() {
        delegateBuilder.doNotCloseAfterDecode();
        return this;
    }

    @Override
    public Feign.Builder exceptionPropagationPolicy(ExceptionPropagationPolicy propagationPolicy) {
        delegateBuilder.exceptionPropagationPolicy(propagationPolicy);
        return this;
    }

    @Override
    public Feign.Builder addCapability(Capability capability) {
        delegateBuilder.addCapability(capability);
        return this;
    }
    @Override
    public Feign.Builder dismiss404() {
        delegateBuilder.dismiss404();
        return this;
    }

    @Override
    public Feign.Builder responseInterceptor(ResponseInterceptor responseInterceptor) {
        delegateBuilder.responseInterceptor(responseInterceptor);
        return this;
    }

    @Override
    public Feign build() {
        CompositedRequestInterceptor requestInterceptor = new CompositedRequestInterceptor(beanFactory);
        requestInterceptor.injectContextId(clientName);

        this.registry.register(clientName, requestInterceptor);

        this.delegateBuilder.requestInterceptors(Collections.singleton(requestInterceptor));
        return delegateBuilder.build();
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
