package io.microsphere.spring.cloud.openfeign.encoder;

import feign.ResponseHandler;
import feign.codec.Encoder;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedEncoder;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class EncoderComponentAssert extends FeignComponentAssert<Encoder> {

    public static final EncoderComponentAssert INSTANCE = new EncoderComponentAssert();

    private EncoderComponentAssert() {

    }

    @Override
    protected Encoder loadCurrentComponent(Object configuration, ResponseHandler responseHandler) throws Exception {
        Class<?> configurationClass = configuration.getClass();
        Field buildTemplateFromArgs = configurationClass.getDeclaredField("buildTemplateFromArgs");
        buildTemplateFromArgs.setAccessible(true);
        Object buildTemplateFromArgsValue = buildTemplateFromArgs.get(configuration);
        Class<?> buildTemplateFromArgsType = buildTemplateFromArgsValue.getClass();
        Field encoderField = buildTemplateFromArgsType.getDeclaredField("encoder");
        encoderField.setAccessible(true);
        DecoratedEncoder encoder = (DecoratedEncoder)encoderField.get(buildTemplateFromArgsValue);
        return encoder.delegate();
    }
}
