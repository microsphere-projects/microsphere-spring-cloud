package io.microsphere.spring.cloud.openfeign.components;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClientSpecification;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class DecoratedErrorDecoder extends DecoratedFeignComponent<ErrorDecoder> implements ErrorDecoder {

    public DecoratedErrorDecoder(String contextId, NamedContextFactory<FeignClientSpecification> contextFactory, FeignClientProperties clientProperties, ErrorDecoder delegate) {
        super(contextId, contextFactory, clientProperties, delegate);
    }

    @Override
    protected Class<ErrorDecoder> componentType() {
        Class<ErrorDecoder> errorDecoderClass = get(FeignClientConfiguration::getErrorDecoder);
        return errorDecoderClass == null ? (Class) Default.class : errorDecoderClass;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        return delegate().decode(methodKey, response);
    }
}