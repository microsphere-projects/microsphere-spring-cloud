package io.microsphere.spring.cloud.openfeign.components;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class NoOpRequestInterceptor implements RequestInterceptor {

    public static final NoOpRequestInterceptor INSTANCE = new NoOpRequestInterceptor();

    private NoOpRequestInterceptor() {
    }

    @Override
    public void apply(RequestTemplate template) {
        //no op
    }
}