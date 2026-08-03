package io.microsphere.spring.cloud.openfeign.requestInterceptor;

import feign.RequestInterceptor;
import feign.ResponseHandler;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.CompositedRequestInterceptor;

import java.util.List;

import static io.microsphere.reflect.FieldUtils.getFieldValue;

/**
 * @author <a href="mailto:hanchao@66yunlian.com">韩超</a>
 * @since 1.0.0
 */
public class RequestInterceptorComponentAssert extends FeignComponentAssert<RequestInterceptor> {

    @Override
    protected CompositedRequestInterceptor loadCurrentComponent(Object configuration, ResponseHandler responseHandler) throws Exception {
        List<RequestInterceptor> retryer = getFieldValue(true, configuration, "requestInterceptors");
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
        for (RequestInterceptor interceptor : requestInterceptor.getRequestInterceptors()) {
            if (expectedClass.equals(interceptor.getClass()))
                return true;
        }
        return false;
    }
}