package io.microsphere.spring.cloud.openfeign;

import feign.MethodHandlerConfiguration;
import feign.ResponseHandler;

/**
 * @author <a href="mailto:hanchao@66yunlian.com">韩超</a>
 * @since 0.0.1
 */
public abstract class FeignComponentAssert<T> {


    protected abstract T loadCurrentComponent(MethodHandlerConfiguration configuration, ResponseHandler responseHandler) throws Exception;

    public boolean expect(MethodHandlerConfiguration configuration, ResponseHandler responseHandler, Class<T> expectedClass) throws Exception {
        T component = loadCurrentComponent(configuration, responseHandler);
        return expectedClass.equals(component.getClass());
    }

}
