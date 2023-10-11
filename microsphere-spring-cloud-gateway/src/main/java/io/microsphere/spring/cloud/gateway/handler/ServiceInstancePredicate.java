package io.microsphere.spring.cloud.gateway.handler;


import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.server.ServerWebExchange;

/**
 * WebEndpoint Service Instance Choose Filter Handler
 *
 * @author <a href="mailto:835010418@qq.com">caiti</a>
 * @since 1.0.0
 */
public interface ServiceInstancePredicate {

    /**
     * Is the current service selectable
     *
     * @param exchange – the current server exchange
     * @param serviceInstance
     * @return <code>true</code> if selectable
     */
    boolean test(ServerWebExchange exchange, ServiceInstance serviceInstance);

}
