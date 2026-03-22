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

    /**
     * Returns the {@link Contract} implementation class to use when reloading
     * the delegate after a refresh, as configured in {@link FeignClientConfiguration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<? extends Contract> type = decoratedContract.componentType();
     * }</pre>
     *
     * @return the configured {@link Contract} class, or {@link Contract} if not configured
     */
    @Override
    protected Class<? extends Contract> componentType() {
        Class<Contract> contractClass = get(FeignClientConfiguration::getContract);
        return contractClass == null ? Contract.class : contractClass;
    }

    /**
     * Parses and validates the metadata for the given target type by delegating
     * to the underlying {@link Contract} implementation.
     *
     * <p>Example Usage:
     * <pre>{@code
     * List<MethodMetadata> metadata = decoratedContract.parseAndValidateMetadata(MyFeignClient.class);
     * }</pre>
     *
     * @param targetType the Feign client interface class to parse
     * @return the list of {@link MethodMetadata} for the target type
     */
    @Override
    public List<MethodMetadata> parseAndValidateMetadata(Class<?> targetType) {
        return delegate().parseAndValidateMetadata(targetType);
    }
}