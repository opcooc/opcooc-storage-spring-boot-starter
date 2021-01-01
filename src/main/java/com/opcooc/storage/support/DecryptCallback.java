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

package com.opcooc.storage.support;

import com.opcooc.storage.spring.boot.autoconfigure.ClientDriverProperty;

/**
 * 解密信息转换器
 *
 * @author shenqicheng
 * @since 1.2.3
 */
@FunctionalInterface
public interface DecryptCallback {

    /**
     * 解密信息转换器
     *
     * @param config     配置
     * @param cipherText 密文
     * @return 解密后信息
     */
    String decrypt(ClientDriverProperty config, String cipherText);
}
