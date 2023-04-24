/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.microsphere.spring.cloud.fault.tolerance.sentinel.redis;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.ResourceTypeConstants;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.github.microsphere.spring.redis.interceptor.RedisConnectionInterceptor;
import io.github.microsphere.spring.redis.interceptor.RedisMethodContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisCommands;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.github.microsphere.spring.cloud.fault.tolerance.sentinel.util.SentinelUtils.buildResourceName;

/**
 * {@link RedisConnectionInterceptor} for Sentinel
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class SentinelRedisCommandInterceptor implements RedisConnectionInterceptor, InitializingBean, BeanClassLoaderAware {

    private static final Logger logger = LoggerFactory.getLogger(SentinelRedisCommandInterceptor.class);

    private static final String ENTRY_ATTRIBUTE_NAME = "sentinel.entry";

    private final Map<Method, String> methodResourceNamesCache = new HashMap<>(256, 1.0f);

    private ClassLoader classLoader;

    @Override
    public void beforeExecute(RedisMethodContext<RedisConnection> redisMethodContext) {

        Method method = redisMethodContext.getMethod();

        String redisTemplateBeanName = redisMethodContext.getSourceBeanName();

        String resourceName = getResourceName(method);

        if (resourceName == null) {
            logger.trace("The RedisConnection method ['{}'] in the RedisTemplate Bean[name: '{}'] requires no interception", redisTemplateBeanName, method);
            return;
        }

        RedisConnection redisConnection = redisMethodContext.getTarget();

        String origin = getOrigin(redisConnection);

        String contextName = getContextName(resourceName);

        ContextUtil.enter(contextName, origin);

        entranceEntry(redisMethodContext, resourceName);

    }

    @Override
    public void afterExecute(RedisMethodContext<RedisConnection> redisMethodContext, Object result, Throwable failure) throws Throwable {

        Entry entry = getEntry(redisMethodContext);

        if (entry == null) {
            logger.debug("The Sentinel entry can't be found in the current thread[redisTemplateBeanName: '{}', method: '{}']",
                    redisMethodContext.getSourceBeanName(), redisMethodContext.getMethod());
            return;
        }

        if (failure != null) {
            Tracer.traceEntry(failure, entry);
        }

        entry.exit();

        ContextUtil.exit();
    }

    private String getOrigin(RedisConnection redisConnection) {
        return "";
    }

    private String getResourceName(Method method) {
        return methodResourceNamesCache.get(method);
    }

    private String getContextName(String resourceName) {
        return "spring.redis.commands.context-" + resourceName;
    }

    private void entranceEntry(RedisMethodContext<RedisConnection> context, String resourceName) {
        try {
            Entry entry = SphU.entry(resourceName, ResourceTypeConstants.COMMON, EntryType.IN);
            setEntry(context, entry);
        } catch (BlockException e) {
            logger.info("Redis Sentinel Resource[bean: '{}', name: '{}'] is restricted", context.getSourceBeanName(), resourceName, e);
        }
    }

    private void setEntry(RedisMethodContext context, Entry entry) {
        context.setAttribute(ENTRY_ATTRIBUTE_NAME, entry);
    }

    private Entry getEntry(RedisMethodContext context) {
        return (Entry) context.getAttribute(ENTRY_ATTRIBUTE_NAME);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initMethodResourceNamesCache();
    }

    public Map<Method, String> getMethodResourceNamesCache() {
        return Collections.unmodifiableMap(methodResourceNamesCache);
    }

    private void initMethodResourceNamesCache() {
        Class[] allInterfaceClasses = ClassUtils.getAllInterfacesForClass(RedisCommands.class, classLoader);
        for (Class interfaceClass : allInterfaceClasses) {
            Method[] methods = interfaceClass.getMethods();
            for (Method method : methods) {
                String resourceName = buildResourceName(method);
                methodResourceNamesCache.put(method, resourceName);
            }
        }
    }
}
