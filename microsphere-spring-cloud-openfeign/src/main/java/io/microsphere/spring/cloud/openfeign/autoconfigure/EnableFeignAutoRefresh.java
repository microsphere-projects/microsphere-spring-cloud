package io.microsphere.spring.cloud.openfeign.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Enable Feign Auto Refresh
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see FeignClientAutoRefreshAutoConfiguration
 * @since 0.0.1
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
@Inherited
@Import(EnableFeignAutoRefresh.Marker.class)
public @interface EnableFeignAutoRefresh {

    class Marker {
    }
}
