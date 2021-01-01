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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opcooc.storage.spring.boot.autoconfigure.ClientDriverProperty;
import com.opcooc.storage.toolkit.CryptoUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 默认 字符串解密 decrypt
 */
@Slf4j
public class DefaultDecryptCallback implements DecryptCallback {
    /**
     * 加密正则
     */
    private static final Pattern ENC_PATTERN = Pattern.compile("^ENC\\((.*)\\)$");

    @Override
    public String decrypt(ClientDriverProperty config, String cipherText) {
        Matcher matcher = ENC_PATTERN.matcher(cipherText);
        if (matcher.find()) {
            try {
                return CryptoUtils.decrypt(config.getPublicKey(), matcher.group(1));
            } catch (Exception e) {
                log.error("DefaultDecryptCallback.decrypt error，continue...... ", e);
            }
        }
        return cipherText;
    }
}
