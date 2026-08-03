package io.microsphere.spring.cloud.openfeign.querymapencoder;

import feign.QueryMapEncoder;
import feign.ResponseHandler;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedQueryMapEncoder;

import static io.microsphere.reflect.FieldUtils.getFieldValue;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0.0
 */
public class QueryMapEncoderComponentAssert extends FeignComponentAssert<QueryMapEncoder> {

    @Override
    protected QueryMapEncoder loadCurrentComponent(Object configuration, ResponseHandler responseHandler) {
        Object buildTemplateFromArgsValue = getFieldValue(true, configuration, "buildTemplateFromArgs");
        DecoratedQueryMapEncoder encoder = getFieldValue(true, buildTemplateFromArgsValue, "queryMapEncoder");
        return encoder.delegate();
    }
}