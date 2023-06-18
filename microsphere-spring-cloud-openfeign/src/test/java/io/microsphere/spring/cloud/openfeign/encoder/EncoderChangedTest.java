package io.microsphere.spring.cloud.openfeign.encoder;

import io.microsphere.spring.cloud.openfeign.BaseClient;
import io.microsphere.spring.cloud.openfeign.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class EncoderChangedTest extends BaseTest {

    @Autowired
    private Environment environment;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private BaseClient baseClient;

    @Test
    public void testEncoderChange() {
        try {
            baseClient.echo("aaa");

        } catch (Exception ignored) {
        }
        applyEnvironmentChange();
        try {
            baseClient.echo("bbb");
        } catch (Exception ignored) {

        }
    }

    private void applyEnvironmentChange() {
        Set<String> keys = Collections.singleton("feign.client.config.aaa.encoder");
        MutablePropertySources propertySources = ((ConfigurableEnvironment)this.environment).getPropertySources();
        Map<String, Object> map = new HashMap<>();
        System.out.println("替换encoder: BEncoder");
        map.put("feign.client.config.aaa.encoder", BEncoder.class);
        propertySources.addFirst(new MapPropertySource("addition", map));

        EnvironmentChangeEvent event = new EnvironmentChangeEvent(keys);

        this.eventPublisher.publishEvent(new RefreshEvent(new Object(), new Object(), "test"));

        this.eventPublisher.publishEvent(event);
    }


}
