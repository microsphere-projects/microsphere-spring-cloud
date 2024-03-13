package io.microsphere.spring.cloud.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
@FeignClient(contextId = "aaa", name = "aaa")
public interface BaseClient {

    @GetMapping("echo")
    public String echo(@RequestBody String value);

}
