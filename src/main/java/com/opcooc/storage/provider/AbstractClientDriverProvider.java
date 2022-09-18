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

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import com.opcooc.storage.drivers.ClientDriver;
import com.opcooc.storage.holder.ClientDriverHolder;
import com.opcooc.storage.spring.boot.autoconfigure.ClientDriverProperty;

/**
 * 默认驱动实现类
 *
 * @author shenqicheng
 * @since 1.0.0
 */
public abstract class AbstractClientDriverProvider implements ClientDriverProvider {

    protected Map<String, ClientDriver> createClientDriverMap(Map<String, ClientDriverProperty> clientDriverMap) {

        Map<String, ClientDriver> map = new HashMap<>(clientDriverMap.size() * 2);
        for (Map.Entry<String, ClientDriverProperty> item : clientDriverMap.entrySet()) {
            ClientDriverProperty clientDriverProperty = item.getValue();
            String driverName = clientDriverProperty.getDriver();
            ClientDriver driver = ClientDriverHolder.getClientDriver(clientDriverProperty);
            if (ObjectUtils.isEmpty(driverName)) {
                driverName = item.getKey();
            }
            if (driver != null) {
                map.put(driverName, driver);
            }
        }

        return map;
    }

}
