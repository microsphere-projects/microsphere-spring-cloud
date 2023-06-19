package io.microsphere.spring.cloud.openfeign;

import feign.Feign;
import feign.Target;
import io.microsphere.spring.cloud.openfeign.autorefresh.FeignComponentRegistry;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.cloud.openfeign.Targeter;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class DelegatingTargeter implements Targeter {

    private final Targeter delegate;

    public DelegatingTargeter(Targeter delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> T target(FeignClientFactoryBean factory, Feign.Builder feign, FeignContext context, Target.HardCodedTarget<T> target) {
        if (feign instanceof RefreshableBuilder) {
            RefreshableBuilder refreshableBuilder = (RefreshableBuilder) feign;
            refreshableBuilder.setClientName(factory.getContextId());
        }
        return delegate.target(factory, feign, context, target);
    }
}
