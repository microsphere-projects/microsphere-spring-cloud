package io.microsphere.spring.cloud.openfeign.components;

import feign.QueryMapEncoder;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClientSpecification;

import java.lang.invoke.MethodHandle;
import java.util.Map;

import static io.microsphere.invoke.MethodHandleUtils.findVirtual;
import static io.microsphere.invoke.MethodHandleUtils.handleInvokeExactFailure;
import static io.microsphere.invoke.MethodHandlesLookupUtils.NOT_FOUND_METHOD_HANDLE;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class DecoratedQueryMapEncoder extends DecoratedFeignComponent<QueryMapEncoder> implements QueryMapEncoder {

    private static final String getQueryMapEncoderMethodName = "getQueryMapEncoder";

    static final MethodHandle getQueryMapEncoderMethodHandle = findVirtual(FeignClientConfiguration.class, getQueryMapEncoderMethodName);

    public DecoratedQueryMapEncoder(String contextId, NamedContextFactory<FeignClientSpecification> contextFactory, FeignClientProperties clientProperties, QueryMapEncoder delegate) {
        super(contextId, contextFactory, clientProperties, delegate);
    }

    @Override
    protected Class<QueryMapEncoder> componentType() {
        Class<QueryMapEncoder> queryMapEncoderClass = getQueryMapEncoder(getCurrentConfiguration());
        if (queryMapEncoderClass == null) {
            queryMapEncoderClass = getQueryMapEncoder(getDefaultConfiguration());
        }
        return queryMapEncoderClass == null ? QueryMapEncoder.class : queryMapEncoderClass;
    }

    private Class<QueryMapEncoder> getQueryMapEncoder(FeignClientConfiguration feignClientConfiguration) {
        return getQueryMapEncoder(getQueryMapEncoderMethodHandle, feignClientConfiguration);
    }

    static Class<QueryMapEncoder> getQueryMapEncoder(MethodHandle methodHandle, FeignClientConfiguration feignClientConfiguration) {
        if (methodHandle == NOT_FOUND_METHOD_HANDLE) {
            return null;
        }
        Class<QueryMapEncoder> queryMapEncoderClass = null;
        try {
            queryMapEncoderClass = (Class<QueryMapEncoder>) getQueryMapEncoderMethodHandle.invokeExact(feignClientConfiguration);
        } catch (Throwable e) {
            handleInvokeExactFailure(e, getQueryMapEncoderMethodHandle, feignClientConfiguration);
        }
        return queryMapEncoderClass;
    }

    @Override
    public Map<String, Object> encode(Object object) {
        return delegate().encode(object);
    }
}