package io.microsphere.spring.cloud.openfeign.components;

import feign.RetryableException;
import feign.Retryer;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignContext;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class DecoratedRetryer extends DecoratedFeignComponent<Retryer> implements Retryer  {

    public DecoratedRetryer(String contextId, FeignContext feignContext, FeignClientProperties clientProperties, Retryer delegate) {
        super(contextId, feignContext, clientProperties, delegate);
    }

    @Override
    protected Class<Retryer> componentType() {
        Class<Retryer> retryerClass = null;
        if (getDefaultConfiguration() != null && getDefaultConfiguration().getRetryer() != null)
            retryerClass = getDefaultConfiguration().getRetryer();

        if (getCurrentConfiguration() != null && getCurrentConfiguration().getRetryer() != null)
            retryerClass = getCurrentConfiguration().getRetryer();

        if (retryerClass != null)
            return retryerClass;
        return Retryer.class;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        Retryer retryer = delegate();
        if (retryer != null)
            retryer.continueOrPropagate(e);
    }

    @Override
    public Retryer clone() {
        return delegate().clone();
    }
}
