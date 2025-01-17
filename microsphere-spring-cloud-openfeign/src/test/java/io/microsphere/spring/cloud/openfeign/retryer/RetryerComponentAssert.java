package io.microsphere.spring.cloud.openfeign.retryer;

import feign.MethodHandlerConfiguration;
import feign.ResponseHandler;
import feign.Retryer;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedRetryer;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:hanchao@66yunlian.com">韩超</a>
 * @since 0.0.1
 */
public class RetryerComponentAssert extends FeignComponentAssert<Retryer> {

    @Override
    protected Retryer loadCurrentComponent(MethodHandlerConfiguration configuration, ResponseHandler responseHandler) throws Exception {
        Class<MethodHandlerConfiguration> methodHandlerConfigurationClass = MethodHandlerConfiguration.class;
        Field retryField = methodHandlerConfigurationClass.getDeclaredField("retryer");
        retryField.setAccessible(true);
        DecoratedRetryer retryer = (DecoratedRetryer) retryField.get(configuration);
        return retryer.delegate();
    }
}
