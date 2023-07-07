package io.microsphere.spring.cloud.openfeign.encoder;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;

import java.lang.reflect.Type;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class AEncoder implements Encoder {

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        System.out.println("AEncoder is working...");
        template.body(object.toString());
    }
}
