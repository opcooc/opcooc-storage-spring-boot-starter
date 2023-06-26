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
package com.opcooc.storage.spring.boot.autoconfigure;

import org.springframework.util.StringUtils;

import com.opcooc.storage.constant.DriverType;
import com.opcooc.storage.exception.StorageException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shenqicheng
 * @since 1.0.0
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverProperties {

    /**
     * 类型
     */
    private String type;

    /**
     * 默认主目录(需要保证唯一)
     */
    private String defaultBucket;

    /**
     * 访问域名
     */
    private String endpoint;

    /**
     * 账号
     */
    private String accessKey;

    /**
     * 密码
     */
    private String secretKey;

    /**
     * 路径样式(默认为true)
     */
    private Boolean pathStyle = true;
    /**
     * 区域
     */
    private String region;

    /**
     * 是否自动创建目标bucket
     */
    private Boolean autoCreateBucket = false;

    /**
     * 客户端驱动参数预处理(抛出内置异常)
     */
    public void preCheck() throws StorageException {
        if (!StringUtils.hasText(accessKey) || !StringUtils.hasText(secretKey) || !StringUtils.hasText(endpoint)) {
            throw new StorageException("property pre check error, params incomplete.");
        }
    }
}
