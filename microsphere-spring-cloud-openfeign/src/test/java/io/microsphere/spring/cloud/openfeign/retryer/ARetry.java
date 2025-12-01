package io.microsphere.spring.cloud.openfeign.retryer;

import feign.RetryableException;
import feign.Retryer;
import io.microsphere.logging.Logger;
import static io.microsphere.logging.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
public class ARetry implements Retryer {

    private static final Logger log = getLogger(ARetry.class);

    @Override
    public void continueOrPropagate(RetryableException e) {
        log.trace("Attempting to propagate exception", e);
    }

    @Override
    public Retryer clone() {
        return this;
    }
}
