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

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.opcooc.storage.drivers.ClientDriver;
import com.opcooc.storage.drivers.DefaultS3ClientDriver;
import com.opcooc.storage.enums.DefaultDriverType;
import com.opcooc.storage.spring.boot.autoconfigure.ClientDriverProperty;
import com.opcooc.storage.spring.boot.autoconfigure.DynamicStorageProperties;
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

    private DynamicStorageProperties properties;

    public ClientDriver getClientDriver(ClientDriverProperty property) {
        //当不存在PublicKey时添加默认PublicKey
        if (ObjectUtils.isEmpty(property.getPublicKey())) {
            property.setPublicKey(properties.getPublicKey());
        }

        if (property.getCustomizeClientDriver() != null) {
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
        ClientDriver driver = StorageUtil.instantiateClass(property.getCustomizeClientDriver(), property);
        log.info("opcooc-storage - extend customize client driver instantiate success.");
        return driver;
    }

    public ClientDriver getDefaultS3ClientDriver(ClientDriverProperty property) {
        //客户端驱动参数预处理
        if (!property.preCheck()) {
            log.error("opcooc-storage - default s3 client driver property pre check error, params incomplete.");
            return null;
        }
        ClientDriver driver = new DefaultS3ClientDriver(property);
        log.info("opcooc-storage - default s3 client driver instantiate success.");
        return driver;
    }
}
