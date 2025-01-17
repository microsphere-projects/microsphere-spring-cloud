package io.microsphere.spring.cloud.openfeign.querymapencoder;

import feign.QueryMapEncoder;
import io.microsphere.spring.cloud.openfeign.BaseTest;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
@SpringBootTest(classes = QueryMapEncoderChangedTest.class)
@EnableAutoConfiguration
public class QueryMapEncoderChangedTest extends BaseTest<QueryMapEncoder> {

    @Override
    protected Class<? extends QueryMapEncoder> beforeTestComponentClass() {
        return AQueryMapEncoder.class;
    }

    @Override
    protected FeignComponentAssert<QueryMapEncoder> loadFeignComponentAssert() {
        return new QueryMapEncoderComponentAssert();
    }

    @Override
    protected String afterTestComponentConfigKey() {
        return "spring.cloud.openfeign.client.config.my-client.query-map-encoder";
    }

    @Override
    protected Class<? extends QueryMapEncoder> afterTestComponent() {
        return BQueryMapEncoder.class;
    }
}
