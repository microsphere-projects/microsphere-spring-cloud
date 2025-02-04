package io.microsphere.spring.cloud.openfeign.decoder;

import feign.InvocationHandlerFactory;
import feign.codec.Decoder;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedDecoder;

import static io.microsphere.reflect.FieldUtils.getFieldValue;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class DecoderComponentAssert extends FeignComponentAssert<Decoder> {

    @Override
    protected Decoder loadCurrentComponent(InvocationHandlerFactory.MethodHandler methodHandler) throws Exception {
        DecoratedDecoder decoder = getFieldValue(methodHandler, "decoder");
        return decoder.delegate();
    }
}
