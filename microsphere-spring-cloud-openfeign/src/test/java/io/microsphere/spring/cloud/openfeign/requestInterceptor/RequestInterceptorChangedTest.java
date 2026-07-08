package io.microsphere.spring.cloud.openfeign.requestInterceptor;

import feign.RequestInterceptor;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.autorefresh.BaseTest;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 *  @since 1.0.0
 */
@SpringBootTest(classes = RequestInterceptorChangedTest.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableAutoConfiguration
class RequestInterceptorChangedTest extends BaseTest<RequestInterceptor> {

    @Override
    protected Class<? extends RequestInterceptor> beforeTestComponentClass() {
        return ARequestInterceptor.class;
    }

    @Override
    protected FeignComponentAssert<RequestInterceptor> loadFeignComponentAssert() {
        return new RequestInterceptorComponentAssert();
    }

    @Override
    protected String afterTestComponentConfigKey() {
        return "feign.client.config.my-client.request-interceptors[0]";
    }

    @Override
    protected Class<? extends RequestInterceptor> afterTestComponent() {
        return BRequestInterceptor.class;
    }

}