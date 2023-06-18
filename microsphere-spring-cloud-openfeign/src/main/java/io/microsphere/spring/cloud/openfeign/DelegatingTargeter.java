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
    private final BeanFactory beanFactory;
    private final FeignComponentRegistry registry;

    public DelegatingTargeter(BeanFactory beanFactory, FeignComponentRegistry registry, Targeter delegate) {
        this.beanFactory = beanFactory;
        this.delegate = delegate;
        this.registry = registry;
    }

    @Override
    public <T> T target(FeignClientFactoryBean factory, Feign.Builder feign, FeignContext context, Target.HardCodedTarget<T> target) {
        if (feign instanceof RefreshableBuilder) {
            RefreshableBuilder refreshableBuilder = (RefreshableBuilder) feign;
            refreshableBuilder.setClientName(factory.getContextId());
        }
        return feign.target(target);
    }
}
