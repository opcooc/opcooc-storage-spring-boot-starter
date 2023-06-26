/*
 * Copyright © 2020-2030 organization opcooc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opcooc.storage.provider;

import com.opcooc.storage.spring.boot.autoconfigure.DriverProperties;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 获取yml s3 客户端驱动类
 *
 * @author shenqicheng
 * @since 1.0.0
 */
@AllArgsConstructor
public class YmlDriverPropertiesProvider implements DriverPropertiesProvider {

    /**
     * 加载存储配置
     */
    private final Map<String, DriverProperties> propertiesMap;

    @Override
    public Map<String, DriverProperties> loadProperties() {
        return propertiesMap;
    }
}
