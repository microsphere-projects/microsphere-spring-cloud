package io.microsphere.spring.cloud.openfeign.errordecoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
public class AErrorDecoder implements ErrorDecoder {

    private static final Logger log = LoggerFactory.getLogger(AErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        return new IllegalArgumentException("");
    }
}
