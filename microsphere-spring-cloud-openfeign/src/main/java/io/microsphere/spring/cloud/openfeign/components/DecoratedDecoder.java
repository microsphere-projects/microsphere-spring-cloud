package io.microsphere.spring.cloud.openfeign.components;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignContext;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class DecoratedDecoder extends DecoratedFeignComponent<Decoder> implements Decoder {

    public DecoratedDecoder(String contextId, FeignContext feignContext, FeignClientProperties clientProperties, Decoder delegate) {
        super(contextId, feignContext, clientProperties, delegate);
    }

    /**
     * Returns the {@link Decoder} implementation class to use when reloading
     * the delegate after a refresh, as configured in {@link FeignClientConfiguration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<? extends Decoder> type = decoratedDecoder.componentType();
     * }</pre>
     *
     * @return the configured {@link Decoder} class, or {@link Decoder} if not configured
     */
    @Override
    protected Class<? extends Decoder> componentType() {
        Class<Decoder> decoderClass = get(FeignClientConfiguration::getDecoder);
        return decoderClass == null ? Decoder.class : decoderClass;
    }

    /**
     * Decodes the given {@link Response} into an object of the specified type by
     * delegating to the underlying {@link Decoder} implementation.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Object result = decoratedDecoder.decode(response, MyDto.class);
     * }</pre>
     *
     * @param response the HTTP {@link Response} to decode
     * @param type the target type to decode to
     * @return the decoded object
     * @throws IOException if an I/O error occurs during decoding
     * @throws DecodeException if decoding fails
     * @throws FeignException if a Feign-specific error occurs
     */
    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        return delegate().decode(response, type);
    }
}
