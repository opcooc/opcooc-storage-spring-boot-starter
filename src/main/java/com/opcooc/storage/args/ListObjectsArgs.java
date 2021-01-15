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

import static com.opcooc.storage.toolkit.StorageChecker.validateNotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * @author shenqicheng
 * @since 1.0.0
 */
@Getter
@SuperBuilder(toBuilder = true)
public class ListObjectsArgs extends BucketArgs {

    private String prefix;

    @Builder.Default
    private boolean recursive = true;

    @Builder.Default
    private int maxKeys = 1000;

    @Override
    public void validate() {
        super.validate();
        validateNotNull(prefix, "prefix");
    }
}
