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
package com.opcooc.storage.adapter;

import com.opcooc.storage.exception.StorageException;
import com.opcooc.storage.service.NFSService;
import com.opcooc.storage.spring.boot.autoconfigure.DriverProperties;

import java.io.Closeable;

/**
 * @author shenqicheng
 * @since 1.0.0
 */
public interface DriverAdapter extends Closeable {

    /**
     * 客户端驱动名称唯一标识 (默认为配置文件key名称)
     *
     * @return 标识
     */
    default String driver() {
        throw new StorageException("请实现driver方法");
    }

    /**
     * 得到client操作类
     *
     * @return 操作类
     */
    NFSService connect();

    /**
     * 得到ClientDriver配置信息
     *
     * @return 配置信息
     */
    DriverProperties configuration();
}
