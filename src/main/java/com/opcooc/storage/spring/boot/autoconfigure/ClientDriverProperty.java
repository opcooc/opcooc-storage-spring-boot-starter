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
package com.opcooc.storage.spring.boot.autoconfigure;

import com.opcooc.storage.drivers.ClientDriver;
import com.opcooc.storage.enums.DefaultDriverType;
import com.opcooc.storage.exception.StorageException;
import com.opcooc.storage.support.DecryptCallback;
import com.opcooc.storage.support.DefaultDecryptCallback;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

/**
 * @author shenqicheng
 * @since 1.2.0
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDriverProperty {

    /**
     * 客户端驱动名称唯一标识 (默认为配置文件key名称)
     */
    private String driverName;

    /**
     * 默认驱动类型
     */
    private DefaultDriverType type = DefaultDriverType.S3;

    /**
     * 默认主目录(需要保证唯一)
     */
    private String defaultBucket;

    /**
     * 访问域名
     */
    private String endPoint;

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 密钥
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
     * 第一目录层级(默认为空, 当存在时所有路径都以 [firstPath + objectName] 拼接 ** 需要自己实现ObjectConverter **)
     */
    private String firstPath;

    /**
     * 是否自动创建目标bucket
     */
    private Boolean autoCreateBucket = false;

    /**
     * 解密回调(默认为 DefaultDecryptCallback )
     * 可通过 customizeDecryptCallback 覆盖
     */
    private DecryptCallback decryptCallback = new DefaultDecryptCallback();

    /**
     * 自定义解密回调clazz
     */
    private Class<? extends DecryptCallback> customizeDecryptCallback;

    /**
     * 自定义客户端clazz
     */
    private Class<? extends ClientDriver> customizeClientDriver;

    /**
     * 解密公匙(如果未设置默认使用全局的)
     */
    private String publicKey;

    public String getEndPoint() {
        return decrypt(endPoint);
    }

    public String getAccessKey() {
        return decrypt(accessKey);
    }

    public String getSecretKey() {
        return decrypt(secretKey);
    }

    public void setCustomizeDecryptCallback(Class<? extends DecryptCallback> clazz) {
        if (clazz != null) {
            try {
                this.decryptCallback = clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                log.error("load decryptCallback error : " + clazz.getName() + ", " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * 字符串解密
     */
    private String decrypt(String cipherText) {
        if (StringUtils.hasText(cipherText)) {
            return decryptCallback.decrypt(this, cipherText);
        }
        return cipherText;
    }

    /**
     * 客户端驱动参数预处理
     */
    public boolean preCheck() {
        return StringUtils.hasText(accessKey) && StringUtils.hasText(secretKey) && StringUtils.hasText(endPoint);
    }

    /**
     * 客户端驱动参数预处理(抛出内置异常)
     */
    public void preCheckThrow() throws StorageException {
        if (!preCheck()) {
            throw new StorageException("opcooc-storage - property pre check error, params incomplete.");
        }
    }

    /**
     * 客户端驱动参数预处理(抛出自定义异常)
     */
    public <E extends Throwable> void preCheckThrow(Supplier<? extends E> exceptionSupplier) throws E {
        if (!preCheck()) {
            throw exceptionSupplier.get();
        }
    }
}
