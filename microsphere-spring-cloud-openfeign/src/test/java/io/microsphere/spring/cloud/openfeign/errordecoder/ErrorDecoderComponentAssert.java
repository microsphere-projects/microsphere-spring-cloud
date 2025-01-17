package io.microsphere.spring.cloud.openfeign.errordecoder;

import feign.ResponseHandler;
import feign.codec.ErrorDecoder;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedErrorDecoder;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class ErrorDecoderComponentAssert extends FeignComponentAssert<ErrorDecoder> {

    @Override
    protected ErrorDecoder loadCurrentComponent(Object configuration, ResponseHandler responseHandler) throws Exception {
        Class<ResponseHandler> responseHandlerClass = ResponseHandler.class;
        Field errorDecoderField = responseHandlerClass.getDeclaredField("errorDecoder");
        errorDecoderField.setAccessible(true);
        DecoratedErrorDecoder errorDecoder = (DecoratedErrorDecoder)errorDecoderField.get(responseHandler);
        return errorDecoder.delegate();
    }
}
