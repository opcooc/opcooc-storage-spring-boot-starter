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
package com.opcooc.storage.args;

import java.io.File;
import java.io.InputStream;

import com.opcooc.storage.exception.StorageException;
import com.opcooc.storage.toolkit.StorageChecker;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * @author shenqicheng
 * @since 1.0.0
 */
@Getter
@SuperBuilder(toBuilder = true)
public class UploadArgs extends ObjectArgs {

    private File file;

    private InputStream stream;

    private long objectSize;

    private String contentType;

    @Override
    public void validate() {
        super.validate();
        StorageChecker.validateObjectSize(objectSize);
        if (file == null && stream == null) {
            throw new StorageException("file and stream cannot both be empty.");
        }
        if (file == null) {
            StorageChecker.validateNotNull(stream, "UploadArgs stream");
        } else {
            StorageChecker.validateFile(file);
        }
    }
}
