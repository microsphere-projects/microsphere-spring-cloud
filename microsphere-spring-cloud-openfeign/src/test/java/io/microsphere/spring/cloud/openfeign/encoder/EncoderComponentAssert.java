package io.microsphere.spring.cloud.openfeign.encoder;

import feign.InvocationHandlerFactory;
import feign.codec.Encoder;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedEncoder;

import static io.microsphere.reflect.FieldUtils.getFieldValue;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class EncoderComponentAssert extends FeignComponentAssert<Encoder> {

    public static final EncoderComponentAssert INSTANCE = new EncoderComponentAssert();

    private EncoderComponentAssert() {
    }

    @Override
    protected Encoder loadCurrentComponent(InvocationHandlerFactory.MethodHandler methodHandler) throws Exception {
        Object buildTemplateFromArgsValue = getFieldValue(methodHandler, "buildTemplateFromArgs");
        DecoratedEncoder encoder = getFieldValue(buildTemplateFromArgsValue, "encoder");
        return encoder.delegate();
    }
}
