package io.microsphere.spring.cloud.openfeign;

import feign.InvocationHandlerFactory;
import feign.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import static feign.Util.checkNotNull;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class ObservableFeignInvocationHandler implements InvocationHandler {


    private static final Logger log = LoggerFactory.getLogger(ObservableFeignInvocationHandler.class);
    public static FeignComponentAssert<?> componentAssert;
    public static Class expectComponentClass;

    private final Target target;
    private final Map<Method, InvocationHandlerFactory.MethodHandler> dispatch;

    ObservableFeignInvocationHandler(Target target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
        this.target = checkNotNull(target, "target");
        this.dispatch = checkNotNull(dispatch, "dispatch for %s", target);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("equals".equals(method.getName())) {
            try {
                Object otherHandler =
                        args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                return equals(otherHandler);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if ("hashCode".equals(method.getName())) {
            return hashCode();
        } else if ("toString".equals(method.getName())) {
            return toString();
        } else if (!dispatch.containsKey(method)) {
            throw new UnsupportedOperationException(
                    String.format("Method \"%s\" should not be called", method.getName()));
        }

        InvocationHandlerFactory.MethodHandler methodHandler = dispatch.get(method);
        Assert.isTrue(componentAssert.expect(methodHandler, expectComponentClass), "unexpected component");
        log.info("component validation is True");
        return dispatch.get(method).invoke(args);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ObservableFeignInvocationHandler) {
            ObservableFeignInvocationHandler other = (ObservableFeignInvocationHandler) obj;
            return target.equals(other.target);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }

    @Override
    public String toString() {
        return target.toString();
    }
}
