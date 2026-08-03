package io.microsphere.spring.cloud.openfeign.encoder;

import feign.ResponseHandler;
import feign.codec.Encoder;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedEncoder;

import static io.microsphere.reflect.FieldUtils.getFieldValue;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0.0
 */
public class EncoderComponentAssert extends FeignComponentAssert<Encoder> {

    public static final EncoderComponentAssert INSTANCE = new EncoderComponentAssert();

    private EncoderComponentAssert() {
    }

    @Override
    protected Encoder loadCurrentComponent(Object configuration, ResponseHandler responseHandler) {
        Object buildTemplateFromArgsValue = getFieldValue(true, configuration, "buildTemplateFromArgs");
        DecoratedEncoder encoder = getFieldValue(true, buildTemplateFromArgsValue, "encoder");
        return encoder.delegate();
    }
}
