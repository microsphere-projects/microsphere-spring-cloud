package io.microsphere.spring.cloud.openfeign.requestInterceptor;

import feign.RequestInterceptor;
import feign.ResponseHandler;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.CompositedRequestInterceptor;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author <a href="mailto:hanchao@66yunlian.com">韩超</a>
 * @since 0.0.1
 */
public class RequestInterceptorComponentAssert extends FeignComponentAssert<RequestInterceptor> {

    @Override
    protected CompositedRequestInterceptor loadCurrentComponent(Object configuration, ResponseHandler responseHandler) throws Exception {
        Class<?> configurationClass = configuration.getClass();
        Field retryField = configurationClass.getDeclaredField("requestInterceptors");
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
    public boolean expect(Object configuration, ResponseHandler responseHandler, Class<RequestInterceptor> expectedClass) throws Exception {
        CompositedRequestInterceptor requestInterceptor = loadCurrentComponent(configuration, responseHandler);
        for(RequestInterceptor interceptor : requestInterceptor.getRequestInterceptors()) {
            if (expectedClass.equals(interceptor.getClass()))
                return true;
        }
        return false;
    }
}
