package io.microsphere.spring.cloud.openfeign;

import feign.ResponseHandler;

/**
 * @author <a href="mailto:hanchao@66yunlian.com">韩超</a>
 * @since 1.0.0
 */
public abstract class FeignComponentAssert<T> {

    protected abstract T loadCurrentComponent(Object configuration, ResponseHandler responseHandler) throws Exception;

    public boolean expect(Object configuration, ResponseHandler responseHandler, Class<T> expectedClass) throws Exception {
        T component = loadCurrentComponent(configuration, responseHandler);
        return expectedClass.equals(component.getClass());
    }

}