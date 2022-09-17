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
package com.opcooc.storage.holder;

import com.opcooc.storage.client.Client;
import com.opcooc.storage.client.S3Client;
import com.opcooc.storage.drivers.ClientDriver;
import com.opcooc.storage.drivers.DefaultClientDriver;
import com.opcooc.storage.enums.DefaultDriverType;
import com.opcooc.storage.spring.boot.autoconfigure.ClientDriverProperty;
import com.opcooc.storage.toolkit.StorageUtil;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shenqicheng
 * @since 1.0.0
 */
@Slf4j
@Setter
public class ClientDriverHolder {

    public ClientDriver getClientDriver(ClientDriverProperty property) {

        if (property.getCustomClient() != null) {
            //实例化自定义 ClientDriver
            return getCustomizeClientDriver(property);
        }

        if (property.getType() == DefaultDriverType.S3) {
            //s3 ClientDriver
            return getDefaultS3ClientDriver(property);
        }

        if (property.getType() == DefaultDriverType.LOCAL) {
            // ... todo 待实现
        }

        //默认s3 (将更改为local)
        return getDefaultS3ClientDriver(property);
    }

    public ClientDriver getCustomizeClientDriver(ClientDriverProperty property) {
        // 校验配置合法性
        property.preCheck();
        Client client = StorageUtil.instantiateClass(property.getCustomClient(), property);
        ClientDriver driver = new DefaultClientDriver(property, client);
        log.info("opcooc-storage - default s3 client driver instantiate success.");
        return driver;
    }

    public ClientDriver getDefaultS3ClientDriver(ClientDriverProperty property) {
        // 校验配置合法性
        property.preCheck();
        S3Client client = new S3Client(property);
        ClientDriver driver = new DefaultClientDriver(property, client);
        log.info("opcooc-storage - default s3 client driver instantiate success.");
        return driver;
    }
}
