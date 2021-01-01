/*
 * Copyright © 2020-2029 organization opcooc
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
package com.opcooc.storage.args;

import static com.opcooc.storage.toolkit.StorageChecker.validateNotNull;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.opcooc.storage.exception.StorageException;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * @author shenqicheng
 * @since 1.2.0
 */
@Getter
@SuperBuilder(toBuilder = true)
public class BucketArgs extends BaseArgs {

    private static final Integer MIN_LENGTH = 3;

    private static final Integer MAX_LENGTH = 63;

    private static final String POINTS = "..";

    /**
     * Check compliance with Amazon S3 standards
     */
    public static final Predicate<String> CHECK_BUCKET_NAME = name -> Pattern.matches("^[a-z0-9][a-z0-9\\.\\-]+[a-z0-9]$", name);

    /**
     * 存储空间(主目录)名称
     */
    private String bucketName;

    /**
     * 地区
     */
    private String region;

    @Override
    public void validate() {
        validateBucketName(bucketName);
    }

    private void validateBucketName(String name) {
        validateNotNull(name, "bucket name");

        if (name.length() < MIN_LENGTH || name.length() > MAX_LENGTH) {
            throw new StorageException(
                    "opcooc-storage - [%s] bucket name must be at least 3 and no more than 63 characters long", name);
        }
        if (name.contains(POINTS)) {
            throw new StorageException("opcooc-storage - [%s] bucket name cannot contain successive periods. For more information refer", name);
        }

        if (!CHECK_BUCKET_NAME.test(name)) {
            throw new StorageException(
                    "opcooc-storage - [%s] bucket name does not follow Amazon S3 standards. For more information refer", name);
        }
    }
}
