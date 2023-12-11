package io.microsphere.spring.cloud.client.service.registry.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

import static io.microsphere.spring.cloud.commons.constants.CommonsPropertyConstants.*;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ConditionalOnProperty(name = {MULTIPLE_REGISTRATION_ENABLED_PROPERTY_NAME,
        MULTIPLE_REGISTRATION_DEFAULT_REGISTRATION_PROPERTY_NAME,
        MULTIPLE_REGISTRATION_DEFAULT_REGISTRY_PROPERTY_NAME
})
public @interface ConditionalOnMultipleRegistrationEnabled {

}
