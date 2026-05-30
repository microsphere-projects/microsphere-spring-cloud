package io.microsphere.spring.cloud.openfeign.components;

import feign.RetryableException;
import feign.Retryer;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignContext;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class DecoratedRetryer extends DecoratedFeignComponent<Retryer> implements Retryer  {

    public DecoratedRetryer(String contextId, FeignContext feignContext, FeignClientProperties clientProperties, Retryer delegate) {
        super(contextId, feignContext, clientProperties, delegate);
    }

    /**
     * Returns the {@link Retryer} implementation class to use when reloading
     * the delegate after a refresh, as configured in {@link FeignClientConfiguration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<? extends Retryer> type = decoratedRetryer.componentType();
     * }</pre>
     *
     * @return the configured {@link Retryer} class, or {@link Retryer.Default} if not configured
     */
    @Override
    protected Class<? extends Retryer> componentType() {
        Class<Retryer> retryerClass = get(FeignClientConfiguration::getRetryer);
        return retryerClass == null ? Default.class : retryerClass;
    }

    /**
     * Continues or propagates the given {@link RetryableException} by delegating
     * to the underlying {@link Retryer} implementation.
     *
     * <p>Example Usage:
     * <pre>{@code
     * try {
     *     decoratedRetryer.continueOrPropagate(retryableException);
     * } catch (RetryableException ex) {
     *     // max retries exceeded
     * }
     * }</pre>
     *
     * @param e the {@link RetryableException} to handle
     */
    @Override
    public void continueOrPropagate(RetryableException e) {
        continueOrPropagate(delegate(), e);
    }

    static void continueOrPropagate(Retryer retryer, RetryableException e) {
        if (retryer != null) {
            retryer.continueOrPropagate(e);
        }
    }

    /**
     * Clones the underlying {@link Retryer} delegate to produce a fresh copy for
     * each request invocation.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Retryer cloned = decoratedRetryer.clone();
     * }</pre>
     *
     * @return a cloned {@link Retryer} instance from the delegate
     */
    @Override
    public Retryer clone() {
        return delegate().clone();
    }
}