package io.microsphere.spring.cloud.openfeign.retryer;

import feign.ResponseHandler;
import feign.Retryer;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedRetryer;

import static io.microsphere.reflect.FieldUtils.getFieldValue;

/**
 * @author <a href="mailto:hanchao@66yunlian.com">韩超</a>
 * @since 1.0.0
 */
public class RetryerComponentAssert extends FeignComponentAssert<Retryer> {

    @Override
    protected Retryer loadCurrentComponent(Object configuration, ResponseHandler responseHandler) {
        DecoratedRetryer retryer = getFieldValue(true, configuration, "retryer");
        return retryer.delegate();
    }
}