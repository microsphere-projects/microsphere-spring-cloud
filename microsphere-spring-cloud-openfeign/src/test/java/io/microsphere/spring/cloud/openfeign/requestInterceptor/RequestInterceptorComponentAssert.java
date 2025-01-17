package io.microsphere.spring.cloud.openfeign.requestInterceptor;

import feign.MethodHandlerConfiguration;
import feign.RequestInterceptor;
import feign.ResponseHandler;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.CompositedRequestInterceptor;
import io.microsphere.spring.cloud.openfeign.components.DecoratedRetryer;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author <a href="mailto:hanchao@66yunlian.com">韩超</a>
 * @since 0.0.1
 */
public class RequestInterceptorComponentAssert extends FeignComponentAssert<RequestInterceptor> {

    @Override
    protected CompositedRequestInterceptor loadCurrentComponent(MethodHandlerConfiguration configuration, ResponseHandler responseHandler) throws Exception {
        Class<MethodHandlerConfiguration> methodHandlerConfigurationClass = MethodHandlerConfiguration.class;
        Field retryField = methodHandlerConfigurationClass.getDeclaredField("requestInterceptors");
        retryField.setAccessible(true);
        List<RequestInterceptor> retryer = (List<RequestInterceptor>) retryField.get(configuration);
        for (RequestInterceptor interceptor : retryer) {
            if (interceptor instanceof CompositedRequestInterceptor) {
                return (CompositedRequestInterceptor) interceptor;
            }
        }
        return null;
    }

    @Override
    public boolean expect(MethodHandlerConfiguration configuration, ResponseHandler responseHandler, Class<RequestInterceptor> expectedClass) throws Exception {
        CompositedRequestInterceptor requestInterceptor = loadCurrentComponent(configuration, responseHandler);
        for(RequestInterceptor interceptor : requestInterceptor.getRequestInterceptors()) {
            if (expectedClass.equals(interceptor.getClass()))
                return true;
        }
        return false;
    }
}
