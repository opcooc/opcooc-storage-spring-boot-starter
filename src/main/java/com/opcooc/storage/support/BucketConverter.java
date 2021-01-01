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
package com.opcooc.storage.support;

import com.opcooc.storage.args.BucketArgs;
import com.opcooc.storage.spring.boot.autoconfigure.ClientDriverProperty;

/**
 * bucket名称转换器
 * @author shenqicheng
 * @since 1.2.0
 */
@FunctionalInterface
public interface BucketConverter {

    /**
     * bucket名称转换
     * @param config 配置
     * @param bucket 传入的bucket名称
     * @return 名称
     */
    String convert(ClientDriverProperty config, BucketArgs bucket);

}
