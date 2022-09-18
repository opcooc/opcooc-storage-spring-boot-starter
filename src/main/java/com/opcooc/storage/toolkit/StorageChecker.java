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
package com.opcooc.storage.toolkit;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.opcooc.storage.exception.StorageException;

/**
 * 公共数据检查类
 *
 * @author shenqicheng
 * @since 1.0.0
 */
public class StorageChecker {

    private static final Integer MIN_LENGTH = 3;

    private static final Integer MAX_LENGTH = 63;

    private static final String POINTS = "..";
    //默认为 15 分钟
    public static final long DEFAULT_EXPIRY_TIME = 15;
    // 最多 7 天
    public static final long MAX_EXPIRY_TIME = 60 * 24 * 7L;
    public static final long MAX_OBJECT_SIZE = 5L * 1024 * 1024 * 1024 * 1024;
    private static final String SYMBOL = "/";
    /**
     * Check compliance with Amazon S3 standards
     */
    public static final Predicate<String> CHECK_BUCKET_NAME = name -> Pattern.matches("^[a-z0-9][a-z0-9\\.\\-]+[a-z0-9]$", name);

    public static void validateBucket(String bucketName, String argName) {
        validateNotNull(bucketName, argName);

        if (bucketName.length() < MIN_LENGTH || bucketName.length() > MAX_LENGTH) {
            throw new StorageException("[%s] %s must be at least 3 and no more than 63 characters long", bucketName, argName);
        }
        if (bucketName.contains(POINTS)) {
            throw new StorageException("[%s] %s cannot contain successive periods. For more information refer", bucketName, argName);
        }

        if (!CHECK_BUCKET_NAME.test(bucketName)) {
            throw new StorageException("[%s] %s does not follow Amazon S3 standards. For more information refer", bucketName, argName);
        }
    }

    public static void validateEmpty(Collection<String> list, String argName) {
        if (list == null) {
            throw new StorageException("[%s] must not be null.", argName);
        }
        if (list.isEmpty()) {
            throw new StorageException("[%s] must be a non-empty string.", argName);
        }
    }

    public static void validateNotNull(Object arg, String argName) {
        if (arg == null) {
            throw new StorageException("[%s] must not be null.", argName);
        }
    }

    public static void validateNotEmptyString(String arg, String argName) {
        validateNotNull(arg, argName);
        if (arg.isEmpty()) {
            throw new StorageException("[%s] must be a non-empty string.", argName);
        }
    }

    public static void validateFolderName(String folderName) {
        validateNotEmptyString(folderName, "folderName");
        if (!folderName.endsWith(SYMBOL)) {
            throw new StorageException("[%s] folderName must end with '/' ", folderName);
        }
    }

    public static void validateObjectSize(long objectSize) {
        if (objectSize > MAX_OBJECT_SIZE) {
            throw new StorageException("object size %s is not supported; maximum allowed 5TiB", objectSize);
        }
    }

    public static void validateFile(File file) {
        validateNotNull(file, "file");
        if (!file.exists()) {
            throw new StorageException("[%s] the file does not exist", file);
        }
    }

    public static void validateExpiry(long expiry) {
        if (expiry < 1 || expiry > MAX_EXPIRY_TIME) {
            throw new StorageException("expiry must be minimum 1 second to maximum %s days", TimeUnit.DAYS.toMinutes(MAX_EXPIRY_TIME));
        }
    }
}
