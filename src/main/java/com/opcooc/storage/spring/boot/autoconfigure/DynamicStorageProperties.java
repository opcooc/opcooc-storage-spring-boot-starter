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

package com.opcooc.storage.spring.boot.autoconfigure;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

import com.opcooc.storage.toolkit.CryptoUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 配置属性类
 *
 * @author shenqicheng
 * @since 1.2.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = DynamicStorageProperties.PREFIX)
public class DynamicStorageProperties {

    public static final String PREFIX = "spring.storage.dynamic";
    public static final String HEALTH = "health";
    public static final String ENABLED = "enabled";

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 默认的客户端类型
     */
    private String primary = "s3";

    /**
     * 是否启用严格模式,默认不启动. 严格模式下未匹配到客户端直接报错, 非严格模式下则使用默认客户端primary所设置的客户端
     */
    private Boolean strict = false;

    /**
     * 是否使用 spring actuator 监控检查，默认不检查
     */
    private boolean health = false;

    /**
     * 默认s3配置
     */
    private Map<String, ClientDriverProperty> driver = new HashMap<>();

    /**
     * aop切面顺序，默认优先级最高
     */
    private Integer order = Ordered.HIGHEST_PRECEDENCE;

    /**
     * aop 切面是否只允许切 public 方法
     */
    private boolean allowedPublicOnly = true;

    /**
     * 全局默认publicKey
     */
    private String publicKey = CryptoUtils.DEFAULT_PUBLIC_KEY_STRING;

}
