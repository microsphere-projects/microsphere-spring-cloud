package io.microsphere.spring.cloud.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
@FeignClient(contextId = "my-client", name = "my-client")
public interface BaseClient {

    @GetMapping("echo")
    public String echo(@RequestBody String value, @RequestParam("version") String version);

}
