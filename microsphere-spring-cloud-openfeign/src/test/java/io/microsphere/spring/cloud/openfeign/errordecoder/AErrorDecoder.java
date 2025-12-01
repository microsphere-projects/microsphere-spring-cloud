package io.microsphere.spring.cloud.openfeign.errordecoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.microsphere.logging.Logger;
import static io.microsphere.logging.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
public class AErrorDecoder implements ErrorDecoder {

    private static final Logger log = getLogger(AErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        return new IllegalArgumentException("");
    }
}
