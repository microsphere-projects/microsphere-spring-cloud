package io.microsphere.spring.cloud.openfeign.components;

import feign.Contract;
import feign.MethodMetadata;
import org.springframework.cloud.openfeign.FeignClientFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;

import java.util.List;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class DecoratedContract extends DecoratedFeignComponent<Contract> implements Contract {

    public DecoratedContract(String contextId, FeignClientFactory feignClientFactory, FeignClientProperties clientProperties, Contract delegate) {
        super(contextId, feignClientFactory, clientProperties, delegate);
    }

    @Override
    protected Class<Contract> componentType() {
        Class<Contract> contractClass = null;
        if (getDefaultConfiguration() != null && getDefaultConfiguration().getContract() != null)
            contractClass = getDefaultConfiguration().getContract();

        if (getCurrentConfiguration() != null && getCurrentConfiguration().getContract() != null)
            contractClass = getCurrentConfiguration().getContract();

        if (contractClass != null)
            return contractClass;
        return Contract.class;
    }

    @Override
    public List<MethodMetadata> parseAndValidateMetadata(Class<?> targetType) {
        return delegate().parseAndValidateMetadata(targetType);
    }
}
