package io.microsphere.spring.cloud.openfeign.requestInterceptor;

import feign.RequestInterceptor;
import io.microsphere.spring.cloud.openfeign.BaseTest;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
@SpringBootTest(classes = RequestInterceptorChangedTest.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableAutoConfiguration
public class RequestInterceptorChangedTest extends BaseTest<RequestInterceptor> {

    @Override
    protected String afterTestComponentConfigKey() {
        return "spring.cloud.openfeign.client.config.my-client.request-interceptors[0]";
    }

    @Override
    protected Class<? extends RequestInterceptor> afterTestComponent() {
        return BRequestInterceptor.class;
    }


}
