package io.microsphere.spring.cloud.openfeign.querymapencoder;

import feign.QueryMapEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
public class BQueryMapEncoder implements QueryMapEncoder {


    private static final Logger log = LoggerFactory.getLogger(BQueryMapEncoder.class);

    @Override
    public Map<String, Object> encode(Object object) {
        log.trace("AQueryMapEncoder.encode({})", object);
        return new HashMap<>();
    }
}
