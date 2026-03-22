package io.microsphere.spring.cloud.openfeign.components;

import feign.QueryMapEncoder;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClientSpecification;
import org.springframework.cloud.openfeign.support.PageableSpringQueryMapEncoder;

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

    /**
     * Constructs a {@link DecoratedQueryMapEncoder} wrapping the given {@link QueryMapEncoder} delegate.
     *
     * <p>Example Usage:
     * <pre>{@code
     * DecoratedQueryMapEncoder encoder = new DecoratedQueryMapEncoder(
     *     "my-client", contextFactory, clientProperties, new QueryMapEncoder.Default());
     * }</pre>
     *
     * @param contextId        the Feign client context ID
     * @param contextFactory   the {@link NamedContextFactory} for resolving per-client contexts
     * @param clientProperties the {@link FeignClientProperties} for configuration lookup
     * @param delegate         the original {@link QueryMapEncoder} to delegate to
     */
    public DecoratedQueryMapEncoder(String contextId, NamedContextFactory<FeignClientSpecification> contextFactory, FeignClientProperties clientProperties, QueryMapEncoder delegate) {
        super(contextId, contextFactory, clientProperties, delegate);
    }

    /**
     * Returns the configured {@link QueryMapEncoder} class from {@link FeignClientConfiguration},
     * falling back to {@link PageableSpringQueryMapEncoder} if not configured.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<? extends QueryMapEncoder> type = decoratedQueryMapEncoder.componentType();
     * }</pre>
     *
     * @return the {@link QueryMapEncoder} component type class
     */
    @Override
    protected Class<? extends QueryMapEncoder> componentType() {
        Class<QueryMapEncoder> queryMapEncoderClass = getQueryMapEncoder(getCurrentConfiguration());
        if (queryMapEncoderClass == null) {
            queryMapEncoderClass = getQueryMapEncoder(getDefaultConfiguration());
        }
        return queryMapEncoderClass == null ? PageableSpringQueryMapEncoder.class : queryMapEncoderClass;
    }

    private Class<QueryMapEncoder> getQueryMapEncoder(FeignClientConfiguration feignClientConfiguration) {
        return getQueryMapEncoder(getQueryMapEncoderMethodHandle, feignClientConfiguration);
    }

    /**
     * Retrieves the {@link QueryMapEncoder} class from a {@link FeignClientConfiguration}
     * using a {@link MethodHandle} for compatibility across Spring Cloud versions.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<QueryMapEncoder> encoderClass = DecoratedQueryMapEncoder.getQueryMapEncoder(
     *     getQueryMapEncoderMethodHandle, feignClientConfiguration);
     * }</pre>
     *
     * @param methodHandle             the {@link MethodHandle} to invoke {@code getQueryMapEncoder}
     * @param feignClientConfiguration the configuration to read from
     * @return the configured {@link QueryMapEncoder} class, or {@code null} if unavailable
     */
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

    /**
     * Encodes the given object into a query parameter map by delegating to the
     * underlying {@link QueryMapEncoder}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Map<String, Object> queryParams = decoratedQueryMapEncoder.encode(myQueryObject);
     * }</pre>
     *
     * @param object the object to encode as query parameters
     * @return a map of query parameter names to values
     */
    @Override
    public Map<String, Object> encode(Object object) {
        return delegate().encode(object);
    }
}