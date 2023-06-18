package io.microsphere.spring.cloud.openfeign.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({FeignClientAutoRefreshConfiguration.class})
public @interface EnableFeignAutoRefresh {
}
