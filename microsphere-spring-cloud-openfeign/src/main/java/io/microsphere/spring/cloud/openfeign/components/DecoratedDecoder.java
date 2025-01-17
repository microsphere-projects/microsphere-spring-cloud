package io.microsphere.spring.cloud.openfeign.components;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientSpecification;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class DecoratedDecoder extends DecoratedFeignComponent<Decoder> implements Decoder {

    public DecoratedDecoder(String contextId, NamedContextFactory<FeignClientSpecification> contextFactory, FeignClientProperties clientProperties, Decoder delegate) {
        super(contextId, contextFactory, clientProperties, delegate);
    }

    @Override
    protected Class<Decoder> componentType() {
        Class<Decoder> decoderClass = null;
        if (getDefaultConfiguration() != null && getDefaultConfiguration().getDecoder() != null)
            decoderClass = getDefaultConfiguration().getDecoder();

        if (getCurrentConfiguration() != null && getCurrentConfiguration().getDecoder() != null)
            decoderClass = getCurrentConfiguration().getDecoder();

        if (decoderClass != null)
            return decoderClass;
        return Decoder.class;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        return delegate().decode(response, type);
    }
}
