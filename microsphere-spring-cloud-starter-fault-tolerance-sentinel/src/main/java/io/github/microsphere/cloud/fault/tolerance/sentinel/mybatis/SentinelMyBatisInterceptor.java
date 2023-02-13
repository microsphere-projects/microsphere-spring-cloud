package io.github.microsphere.cloud.fault.tolerance.sentinel.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Sentinel for Mybatis {@link Interceptor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class SentinelMyBatisInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(SentinelMyBatisInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        Class<?> targetClass = target.getClass();
        if (Executor.class.isAssignableFrom(targetClass)) {
            return new DelegatingSentinelMyBatisExecutor((Executor) target);
        }
        logger.debug("Non-executor [type: '{}'] instances simply return without any dynamic proxy interception", targetClass.getName());
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        // NO-OP
    }
}
