package io.microsphere.spring.cloud.openfeign.components;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignContext;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class DecoratedErrorDecoder extends DecoratedFeignComponent<ErrorDecoder> implements ErrorDecoder {

    public DecoratedErrorDecoder(String contextId, FeignContext feignContext, FeignClientProperties clientProperties, ErrorDecoder delegate) {
        super(contextId, feignContext, clientProperties, delegate);
    }

    /**
     * Returns the {@link ErrorDecoder} implementation class to use when reloading
     * the delegate after a refresh, as configured in {@link FeignClientConfiguration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<? extends ErrorDecoder> type = decoratedErrorDecoder.componentType();
     * }</pre>
     *
     * @return the configured {@link ErrorDecoder} class, or {@link ErrorDecoder.Default} if not configured
     */
    @Override
    protected Class<? extends ErrorDecoder> componentType() {
        Class<ErrorDecoder> errorDecoderClass = get(FeignClientConfiguration::getErrorDecoder);
        return errorDecoderClass == null ? Default.class : errorDecoderClass;
    }

    /**
     * Decodes an error {@link Response} into an {@link Exception} by delegating
     * to the underlying {@link ErrorDecoder} implementation.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Exception ex = decoratedErrorDecoder.decode("MyClient#myMethod()", response);
     * }</pre>
     *
     * @param methodKey the Feign method key (e.g., {@code "MyClient#myMethod()"})
     * @param response the HTTP {@link Response} that caused the error
     * @return an {@link Exception} representing the error
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        return delegate().decode(methodKey, response);
    }
}