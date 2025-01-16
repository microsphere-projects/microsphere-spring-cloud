package io.microsphere.spring.cloud.openfeign.errordecoder;

import feign.codec.ErrorDecoder;
import io.microsphere.spring.cloud.openfeign.BaseTest;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
@SpringBootTest(classes = ErrorDecoderChangedTest.class)
@EnableAutoConfiguration
public class ErrorDecoderChangedTest extends BaseTest<ErrorDecoder> {

    @Override
    protected String afterTestComponentConfigKey() {
        return "spring.cloud.openfeign.client.config.my-client.error-decoder";
    }

    @Override
    protected Class<? extends ErrorDecoder> afterTestComponent() {
        return AErrorDecoder.class;
    }
}
