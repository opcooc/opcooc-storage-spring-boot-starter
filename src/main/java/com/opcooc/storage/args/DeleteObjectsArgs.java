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
package com.opcooc.storage.args;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

import static com.opcooc.storage.toolkit.StorageChecker.validateNotNull;

/**
 * @author shenqicheng
 * @since 1.2.0
 */
@Slf4j
@Getter
@SuperBuilder(toBuilder = true)
public class DeleteObjectsArgs extends BucketArgs {

    @Singular
    private Collection<String> objects;

    @Override
    public void validate() {
        log.debug("opcooc-storage - DeleteObjectsArgs, objects: [{}]", this.objects);
        super.validate();
        validateNotNull(this.objects, "objects");
    }
}
