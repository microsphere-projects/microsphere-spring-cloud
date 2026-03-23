package io.microsphere.spring.cloud.openfeign.components;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignContext;

import java.lang.reflect.Type;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class DecoratedEncoder extends DecoratedFeignComponent<Encoder> implements Encoder {

    /**
     * Constructs a {@link DecoratedEncoder} wrapping the given {@link Encoder} delegate.
     *
     * <p>Example Usage:
     * <pre>{@code
     * DecoratedEncoder encoder = new DecoratedEncoder(
     *     "my-client", feignContext, clientProperties, new Encoder.Default());
     * }</pre>
     *
     * @param contextId        the Feign client context ID
     * @param feignContext     the {@link FeignContext} for resolving per-client contexts
     * @param clientProperties the {@link FeignClientProperties} for configuration lookup
     * @param delegate         the original {@link Encoder} to delegate to
     */
    public DecoratedEncoder(String contextId, FeignContext feignContext, FeignClientProperties clientProperties,
                            Encoder delegate) {
        super(contextId, feignContext, clientProperties, delegate);
    }

    /**
     * Returns the configured {@link Encoder} class from {@link FeignClientConfiguration},
     * falling back to {@link Encoder} if not configured.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<? extends Encoder> type = decoratedEncoder.componentType();
     * }</pre>
     *
     * @return the {@link Encoder} component type class
     */
    @Override
    protected Class<? extends Encoder> componentType() {
        Class<Encoder> encoderClass = get(FeignClientConfiguration::getEncoder);
        return encoderClass == null ? Encoder.class : encoderClass;
    }

    /**
     * Encodes the given object into the {@link RequestTemplate} by delegating to the
     * underlying {@link Encoder}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * decoratedEncoder.encode(myObject, MyObject.class, requestTemplate);
     * }</pre>
     *
     * @param object   the object to encode
     * @param bodyType the body type
     * @param template the {@link RequestTemplate} to encode into
     * @throws EncodeException if encoding fails
     */
    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        delegate().encode(object, bodyType, template);
    }
}