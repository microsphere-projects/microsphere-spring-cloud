package io.microsphere.spring.cloud.openfeign.querymapencoder;

import feign.QueryMapEncoder;
import io.microsphere.logging.Logger;

import java.util.HashMap;
import java.util.Map;

import static io.microsphere.logging.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
public class AQueryMapEncoder implements QueryMapEncoder {


    private static final Logger log = getLogger(AQueryMapEncoder.class);

    @Override
    public Map<String, Object> encode(Object object) {
        return new HashMap<>();
    }
}
