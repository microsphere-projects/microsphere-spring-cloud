package io.microsphere.spring.cloud.openfeign.errordecoder;

import feign.codec.ErrorDecoder;
import io.microsphere.spring.cloud.openfeign.BaseTest;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
@SpringBootTest(classes = ErrorDecoderChangedTest.class)
@EnableAutoConfiguration
class ErrorDecoderChangedTest extends BaseTest<ErrorDecoder> {

    @Override
    protected Class<? extends ErrorDecoder> beforeTestComponentClass() {
        return AErrorDecoder.class;
    }

    @Override
    protected FeignComponentAssert<ErrorDecoder> loadFeignComponentAssert() {
        return new ErrorDecoderComponentAssert();
    }

    @Override
    protected String afterTestComponentConfigKey() {
        return "feign.client.config.my-client.error-decoder";
    }

    @Override
    protected Class<? extends ErrorDecoder> afterTestComponent() {
        return BErrorEncoder.class;
    }
}
