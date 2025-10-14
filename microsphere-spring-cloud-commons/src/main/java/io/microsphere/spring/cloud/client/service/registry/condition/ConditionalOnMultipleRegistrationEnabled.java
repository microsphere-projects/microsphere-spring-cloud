package io.microsphere.spring.cloud.client.service.registry.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.microsphere.spring.cloud.commons.constants.CommonsPropertyConstants.MULTIPLE_REGISTRATION_ENABLED_PROPERTY_NAME;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Conditional @Conditional} that checks whether the multiple service registry enabled
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Documented
@ConditionalOnProperty(name = MULTIPLE_REGISTRATION_ENABLED_PROPERTY_NAME)
public @interface ConditionalOnMultipleRegistrationEnabled {

}
