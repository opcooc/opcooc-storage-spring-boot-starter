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
package com.opcooc.storage.adapter;

import java.io.IOException;

import com.opcooc.storage.service.NFSService;
import com.opcooc.storage.exception.StorageException;
import com.opcooc.storage.service.impl.S3NFSService;
import com.opcooc.storage.spring.boot.autoconfigure.DriverProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * @author shenqicheng
 * @since 1.0.0
 */
@Slf4j
public class S3DriverAdapter implements DriverAdapter {

    private final DriverProperties configuration;
    private final NFSService connect;
    private final String driver;

    public S3DriverAdapter(String driver, DriverProperties properties) {
        // 校验配置合法性
        properties.preCheck();
        this.driver = driver;
        this.configuration = properties;
        this.connect = new S3NFSService(driver, properties);
    }

    @Override
    public String driver() {
        return driver;
    }

    @Override
    public NFSService connect() {
        return connect;
    }

    @Override
    public DriverProperties configuration() {
        return configuration;
    }

    @Override
    public void close() throws IOException {
        log.debug("opcooc-storage - shutdown [{}] client driver", driver);
        try {
            connect.close();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }
}
