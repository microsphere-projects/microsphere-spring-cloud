package io.microsphere.spring.cloud.openfeign.requestInterceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class ARequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        System.out.println(getClass() + ": A is working...");
    }
}
