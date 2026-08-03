package io.microsphere.spring.cloud.openfeign.errordecoder;

import feign.ResponseHandler;
import feign.codec.ErrorDecoder;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedErrorDecoder;

import static io.microsphere.reflect.FieldUtils.getFieldValue;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0.0
 */
public class ErrorDecoderComponentAssert extends FeignComponentAssert<ErrorDecoder> {

    @Override
    protected ErrorDecoder loadCurrentComponent(Object configuration, ResponseHandler responseHandler) {
        DecoratedErrorDecoder errorDecoder = getFieldValue(true, responseHandler, "errorDecoder");
        return errorDecoder.delegate();
    }
}
