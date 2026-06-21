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

package io.microsphere.spring.cloud.client.actuator;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.actuator.FeaturesEndpoint;

import java.util.List;
import java.util.Map;

import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.PROPERTY_NAME_PREFIX;

/**
 * The {@link ConfigurationProperties} for {@link FeaturesEndpoint}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConfigurationProperties
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = PROPERTY_NAME_PREFIX)
public class FeaturesProperties {

    private Map<String, List<String>> _abstract;

    private Map<String, Map<String, String>> named;

    public void setAbstract(Map<String, List<String>> _abstract) {
        this._abstract = _abstract;
    }

    public Map<String, List<String>> getAbstract() {
        return _abstract;
    }

    public void setNamed(Map<String, Map<String, String>> named) {
        this.named = named;
    }

    public Map<String, Map<String, String>> getNamed() {
        return named;
    }
}
