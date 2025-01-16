package io.microsphere.spring.cloud.openfeign.components;

import feign.QueryMapEncoder;
import io.microsphere.logging.Logger;
import io.microsphere.spring.cloud.openfeign.FeignComponentProvider;
import org.springframework.cloud.openfeign.FeignClientProperties;

import java.lang.invoke.MethodHandle;
import java.util.Map;

import static io.microsphere.invoke.MethodHandleUtils.findVirtual;
import static io.microsphere.logging.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class DecoratedQueryMapEncoder extends DecoratedFeignComponent<QueryMapEncoder> implements QueryMapEncoder {

    private static final Logger logger = getLogger(DecoratedQueryMapEncoder.class);

    private static final String getQueryMapEncoderMethodName = "getQueryMapEncoder";

    private static final MethodHandle getQueryMapEncoderMethodHandle = findVirtual(FeignClientProperties.FeignClientConfiguration.class, getQueryMapEncoderMethodName);

    public DecoratedQueryMapEncoder(String contextId, FeignComponentProvider feignComponentProvider, FeignClientProperties clientProperties, QueryMapEncoder delegate) {
        super(contextId, feignComponentProvider, clientProperties, delegate);
    }

    @Override
    protected Class<QueryMapEncoder> componentType() {
        Class<QueryMapEncoder> queryMapEncoderClass = getQueryMapEncoder(getCurrentConfiguration());
        if (queryMapEncoderClass == null) {
            queryMapEncoderClass = getQueryMapEncoder(getDefaultConfiguration());
        }
        return queryMapEncoderClass == null ? QueryMapEncoder.class : queryMapEncoderClass;
    }

    private Class<QueryMapEncoder> getQueryMapEncoder(FeignClientProperties.FeignClientConfiguration feignClientConfiguration) {
        if (feignClientConfiguration == null || getQueryMapEncoderMethodHandle == null) {
            return null;
        }
        Class<QueryMapEncoder> queryMapEncoderClass = null;
        try {
            queryMapEncoderClass = (Class<QueryMapEncoder>) getQueryMapEncoderMethodHandle.invokeExact(feignClientConfiguration);
        } catch (Throwable e) {
            if (logger.isWarnEnabled()) {
                logger.warn("FeignClientProperties.FeignClientConfiguration#getQueryMapEncoder() method can't be invoked , instance : {}", feignClientConfiguration);
            }
        }
        return queryMapEncoderClass;
    }

    @Override
    public Map<String, Object> encode(Object object) {
        return delegate().encode(object);
    }
}
