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

    /**
     * Constructs a {@link DecoratedErrorDecoder} wrapping the given {@link ErrorDecoder} delegate.
     *
     * <p>Example Usage:
     * <pre>{@code
     * DecoratedErrorDecoder decoder = new DecoratedErrorDecoder(
     *     "my-client", contextFactory, clientProperties, new ErrorDecoder.Default());
     * }</pre>
     *
     * @param contextId        the Feign client context ID
     * @param contextFactory   the {@link NamedContextFactory} for resolving per-client contexts
     * @param clientProperties the {@link FeignClientProperties} for configuration lookup
     * @param delegate         the original {@link ErrorDecoder} to delegate to
     */
    public DecoratedErrorDecoder(String contextId, NamedContextFactory<FeignClientSpecification> contextFactory, FeignClientProperties clientProperties, ErrorDecoder delegate) {
        super(contextId, contextFactory, clientProperties, delegate);
    }

    /**
     * Returns the configured {@link ErrorDecoder} class from {@link FeignClientConfiguration},
     * falling back to {@link ErrorDecoder.Default} if not configured.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<? extends ErrorDecoder> type = decoratedErrorDecoder.componentType();
     * }</pre>
     *
     * @return the {@link ErrorDecoder} component type class
     */
    @Override
    protected Class<? extends ErrorDecoder> componentType() {
        Class<ErrorDecoder> errorDecoderClass = get(FeignClientConfiguration::getErrorDecoder);
        return errorDecoderClass == null ? Default.class : errorDecoderClass;
    }

    /**
     * Decodes an error response by delegating to the underlying {@link ErrorDecoder}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Exception ex = decoratedErrorDecoder.decode("MyClient#myMethod()", response);
     * }</pre>
     *
     * @param methodKey the Feign method key (e.g. {@code "MyClient#myMethod()"})
     * @param response  the Feign {@link Response} containing the error
     * @return the decoded {@link Exception}
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        return delegate().decode(methodKey, response);
    }
}