package io.microsphere.spring.cloud.openfeign.requestInterceptor;

import feign.InvocationHandlerFactory;
import feign.RequestInterceptor;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.CompositedRequestInterceptor;

import java.util.List;

import static io.microsphere.reflect.FieldUtils.getFieldValue;

/**
 * @author <a href="mailto:hanchao@66yunlian.com">韩超</a>
 * @since 0.0.1
 */
public class RequestInterceptorComponentAssert extends FeignComponentAssert<RequestInterceptor> {

    @Override
    protected CompositedRequestInterceptor loadCurrentComponent(InvocationHandlerFactory.MethodHandler methodHandler) throws Exception {
        List<RequestInterceptor> retryer = getFieldValue(methodHandler, "requestInterceptors");
        for (RequestInterceptor interceptor : retryer) {
            if (interceptor instanceof CompositedRequestInterceptor) {
                return (CompositedRequestInterceptor) interceptor;
            }
        }
        return null;
    }

    @Override
    public boolean expect(InvocationHandlerFactory.MethodHandler methodHandler, Class<RequestInterceptor> expectedClass) throws Exception {
        CompositedRequestInterceptor requestInterceptor = loadCurrentComponent(methodHandler);
        for (RequestInterceptor interceptor : requestInterceptor.getRequestInterceptors()) {
            if (expectedClass.equals(interceptor.getClass()))
                return true;
        }
        return false;
    }
}
