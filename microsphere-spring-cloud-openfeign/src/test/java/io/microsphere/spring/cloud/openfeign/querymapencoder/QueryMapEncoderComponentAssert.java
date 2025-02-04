package io.microsphere.spring.cloud.openfeign.querymapencoder;

import feign.InvocationHandlerFactory;
import feign.QueryMapEncoder;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedQueryMapEncoder;

import static io.microsphere.reflect.FieldUtils.getFieldValue;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class QueryMapEncoderComponentAssert extends FeignComponentAssert<QueryMapEncoder> {

    @Override
    protected QueryMapEncoder loadCurrentComponent(InvocationHandlerFactory.MethodHandler methodHandler) throws Exception {
        Object buildTemplateFromArgsValue = getFieldValue(methodHandler, "buildTemplateFromArgs");
        DecoratedQueryMapEncoder encoder = getFieldValue(buildTemplateFromArgsValue, "queryMapEncoder");
        return encoder.delegate();
    }
}
