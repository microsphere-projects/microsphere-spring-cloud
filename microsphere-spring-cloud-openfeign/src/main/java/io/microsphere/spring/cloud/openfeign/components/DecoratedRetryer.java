package io.microsphere.spring.cloud.openfeign.components;

import feign.RetryableException;
import feign.Retryer;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClientSpecification;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class DecoratedRetryer extends DecoratedFeignComponent<Retryer> implements Retryer {

    /**
     * Constructs a {@link DecoratedRetryer} wrapping the given {@link Retryer} delegate.
     *
     * <p>Example Usage:
     * <pre>{@code
     * DecoratedRetryer retryer = new DecoratedRetryer(
     *     "my-client", contextFactory, clientProperties, new Retryer.Default());
     * }</pre>
     *
     * @param contextId        the Feign client context ID
     * @param contextFactory   the {@link NamedContextFactory} for resolving per-client contexts
     * @param clientProperties the {@link FeignClientProperties} for configuration lookup
     * @param delegate         the original {@link Retryer} to delegate to
     */
    public DecoratedRetryer(String contextId, NamedContextFactory<FeignClientSpecification> contextFactory, FeignClientProperties clientProperties, Retryer delegate) {
        super(contextId, contextFactory, clientProperties, delegate);
    }

    /**
     * Returns the configured {@link Retryer} class from {@link FeignClientConfiguration},
     * falling back to {@link Retryer} if not configured.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<? extends Retryer> type = decoratedRetryer.componentType();
     * }</pre>
     *
     * @return the {@link Retryer} component type class
     */
    @Override
    protected Class<? extends Retryer> componentType() {
        Class<Retryer> retryerClass = get(FeignClientConfiguration::getRetryer);
        return retryerClass == null ? Retryer.class : retryerClass;
    }

    /**
     * Continues or propagates the retry by delegating to the underlying {@link Retryer}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * try {
     *     // Feign call
     * } catch (RetryableException e) {
     *     decoratedRetryer.continueOrPropagate(e);
     * }
     * }</pre>
     *
     * @param e the {@link RetryableException} to evaluate for retry
     */
    @Override
    public void continueOrPropagate(RetryableException e) {
        continueOrPropagate(delegate(), e);
    }

    /**
     * Delegates the continue-or-propagate decision to the given {@link Retryer} if it
     * is not {@code null}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * DecoratedRetryer.continueOrPropagate(retryerInstance, retryableException);
     * }</pre>
     *
     * @param retryer the {@link Retryer} to delegate to, may be {@code null}
     * @param e       the {@link RetryableException} to evaluate
     */
    static void continueOrPropagate(Retryer retryer, RetryableException e) {
        if (retryer != null) {
            retryer.continueOrPropagate(e);
        }
    }

    /**
     * Returns a clone of the delegate {@link Retryer}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Retryer cloned = decoratedRetryer.clone();
     * }</pre>
     *
     * @return a cloned {@link Retryer} instance
     */
    @Override
    public Retryer clone() {
        return delegate().clone();
    }
}