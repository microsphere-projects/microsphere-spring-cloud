package io.microsphere.spring.cloud.openfeign;

import feign.InvocationHandlerFactory;

/**
 * @author <a href="mailto:hanchao@66yunlian.com">韩超</a>
 * @since 0.0.1
 */
public abstract class FeignComponentAssert<T> {


    protected abstract T loadCurrentComponent(InvocationHandlerFactory.MethodHandler methodHandler) throws Exception;

    public boolean expect(InvocationHandlerFactory.MethodHandler methodHandler, Class<T> expectedClass) throws Exception {
        T component = loadCurrentComponent(methodHandler);
        return expectedClass.equals(component.getClass());
    }

}
