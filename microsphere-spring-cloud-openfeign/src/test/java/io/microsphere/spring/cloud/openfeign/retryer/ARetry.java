package io.microsphere.spring.cloud.openfeign.retryer;

import feign.RetryableException;
import feign.Retryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
public class ARetry implements Retryer {

    private static final Logger log = LoggerFactory.getLogger(ARetry.class);

    @Override
    public void continueOrPropagate(RetryableException e) {
        log.trace("Attempting to propagate exception", e);
    }

    @Override
    public Retryer clone() {
        return this;
    }
}
