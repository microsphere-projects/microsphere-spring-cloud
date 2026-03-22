package io.microsphere.spring.cloud.openfeign.components;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClientSpecification;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class DecoratedDecoder extends DecoratedFeignComponent<Decoder> implements Decoder {

    /**
     * Constructs a {@link DecoratedDecoder} wrapping the given {@link Decoder} delegate.
     *
     * <p>Example Usage:
     * <pre>{@code
     * DecoratedDecoder decoder = new DecoratedDecoder(
     *     "my-client", contextFactory, clientProperties, new Decoder.Default());
     * }</pre>
     *
     * @param contextId        the Feign client context ID
     * @param contextFactory   the {@link NamedContextFactory} for resolving per-client contexts
     * @param clientProperties the {@link FeignClientProperties} for configuration lookup
     * @param delegate         the original {@link Decoder} to delegate to
     */
    public DecoratedDecoder(String contextId, NamedContextFactory<FeignClientSpecification> contextFactory, FeignClientProperties clientProperties, Decoder delegate) {
        super(contextId, contextFactory, clientProperties, delegate);
    }

    /**
     * Returns the configured {@link Decoder} class from {@link FeignClientConfiguration},
     * falling back to {@link Decoder} if not configured.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<? extends Decoder> type = decoratedDecoder.componentType();
     * }</pre>
     *
     * @return the {@link Decoder} component type class
     */
    @Override
    protected Class<? extends Decoder> componentType() {
        Class<Decoder> decoderClass = get(FeignClientConfiguration::getDecoder);
        return decoderClass == null ? Decoder.class : decoderClass;
    }

    /**
     * Decodes a Feign {@link Response} into an object of the given type by delegating
     * to the underlying {@link Decoder}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Object result = decoratedDecoder.decode(response, String.class);
     * }</pre>
     *
     * @param response the Feign {@link Response} to decode
     * @param type     the target type to decode into
     * @return the decoded object
     * @throws IOException      if an I/O error occurs
     * @throws DecodeException  if decoding fails
     * @throws FeignException   if a Feign-specific error occurs
     */
    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        return delegate().decode(response, type);
    }
}
