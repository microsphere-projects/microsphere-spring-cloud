package io.microsphere.spring.cloud.openfeign.errordecoder;

import feign.InvocationHandlerFactory;
import feign.codec.ErrorDecoder;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedErrorDecoder;

import static io.microsphere.reflect.FieldUtils.getFieldValue;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class ErrorDecoderComponentAssert extends FeignComponentAssert<ErrorDecoder> {

    @Override
    protected ErrorDecoder loadCurrentComponent(InvocationHandlerFactory.MethodHandler methodHandler) throws Exception {
        Object asyncResponseHandler = getFieldValue(methodHandler, "asyncResponseHandler");
        DecoratedErrorDecoder errorDecoder = getFieldValue(asyncResponseHandler, "errorDecoder");
        return errorDecoder.delegate();
    }
}
