package io.microsphere.spring.cloud.openfeign.components;

import feign.Contract;
import feign.MethodMetadata;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignContext;

import java.util.List;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class DecoratedContract extends DecoratedFeignComponent<Contract> implements Contract {

    public DecoratedContract(String contextId, FeignContext feignContext, FeignClientProperties clientProperties, Contract delegate) {
        super(contextId, feignContext, clientProperties, delegate);
    }

    @Override
    protected Class<? extends Contract> componentType() {
        Class<Contract> contractClass = get(FeignClientConfiguration::getContract);
        return contractClass == null ? Contract.class : contractClass;
    }

    @Override
    public List<MethodMetadata> parseAndValidateMetadata(Class<?> targetType) {
        return delegate().parseAndValidateMetadata(targetType);
    }
}