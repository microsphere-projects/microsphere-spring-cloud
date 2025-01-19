package io.microsphere.spring.cloud.openfeign.errordecoder;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class BErrorEncoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return null;
    }
}
