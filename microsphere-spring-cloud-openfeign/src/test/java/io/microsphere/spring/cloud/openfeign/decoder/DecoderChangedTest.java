package io.microsphere.spring.cloud.openfeign.decoder;

import feign.codec.Decoder;
import io.microsphere.spring.cloud.openfeign.BaseTest;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
@SpringBootTest(classes = DecoderChangedTest.class)
@EnableAutoConfiguration
class DecoderChangedTest extends BaseTest<Decoder> {

    @Override
    protected String afterTestComponentConfigKey() {
        return "spring.cloud.openfeign.client.config.my-client.decoder";
    }

    @Override
    protected Class<? extends Decoder> beforeTestComponentClass() {
        return ADecoder.class;
    }

    @Override
    protected Class<? extends Decoder> afterTestComponent() {
        return BDecoder.class;
    }

    @Override
    protected FeignComponentAssert<Decoder> loadFeignComponentAssert() {
        return new DecoderComponentAssert();
    }
}
