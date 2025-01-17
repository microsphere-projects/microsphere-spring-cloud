package io.microsphere.spring.cloud.openfeign.decoder;

import feign.MethodHandlerConfiguration;
import feign.ResponseHandler;
import feign.codec.Decoder;
import io.microsphere.spring.cloud.openfeign.FeignComponentAssert;
import io.microsphere.spring.cloud.openfeign.components.DecoratedDecoder;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class DecoderComponentAssert extends FeignComponentAssert<Decoder> {

    @Override
    protected Decoder loadCurrentComponent(MethodHandlerConfiguration configuration, ResponseHandler responseHandler) throws Exception {
        Class<ResponseHandler> responseHandlerClass = ResponseHandler.class;
        Field decoderField = responseHandlerClass.getDeclaredField("decoder");
        decoderField.setAccessible(true);
        DecoratedDecoder decoder = (DecoratedDecoder)decoderField.get(responseHandler);
        return decoder.delegate();
    }
}
