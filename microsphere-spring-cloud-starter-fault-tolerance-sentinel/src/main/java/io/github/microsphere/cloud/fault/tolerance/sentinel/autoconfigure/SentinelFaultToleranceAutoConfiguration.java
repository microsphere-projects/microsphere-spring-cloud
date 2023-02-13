package io.github.microsphere.cloud.fault.tolerance.sentinel.autoconfigure;

import com.alibaba.csp.sentinel.SphU;
import io.github.microsphere.cloud.fault.tolerance.constants.FaultTolerancePropertyConstants;
import io.github.microsphere.cloud.fault.tolerance.sentinel.mybatis.SentinelMyBatisInterceptor;
import io.github.microsphere.cloud.fault.tolerance.sentinel.redis.SentinelRedisCommandInterceptor;
import io.github.microsphere.spring.redis.interceptor.RedisMethodInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static io.github.microsphere.cloud.fault.tolerance.constants.FaultTolerancePropertyConstants.ENABLED_PROPERTY_NAME;
import static io.github.microsphere.cloud.fault.tolerance.sentinel.autoconfigure.SentinelFaultToleranceAutoConfiguration.PROPERTY_NAME_PREFIX;

/**
 * Sentinel 自动装配
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@ConditionalOnProperty(prefix = PROPERTY_NAME_PREFIX, name = ENABLED_PROPERTY_NAME, matchIfMissing = true)
@ConditionalOnClass({SphU.class})
@Import(value = {
        SentinelFaultToleranceAutoConfiguration.MyBatisConfiguration.class,
        SentinelFaultToleranceAutoConfiguration.RedisConfiguration.class
})
@AutoConfigureBefore(name = {
        "com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration"
})
@AutoConfigureAfter(value = {
        DataSourceAutoConfiguration.class
})
public class SentinelFaultToleranceAutoConfiguration {

    public static final String PROPERTY_NAME_PREFIX = FaultTolerancePropertyConstants.PROPERTY_NAME_PREFIX + "sentinel";

    @ConditionalOnClass(RedisMethodInterceptor.class)
    static class RedisConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public SentinelRedisCommandInterceptor sentinelRedisCommandInterceptor() {
            return new SentinelRedisCommandInterceptor();
        }
    }

    @ConditionalOnClass(name = "org.apache.ibatis.plugin.Interceptor")
    static class MyBatisConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean(SqlSessionFactory.class)
        public SentinelMyBatisInterceptor sentinelInterceptor(SqlSessionFactory sqlSessionFactory) {
            SentinelMyBatisInterceptor interceptor = new SentinelMyBatisInterceptor();
            sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
            return interceptor;
        }


    }
}
