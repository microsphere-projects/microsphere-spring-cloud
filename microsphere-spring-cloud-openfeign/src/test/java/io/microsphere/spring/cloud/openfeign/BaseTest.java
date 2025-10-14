package io.microsphere.spring.cloud.openfeign;

import io.microsphere.spring.cloud.openfeign.autoconfigure.EnableFeignAutoRefresh;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "feign.client.config.default.encoder=io.microsphere.spring.cloud.openfeign.encoder.AEncoder",
        "feign.client.config.default.error-decoder=io.microsphere.spring.cloud.openfeign.errordecoder.AErrorDecoder",
        "feign.client.config.default.query-map-encoder=io.microsphere.spring.cloud.openfeign.querymapencoder.AQueryMapEncoder",
        "feign.client.config.default.retryer=io.microsphere.spring.cloud.openfeign.retryer.ARetry",
        "feign.client.config.default.decoder=io.microsphere.spring.cloud.openfeign.decoder.ADecoder",
        "feign.client.config.default.request-interceptors[0]=io.microsphere.spring.cloud.openfeign.requestInterceptor.ARequestInterceptor",
        "feign.client.config.default.default-request-headers.app=my-app",
        "feign.client.config.default.default-query-parameters.sign=my-sign",
})
@ComponentScan(basePackages = "io.microsphere.spring.cloud.openfeign")
@EnableFeignClients(clients = BaseClient.class)
@EnableFeignAutoRefresh
public abstract class BaseTest<T> {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private Environment environment;
    @Autowired
    private BaseClient client;

    protected abstract String afterTestComponentConfigKey();
    protected abstract Class<? extends T> beforeTestComponentClass();
    protected abstract Class<? extends T> afterTestComponent();
    protected abstract FeignComponentAssert<T> loadFeignComponentAssert();

    public void replaceConfig() {
        final String key = afterTestComponentConfigKey();
        Set<String> keys = Collections.singleton(key);
        final Class<?> className = afterTestComponent();
        MutablePropertySources propertySources = ((ConfigurableEnvironment)this.environment).getPropertySources();
        Map<String, Object> map = new HashMap<>();
        log.trace("replacing config key {} with value {}", key, className.getName());
        map.put(key, className);
        propertySources.addFirst(new MapPropertySource("after", map));

        EnvironmentChangeEvent event = new EnvironmentChangeEvent(keys);

        triggerRefreshEvent();

        this.publisher.publishEvent(event);
    }

    @Test
    protected void testInternal() {
        ObservableFeignInvocationHandler.componentAssert = loadFeignComponentAssert();

        ObservableFeignInvocationHandler.expectComponentClass = beforeTestComponentClass();
        try {
            this.client.echo("hello", "1.0");
        } catch (Exception ignored) {
        }
        replaceConfig();

        ObservableFeignInvocationHandler.expectComponentClass = afterTestComponent();
        try {
            this.client.echo("world", "1.0");
        } catch (Exception ignored) {

        }
    }

    protected void triggerRefreshEvent() {
        this.publisher.publishEvent(new RefreshEvent(new Object(), new Object(), "test"));
    }

}
