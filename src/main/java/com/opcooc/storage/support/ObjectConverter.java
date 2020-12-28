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
package com.opcooc.storage.support;

import com.opcooc.storage.args.ObjectArgs;
import com.opcooc.storage.spring.boot.autoconfigure.ClientDriverProperty;

/**
 * object转换器
 *
 * @author shenqicheng
 * @since 1.2.0
 */
@FunctionalInterface
public interface ObjectConverter {

    /**
     * object名称转换
     * @param config 配置
     * @param object 传入的object名称
     * @return 转换后的名称
     */
    String convert(ClientDriverProperty config, ObjectArgs object);

}
