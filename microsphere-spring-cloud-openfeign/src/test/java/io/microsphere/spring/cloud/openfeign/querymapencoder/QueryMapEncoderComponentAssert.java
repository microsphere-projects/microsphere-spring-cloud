package io.microsphere.spring.cloud.openfeign.querymapencoder;

import feign.MethodHandlerConfiguration;
import feign.QueryMapEncoder;
import feign.ResponseHandler;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedQueryMapEncoder;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class QueryMapEncoderComponentAssert extends FeignComponentAssert<QueryMapEncoder> {

    @Override
    protected QueryMapEncoder loadCurrentComponent(MethodHandlerConfiguration configuration, ResponseHandler responseHandler) throws Exception {
        Class<MethodHandlerConfiguration> methodHandlerConfigurationClass = MethodHandlerConfiguration.class;
        Field buildTemplateFromArgs = methodHandlerConfigurationClass.getDeclaredField("buildTemplateFromArgs");
        buildTemplateFromArgs.setAccessible(true);
        Object buildTemplateFromArgsValue = buildTemplateFromArgs.get(configuration);
        Class<?> buildTemplateFromArgsType = buildTemplateFromArgsValue.getClass().getSuperclass();
        Field encoderField = buildTemplateFromArgsType.getDeclaredField("queryMapEncoder");
        encoderField.setAccessible(true);
        DecoratedQueryMapEncoder encoder = (DecoratedQueryMapEncoder)encoderField.get(buildTemplateFromArgsValue);
        return encoder.delegate();
    }
}
