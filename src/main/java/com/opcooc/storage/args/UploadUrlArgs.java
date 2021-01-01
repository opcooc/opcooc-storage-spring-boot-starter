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

import static com.opcooc.storage.toolkit.StorageChecker.validateNotEmptyString;
import static com.opcooc.storage.toolkit.StorageChecker.validateNotNull;

import java.io.File;

import com.opcooc.storage.exception.StorageException;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shenqicheng
 * @since 1.2.0
 */
@Slf4j
@SuperBuilder(toBuilder = true)
public class UploadUrlArgs extends ObjectArgs {

    @Getter
    private String url;

    @Getter
    private File file;

    @Override
    public void validate() {
        super.validate();
        validateNotEmptyString(this.url, "url");
        validateNotNull(file, "file");
        if (!file.exists()) {
            throw new StorageException("opcooc-storage - [%s] the file does not exist", file);
        }
    }
}
