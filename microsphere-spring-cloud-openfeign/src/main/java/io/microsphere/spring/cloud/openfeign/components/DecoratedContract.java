package io.microsphere.spring.cloud.openfeign.components;

import feign.Contract;
import feign.MethodMetadata;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClientSpecification;

import java.util.List;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class DecoratedContract extends DecoratedFeignComponent<Contract> implements Contract {

    /**
     * Constructs a {@link DecoratedContract} wrapping the given {@link Contract} delegate.
     *
     * <p>Example Usage:
     * <pre>{@code
     * DecoratedContract contract = new DecoratedContract(
     *     "my-client", contextFactory, clientProperties, new Contract.Default());
     * }</pre>
     *
     * @param contextId        the Feign client context ID
     * @param contextFactory   the {@link NamedContextFactory} for resolving per-client contexts
     * @param clientProperties the {@link FeignClientProperties} for configuration lookup
     * @param delegate         the original {@link Contract} to delegate to
     */
    public DecoratedContract(String contextId, NamedContextFactory<FeignClientSpecification> contextFactory, FeignClientProperties clientProperties, Contract delegate) {
        super(contextId, contextFactory, clientProperties, delegate);
    }

    /**
     * Returns the configured {@link Contract} class from {@link FeignClientConfiguration},
     * falling back to {@link Contract} if not configured.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Class<? extends Contract> type = decoratedContract.componentType();
     * }</pre>
     *
     * @return the {@link Contract} component type class
     */
    @Override
    protected Class<? extends Contract> componentType() {
        Class<Contract> contractClass = get(FeignClientConfiguration::getContract);
        return contractClass == null ? Contract.class : contractClass;
    }

    /**
     * Parses and validates metadata for the given target type by delegating to the
     * underlying {@link Contract}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * List<MethodMetadata> metadata = decoratedContract.parseAndValidateMetadata(MyFeignClient.class);
     * }</pre>
     *
     * @param targetType the Feign client interface class to parse
     * @return the list of parsed {@link MethodMetadata}
     */
    @Override
    public List<MethodMetadata> parseAndValidateMetadata(Class<?> targetType) {
        return delegate().parseAndValidateMetadata(targetType);
    }
}