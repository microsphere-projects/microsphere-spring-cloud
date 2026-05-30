package io.microsphere.spring.cloud.openfeign.components;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class NoOpRequestInterceptor implements RequestInterceptor {

    /**
     * A no-operation {@link RequestInterceptor} that does nothing when applied.
     * Used as a placeholder to ensure at least one interceptor is registered.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RequestInterceptor noOp = NoOpRequestInterceptor.INSTANCE;
     * noOp.apply(requestTemplate); // does nothing
     * }</pre>
     */
    public static final NoOpRequestInterceptor INSTANCE = new NoOpRequestInterceptor();

    /**
     * Applies this interceptor to the given {@link RequestTemplate}. This implementation
     * intentionally performs no operation.
     *
     * <p>Example Usage:
     * <pre>{@code
     * NoOpRequestInterceptor.INSTANCE.apply(requestTemplate);
     * }</pre>
     *
     * @param template the {@link RequestTemplate} (ignored)
     */
    @Override
    public void apply(RequestTemplate template) {
        //no op
    }
}