package io.microsphere.spring.cloud.openfeign.retryer;

import feign.RetryableException;
import feign.Retryer;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 *  @since 1.0.0
 */
public class BRetry implements Retryer {

    @Override
    public void continueOrPropagate(RetryableException e) {

    }

    @Override
    public Retryer clone() {
        return this;
    }
}