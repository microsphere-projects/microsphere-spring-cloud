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

    @Override
    protected Class<? extends QueryMapEncoder> componentType() {
        return PageableSpringQueryMapEncoder.class;
    }

    @Override
    public Map<String, Object> encode(Object object) {
        return delegate().encode(object);
    }
}