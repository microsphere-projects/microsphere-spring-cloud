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
package io.microsphere.spring.cloud.gateway.event;

import io.microsphere.spring.context.event.ApplicationListenerInterceptor;
import io.microsphere.spring.context.event.ApplicationListenerInterceptorChain;
import io.microsphere.util.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.client.discovery.event.ParentHeartbeatEvent;
import org.springframework.cloud.gateway.route.RouteRefreshListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ObjectUtils;

/**
 * The {@link ApplicationListenerInterceptor} class to disable {@link RouteRefreshListener RouteRefreshListeners'}
 * {@link HeartbeatEvent}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ApplicationListenerInterceptor
 * @see RouteRefreshListener
 * @see HeartbeatEvent
 * @see ParentHeartbeatEvent
 * @since 1.0.0
 */
public class RouteRefreshListenerDisabledHeartbeatEventInterceptor implements ApplicationListenerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RouteRefreshListenerDisabledHeartbeatEventInterceptor.class);

    private static final Class<RouteRefreshListener> INTERCEPTED_CLASS = RouteRefreshListener.class;

    private static final Class<?>[] EVENT_CLASSES = ArrayUtils.of(HeartbeatEvent.class, ParentHeartbeatEvent.class);

    @Override
    public void intercept(ApplicationListener<?> applicationListener, ApplicationEvent event, ApplicationListenerInterceptorChain chain) {
        Class<?> listenerClass = applicationListener.getClass();
        Class<?> eventClass = event.getClass();

        if (INTERCEPTED_CLASS.equals(listenerClass) && matchesHeartbeatEvent(eventClass)) {
            logger.debug("The ApplicationListener[class : '{}'] with event[class: '{}'] is disabled",
                    listenerClass.getName(), eventClass.getName());
            return;
        }

        chain.intercept(applicationListener, event);
    }

    private boolean matchesHeartbeatEvent(Class<?> eventClass) {
        return ObjectUtils.containsElement(EVENT_CLASSES, eventClass);
    }
}
