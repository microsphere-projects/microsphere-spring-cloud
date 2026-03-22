package io.microsphere.spring.cloud.openfeign.components;

import feign.QueryMapEncoder;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.cloud.openfeign.support.PageableSpringQueryMapEncoder;

import java.util.Map;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class DecoratedQueryMapEncoder extends DecoratedFeignComponent<QueryMapEncoder> implements QueryMapEncoder {

    public DecoratedQueryMapEncoder(String contextId, FeignContext feignContext, FeignClientProperties clientProperties, QueryMapEncoder delegate) {
        super(contextId, feignContext, clientProperties, delegate);
    }

    /**
     * Returns the {@link QueryMapEncoder} implementation class to use when reloading
     * the delegate after a refresh. Defaults to {@link PageableSpringQueryMapEncoder}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<? extends QueryMapEncoder> type = decoratedQueryMapEncoder.componentType();
     * }</pre>
     *
     * @return the {@link PageableSpringQueryMapEncoder} class
     */
    @Override
    protected Class<? extends QueryMapEncoder> componentType() {
        return PageableSpringQueryMapEncoder.class;
    }

    /**
     * Encodes the given object into a query parameter map by delegating to the
     * underlying {@link QueryMapEncoder} implementation.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Map<String, Object> queryParams = decoratedQueryMapEncoder.encode(myQueryObject);
     * }</pre>
     *
     * @param object the object to encode as query parameters
     * @return a {@link Map} of query parameter names to values
     */
    @Override
    public Map<String, Object> encode(Object object) {
        return delegate().encode(object);
    }
}