package io.microsphere.spring.cloud.openfeign.components;

import feign.QueryMapEncoder;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignContext;

import java.util.Map;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class DecoratedQueryMapEncoder extends DecoratedFeignComponent<QueryMapEncoder> implements QueryMapEncoder {

    public DecoratedQueryMapEncoder(String contextId, FeignContext feignContext, FeignClientProperties clientProperties, QueryMapEncoder delegate) {
        super(contextId, feignContext, clientProperties, delegate);
    }

    @Override
    protected Class<QueryMapEncoder> componentType() {
        Class<QueryMapEncoder> queryMapEncoderClass = null;
        if (getDefaultConfiguration() != null && getDefaultConfiguration().getQueryMapEncoder() != null)
            queryMapEncoderClass = getDefaultConfiguration().getQueryMapEncoder();

        if (getCurrentConfiguration() != null && getCurrentConfiguration().getQueryMapEncoder() != null)
            queryMapEncoderClass = getCurrentConfiguration().getQueryMapEncoder();

        if (queryMapEncoderClass != null)
            return queryMapEncoderClass;
        return QueryMapEncoder.class;
    }

    @Override
    public Map<String, Object> encode(Object object) {
        return delegate().encode(object);
    }
}
