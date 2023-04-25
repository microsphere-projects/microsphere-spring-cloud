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
package io.github.microsphere.spring.cloud.netflix.eureka.client.autoconfigure;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.AbstractDiscoveryClientOptionalArgs;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import io.github.microsphere.spring.cloud.netflix.eureka.client.ConditionalOnEurekaClientEnabled;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.context.event.EventListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.microsphere.spring.cloud.netflix.eureka.client.constants.EurekaClientConstants.ENABLED_PROPERTY_NAME;
import static io.github.microsphere.spring.cloud.netflix.eureka.client.constants.EurekaClientConstants.EUREKA_CLIENT_PROPERTY_PREFIX;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;
import static org.springframework.cloud.netflix.eureka.EurekaClientConfigBean.DEFAULT_ZONE;

/**
 * Auto-Configuration Class to support multiple {@link EurekaClient}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
@ConditionalOnEurekaClientEnabled
@ConditionalOnProperty(prefix = EUREKA_CLIENT_PROPERTY_PREFIX, name = ENABLED_PROPERTY_NAME, matchIfMissing = true)
@AutoConfigureAfter(EurekaClientAutoConfiguration.class)
public class MultipleEurekaClientAutoConfiguration {

    private final ApplicationInfoManager applicationInfoManager;

    private final EurekaClientConfig eurekaClientConfig;

    private final AbstractDiscoveryClientOptionalArgs<?> optionalArgs;

    private final ObjectProvider<EurekaClient> eurekaClientObjectProvider;

    private List<EurekaClient> eurekaClients; // EurekaClient list as the local storage

    public MultipleEurekaClientAutoConfiguration(ApplicationInfoManager applicationInfoManager,
                                                 EurekaClientConfig eurekaClientConfig,
                                                 AbstractDiscoveryClientOptionalArgs<?> optionalArgs,
                                                 ObjectProvider<EurekaClient> eurekaClientObjectProvider) {
        this.applicationInfoManager = applicationInfoManager;
        this.eurekaClientConfig = eurekaClientConfig;
        this.optionalArgs = optionalArgs;
        this.eurekaClientObjectProvider = eurekaClientObjectProvider;
    }

    @Bean
    @Role(ROLE_INFRASTRUCTURE)
    public PointcutAdvisor eurekaClientAdvisor() {
        NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
        advisor.setClassFilter(clazz -> EurekaClient.class.isAssignableFrom(clazz));
        advisor.addMethodName("getApplications");
        advisor.addMethodName("getInstancesByVipAddress");
        advisor.setAdvice(new EurekaClientMethodInterceptor());
        return advisor;
    }

    class EurekaClientMethodInterceptor implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            initEurekaClients();
            Method method = invocation.getMethod();
            String methodName = method.getName();
            switch (methodName) {
                case "getApplications":
                    return doGetApplications(invocation, method);
                case "getInstancesByVipAddress":
                    return doGetInstancesByVipAddress(invocation, method);
                default:
                    return invocation.proceed();
            }
        }

        private Object doGetApplications(MethodInvocation invocation, Method method) throws Throwable {
            Applications combinedApplications = new Applications();
            for (EurekaClient eurekaClient : eurekaClients) {
                final Applications applications;
                if (AopUtils.isAopProxy(eurekaClient)) {
                    applications = (Applications) invocation.proceed();
                } else {
                    Object[] args = invocation.getArguments();
                    applications = (Applications) method.invoke(eurekaClient, args);
                }
                for (Application application : applications.getRegisteredApplications()) {
                    combinedApplications.addApplication(application);
                }
            }

            return combinedApplications;
        }

        private Object doGetInstancesByVipAddress(MethodInvocation invocation, Method method) throws Throwable {
            List<InstanceInfo> combinedInstances = new ArrayList<>();
            for (EurekaClient eurekaClient : eurekaClients) {
                final List<InstanceInfo> infos;
                if (AopUtils.isAopProxy(eurekaClient)) {
                    infos = (List<InstanceInfo>) invocation.proceed();
                } else {
                    Object[] args = invocation.getArguments();
                    infos = (List<InstanceInfo>) method.invoke(eurekaClient, args);
                }
                combinedInstances.addAll(infos);
            }
            return combinedInstances;
        }
    }

    @ConditionalOnClass(InstanceRegisteredEvent.class)
    class EventConfig {

        @EventListener(InstanceRegisteredEvent.class)
        public void onInstanceRegisteredEvent(InstanceRegisteredEvent event) {
            initEurekaClients();
        }
    }


    private void initEurekaClients() {
        if (eurekaClients == null) {
            EurekaClientConfig eurekaClientConfig = this.eurekaClientConfig;
            List<String> serviceUrls = eurekaClientConfig.getEurekaServerServiceUrls(DEFAULT_ZONE);
            eurekaClients = new ArrayList<>(serviceUrls.size());
            eurekaClients.add(eurekaClientObjectProvider.getIfAvailable());
            // Add the customized EurekaClients
            for (int i = 1; i < serviceUrls.size(); i++) {
                eurekaClients.add(createCustomizedEurekaClient(eurekaClientConfig, i));
            }
        }
    }

    private EurekaClient createCustomizedEurekaClient(EurekaClientConfig eurekaClientConfig, int index) {
        EurekaClientConfig customizedEurekaClientConfig = customizeEurekaClientConfig(eurekaClientConfig, index);
        EurekaClient eurekaClient = new DiscoveryClient(applicationInfoManager, customizedEurekaClientConfig, optionalArgs);
        return eurekaClient;
    }

    private EurekaClientConfig customizeEurekaClientConfig(EurekaClientConfig eurekaClientConfig, int index) {
        EurekaClientConfigBean copy = new EurekaClientConfigBean();
        // 复制 eurekaClientConfig Bean 属性到 copy 对象
        BeanUtils.copyProperties(eurekaClientConfig, copy);
        // 重置 service URL
        Map<String, String> serviceUrlsMap = copy.getServiceUrl();
        for (Map.Entry<String, String> entry : serviceUrlsMap.entrySet()) {
            String zone = entry.getKey();
            List<String> serviceUrls = eurekaClientConfig.getEurekaServerServiceUrls(zone);
            String serviceUrl = serviceUrls.get(index); // 目标 Eureka Server 服务地址
            entry.setValue(serviceUrl); // 修改 Entry 值
        }
        return copy;
    }

    public List<EurekaClient> getEurekaClients() {
        return eurekaClients;
    }
}
