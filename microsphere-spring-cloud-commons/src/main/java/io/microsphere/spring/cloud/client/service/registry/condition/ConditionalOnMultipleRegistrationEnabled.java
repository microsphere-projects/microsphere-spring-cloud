package io.microsphere.spring.cloud.client.service.registry.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

import static io.microsphere.spring.cloud.commons.constants.CommonsPropertyConstants.MULTIPLE_REGISTRATION_ENABLED_PROPERTY_NAME;
/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ConditionalOnProperty(name = MULTIPLE_REGISTRATION_ENABLED_PROPERTY_NAME)
public @interface ConditionalOnMultipleRegistrationEnabled {


    /**
     * The string representation of the expected value for the properties. If not
     * specified, the property must <strong>not</strong> be equal to {@code false}.
     *
     * @return the expected value
     */
    @AliasFor(annotation = ConditionalOnProperty.class, attribute = "havingValue")
    String havingValue() default "";

    /**
     * Specify if the condition should match if the property is not set. Defaults to
     * {@code true}.
     *
     * @return if the condition should match if the property is missing
     */
    @AliasFor(annotation = ConditionalOnProperty.class, attribute = "matchIfMissing")
    boolean matchIfMissing() default true;
}
