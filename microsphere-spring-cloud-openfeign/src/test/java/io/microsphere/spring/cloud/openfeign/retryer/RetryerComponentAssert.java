package io.microsphere.spring.cloud.openfeign.retryer;

import feign.InvocationHandlerFactory;
import feign.Retryer;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedRetryer;

import static io.microsphere.reflect.FieldUtils.getFieldValue;

/**
 * @author <a href="mailto:hanchao@66yunlian.com">韩超</a>
 * @since 0.0.1
 */
public class RetryerComponentAssert extends FeignComponentAssert<Retryer> {

    @Override
    protected Retryer loadCurrentComponent(InvocationHandlerFactory.MethodHandler methodHandler) throws Exception {
        DecoratedRetryer retryer = getFieldValue(methodHandler, "retryer");
        return retryer.delegate();
    }
}
