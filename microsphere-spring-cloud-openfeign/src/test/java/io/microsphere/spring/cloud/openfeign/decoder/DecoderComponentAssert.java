package io.microsphere.spring.cloud.openfeign.decoder;

import feign.ResponseHandler;
import feign.codec.Decoder;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedDecoder;

import static io.microsphere.reflect.FieldUtils.getFieldValue;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0.0
 */
public class DecoderComponentAssert extends FeignComponentAssert<Decoder> {

    @Override
    protected Decoder loadCurrentComponent(Object configuration, ResponseHandler responseHandler) {
        DecoratedDecoder decoder = getFieldValue(true, responseHandler, "decoder");
        return decoder.delegate();
    }
}