package io.microsphere.spring.cloud.openfeign.querymapencoder;

import feign.QueryMapEncoder;
import io.microsphere.spring.cloud.openfeign.BaseTest;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
@SpringBootTest(classes = QueryMapEncoderChangedTest.class)
public class QueryMapEncoderChangedTest extends BaseTest<QueryMapEncoder> {

    @Override
    protected String afterTestComponentConfigKey() {
        return "feign.client.config.my-client.query-map-encoder";
    }

    @Override
    protected Class<? extends QueryMapEncoder> afterTestComponent() {
        return BQueryMapEncoder.class;
    }
}
