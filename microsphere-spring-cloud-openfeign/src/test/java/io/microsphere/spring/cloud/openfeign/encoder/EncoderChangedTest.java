package io.microsphere.spring.cloud.openfeign.encoder;

import feign.codec.Encoder;
import io.microsphere.spring.cloud.openfeign.BaseTest;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
@SpringBootTest(classes = EncoderChangedTest.class)
@EnableAutoConfiguration
public class EncoderChangedTest extends BaseTest<Encoder> {

    @Override
    protected String afterTestComponentConfigKey() {
        return "feign.client.config.my-client.encoder";
    }

    @Override
    protected Class<? extends Encoder> afterTestComponent() {
        return BEncoder.class;
    }
}
