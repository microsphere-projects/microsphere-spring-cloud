package io.microsphere.spring.cloud.openfeign;

import feign.Capability;
import feign.InvocationHandlerFactory;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class MockCapability implements Capability {

    @Override
    public InvocationHandlerFactory enrich(InvocationHandlerFactory invocationHandlerFactory) {
        return ObservableFeignInvocationHandler::new;
    }


}
