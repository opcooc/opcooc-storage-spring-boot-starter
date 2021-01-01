/*
 * Copyright © 2020-2029 organization opcooc
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
 */

package com.opcooc.storage.drivers;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import com.opcooc.storage.event.ClientDriverEvent;
import com.opcooc.storage.holder.DynamicClientContextHolder;
import com.opcooc.storage.provider.ClientDriverProvider;
import com.opcooc.storage.toolkit.StorageConstant;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shenqicheng
 * @since 1.2.0
 */
@Slf4j
public class DynamicRoutingClientDriver extends AbstractRoutingClientDriver implements InitializingBean, DisposableBean, ApplicationContextAware {

    /**
     * 所有客户端
     */
    private final Map<String, ClientDriver> clientDriverMap = new LinkedHashMap<>();

    @Setter
    private String primary = "s3";
    @Setter
    private Boolean strict = false;

    private ApplicationContext applicationContext;

    @Override
    public ClientDriver determineClientDriver() {
        return getClientDriver(DynamicClientContextHolder.peek());
    }

    private ClientDriver determinePrimaryClientDriver() {
        log.debug("opcooc-storage - switch to the primary client driver");
        return clientDriverMap.get(primary);
    }

    /**
     * 获取当前所有的驱动
     *
     * @return 当前所有驱动
     */
    public Map<String, ClientDriver> getCurrentClientDrivers() {
        return clientDriverMap;
    }

    /**
     * 获取驱动
     *
     * @param driverName 驱动名称
     * @return 驱动
     */
    public ClientDriver getClientDriver(String driverName) {
        if (StringUtils.isEmpty(driverName)) {
            return determinePrimaryClientDriver();
        } else if (clientDriverMap.containsKey(driverName)) {
            log.debug("opcooc-storage - switch to the client driver named [{}]", driverName);
            return clientDriverMap.get(driverName);
        }
        if (strict) {
            throw new RuntimeException("opcooc-storage - could not find a client driver named" + driverName);
        }
        return determinePrimaryClientDriver();
    }

    /**
     * 添加驱动
     *
     * @param driverName           驱动名称
     * @param clientDriver 驱动
     */
    public synchronized void addClientDriver(String driverName, ClientDriver clientDriver) {
        if (!clientDriverMap.containsKey(driverName)) {
            clientDriverMap.put(driverName, clientDriver);
            publishEvent(driverName, StorageConstant.EVENT_ADD);
            log.info("opcooc-storage - load a client driver named [{}] success", driverName);
        } else {
            log.warn("opcooc-storage - load a client driver named [{}] failed, because it already exist", driverName);
        }
    }

    /**
     * 删除驱动
     *
     * @param driverName 驱动名称
     */
    public synchronized void removeClientDriver(String driverName) {
        if (!StringUtils.hasText(driverName)) {
            throw new RuntimeException("opcooc-storage - remove parameter could not be empty");
        }
        if (primary.equals(driverName)) {
            throw new RuntimeException("opcooc-storage - could not remove primary client driver");
        }
        if (clientDriverMap.containsKey(driverName)) {
            ClientDriver clientDriver = clientDriverMap.get(driverName);
            try {
                clientDriver.close();
                publishEvent(driverName, StorageConstant.EVENT_DELETE);
            } catch (Exception e) {
                log.error("opcooc-storage - remove the client driver named [{}]  failed", driverName, e);
            }
            clientDriverMap.remove(driverName);
            log.info("opcooc-storage - remove the client driver named [{}] success", driverName);
        } else {
            log.warn("opcooc-storage - could not find a client driver named [{}]", driverName);
        }
    }

    @Override
    public void destroy() throws Exception {
        this.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, ClientDriverProvider> validateCodeGeneratorMap = applicationContext.getBeansOfType(ClientDriverProvider.class);
        Collection<ClientDriverProvider> values = validateCodeGeneratorMap.values();
        for (ClientDriverProvider provider : values) {
            Map<String, ClientDriver> clientDrivers = provider.loadClientDrivers();
            for (Map.Entry<String, ClientDriver> item : clientDrivers.entrySet()) {
                addClientDriver(item.getKey(), item.getValue());
            }
        }
        // 检测默认驱动是否设置
        if (clientDriverMap.containsKey(primary)) {
            log.info("opcooc-storage - initial loaded [{}] client driver,primary client driver named [{}]", clientDriverMap.size(), primary);
        } else {
            throw new RuntimeException("opcooc-storage - please check the setting of primary");
        }
    }

    @Override
    public void close() throws IOException {
        log.info("opcooc-storage - start closing ....");
        for (Map.Entry<String, ClientDriver> item : clientDriverMap.entrySet()) {
            item.getValue().close();
        }
        log.info("opcooc-storage - all closed success,bye");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void publishEvent(String driverName, String type) {
        applicationContext.publishEvent(new ClientDriverEvent(driverName, type));
    }
}
