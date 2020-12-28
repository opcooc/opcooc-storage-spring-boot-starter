/*
 * Copyright © 2020-2025 organization opcooc
 * <pre>
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
 * <pre/>
 */
package com.opcooc.storage.support;

import com.opcooc.storage.drivers.ClientDriver;
import com.opcooc.storage.drivers.DynamicRoutingClientDriver;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * ClientDriver 健康检查实现
 * @author shenqicheng
 * @since 1.2.3
 */
public class ClientDriverHealthIndicator extends AbstractHealthIndicator {

    /**
     * 当前执行ClientDriver
     */
    private final ClientDriver clientDriver;

    public ClientDriverHealthIndicator(ClientDriver clientDriver) {
        Assert.notNull(clientDriver, "ClientDriver must not be null");
        this.clientDriver = clientDriver;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        if (this.clientDriver == null) {
            builder.up().withDetail("clientDriver", "unknown");
            return;
        }
        if (clientDriver instanceof DynamicRoutingClientDriver) {
            Map<String, ClientDriver> clientDriverMap = ((DynamicRoutingClientDriver) clientDriver).getCurrentClientDrivers();
            builder.up().withDetail("clientDriver", "DynamicRoutingClientDriver");
            // 循环检查当前客户端驱动是否可用
            for (Map.Entry<String, ClientDriver> clientDriver : clientDriverMap.entrySet()) {
                Integer result = 0;
                try {
                    result = doClientDriverCheck(clientDriver.getValue());
                } finally {
                    builder.withDetail(clientDriver.getKey(), result);
                }
            }
        }
    }

    protected Integer doClientDriverCheck(ClientDriver clientDriver) throws Exception {
        return clientDriver.connect().listBuckets() == null ? 0 : 1;
    }

}
