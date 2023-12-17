package io.microsphere.spring.cloud.context;

import io.microsphere.spring.beans.factory.annotation.EnableConfigurationBeanBinding;
import io.microsphere.spring.cloud.context.autoconfigure.ConfigurationBeanRefresherAutoConfiguration;
import io.microsphere.spring.context.event.ConfigurationBeanRefreshedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.*;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ConfigurationBeanRefresherTest.class,
        ConfigurationBeanRefresherAutoConfiguration.class
})
@TestPropertySource(
        properties = {
            "usr.id=m",
            "usr.name=mercyblitz",
            "usr.age=34",
            "users.a.name=name-a",
            "users.a.age=1",
            "users.b.name=name-b",
            "users.b.age=2"
        }
)
@EnableConfigurationBeanBinding(prefix = "usr", type = User.class, refreshStrategy = EnableConfigurationBeanBinding.ConfigurationBeanRefreshStrategy.REBIND)
@EnableConfigurationBeanBinding(prefix = "users", type = User.class, multiple = true, refreshStrategy = EnableConfigurationBeanBinding.ConfigurationBeanRefreshStrategy.REINITIALIZE)
public class ConfigurationBeanRefresherTest implements ApplicationListener<ConfigurationBeanRefreshedEvent> {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    private ConfigurableEnvironment environment;

    @Autowired
    private ConfigurationBeanRefresher refresher;

    private final MapPropertySource override = new MapPropertySource("override", new HashMap<>());

    public ConfigurableEnvironment getEnvironment() {
        if (environment == null)
            environment = (ConfigurableEnvironment) this.applicationContext.getEnvironment();
        return environment;
    }

    protected void overrideConfigurations(String key, String value) {
        MutablePropertySources propertySources = getEnvironment().getPropertySources();
        MapPropertySource propertySource = (MapPropertySource) propertySources.get("modify");
        if (propertySource == null) {
            propertySources.addFirst(override);
        }
        override.getSource().put(key, value);
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(this.applicationContext, Collections.singleton(key)));
    }

    @BeforeEach
    void setUp() {
        this.applicationContext.addApplicationListener(this);
    }

    @org.junit.jupiter.api.Test
    void refreshAll() {
        refresher.refreshAll();
    }

    @org.junit.jupiter.api.Test
    void refreshByKeys() {

        User user = this.applicationContext.getBean("m", User.class);
        assertEquals(34, user.getAge());
        overrideConfigurations("usr.age", "26");

        assertEquals(26, user.getAge());

    }

    @Override
    public void onApplicationEvent(ConfigurationBeanRefreshedEvent event) {
        System.out.println("refreshed bean : " + event.getBeanName());
    }
}