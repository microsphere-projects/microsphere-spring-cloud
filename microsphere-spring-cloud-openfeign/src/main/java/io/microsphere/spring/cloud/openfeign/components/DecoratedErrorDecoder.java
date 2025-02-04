package io.microsphere.spring.cloud.openfeign.components;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientSpecification;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class DecoratedErrorDecoder extends DecoratedFeignComponent<ErrorDecoder> implements ErrorDecoder {

    public DecoratedErrorDecoder(String contextId, NamedContextFactory<FeignClientSpecification> contextFactory, FeignClientProperties clientProperties, ErrorDecoder delegate) {
        super(contextId, contextFactory, clientProperties, delegate);
    }

    @Override
    protected Class<ErrorDecoder> componentType() {
        Class<ErrorDecoder> errorDecoderClass = null;
        if (getDefaultConfiguration() != null && getDefaultConfiguration().getErrorDecoder() != null)
            errorDecoderClass = getDefaultConfiguration().getErrorDecoder();

        if (getCurrentConfiguration() != null && getCurrentConfiguration().getErrorDecoder() != null)
            errorDecoderClass = getCurrentConfiguration().getErrorDecoder();

        if (errorDecoderClass != null)
            return errorDecoderClass;
        return ErrorDecoder.class;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        return delegate().decode(methodKey, response);
    }
}
