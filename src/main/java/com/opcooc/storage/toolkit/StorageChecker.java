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
package com.opcooc.storage.toolkit;

import com.opcooc.storage.exception.StorageException;

/**
 * 公共数据检查类
 *
 * @author shenqicheng
 * @since 1.2.0
 */
public class StorageChecker {

    public static void validateNotNull(Object arg, String argName) {
        if (arg == null) {
            throw new StorageException("opcooc-storage - %s must not be null.", argName);
        }
    }

    public static void validateNotEmptyString(String arg, String argName) {
        validateNotNull(arg, argName);
        if (arg.isEmpty()) {
            throw new StorageException("opcooc-storage - %s must be a non-empty string.", argName);
        }
    }

    public static boolean equals(String str1, String str2) {
        if (null == str1) {
            // 只有两个都为null才判断相等
            return str2 == null;
        }
        if (null == str2) {
            // 字符串2空，字符串1非空，直接false
            return false;
        }

        return str1.equals(str2);
    }


}
