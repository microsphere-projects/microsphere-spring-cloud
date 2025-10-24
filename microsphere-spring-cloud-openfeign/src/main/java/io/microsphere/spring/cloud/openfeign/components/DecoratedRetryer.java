package io.microsphere.spring.cloud.openfeign.components;

import feign.RetryableException;
import feign.Retryer;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClientSpecification;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class DecoratedRetryer extends DecoratedFeignComponent<Retryer> implements Retryer {

    public DecoratedRetryer(String contextId, NamedContextFactory<FeignClientSpecification> contextFactory, FeignClientProperties clientProperties, Retryer delegate) {
        super(contextId, contextFactory, clientProperties, delegate);
    }

    @Override
    protected Class<Retryer> componentType() {
        Class<Retryer> retryerClass = get(FeignClientConfiguration::getRetryer);
        return retryerClass == null ? Retryer.class : retryerClass;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        continueOrPropagate(delegate(), e);
    }

    static void continueOrPropagate(Retryer retryer, RetryableException e) {
        if (retryer != null) {
            retryer.continueOrPropagate(e);
        }
    }

    @Override
    public Retryer clone() {
        return delegate().clone();
    }
}
