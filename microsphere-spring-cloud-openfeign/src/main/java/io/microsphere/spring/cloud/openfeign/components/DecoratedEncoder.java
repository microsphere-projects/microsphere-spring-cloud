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

    public DecoratedEncoder(String contextId, FeignContext feignContext, FeignClientProperties clientProperties,
                            Encoder delegate) {
        super(contextId, feignContext, clientProperties, delegate);
    }

    /**
     * Returns the {@link Encoder} implementation class to use when reloading
     * the delegate after a refresh, as configured in {@link FeignClientConfiguration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<? extends Encoder> type = decoratedEncoder.componentType();
     * }</pre>
     *
     * @return the configured {@link Encoder} class, or {@link Encoder} if not configured
     */
    @Override
    protected Class<? extends Encoder> componentType() {
        Class<Encoder> encoderClass = get(FeignClientConfiguration::getEncoder);
        return encoderClass == null ? Encoder.class : encoderClass;
    }

    /**
     * Encodes the given object into the {@link RequestTemplate} body by delegating
     * to the underlying {@link Encoder} implementation.
     *
     * <p>Example Usage:
     * <pre>{@code
     * decoratedEncoder.encode(myDto, MyDto.class, requestTemplate);
     * }</pre>
     *
     * @param object the object to encode
     * @param bodyType the type of the body
     * @param template the {@link RequestTemplate} to write the encoded body into
     * @throws EncodeException if encoding fails
     */
    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        delegate().encode(object, bodyType, template);
    }
}