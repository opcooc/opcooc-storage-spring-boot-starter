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
package com.opcooc.storage.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author shenqicheng
 * @since 1.0.0
 */
public class ClientDriverEvent extends ApplicationEvent {

    public static final String EVENT_ADD = "add";

    public static final String EVENT_DELETE = "delete";

    public String driver;

    public String type;

    public ClientDriverEvent(String driver, String type) {
        super(driver);
        this.driver = driver;
        this.type = type;
    }

    /**
     * 发生变更的驱动名称
     *
     * @return 驱动名称
     */
    public String getDriver() {
        return driver;
    }

    /**
     * 变更的类型(添加: add, 删除: delete)
     *
     * @return 变更的类型
     */
    public String getType() {
        return type;
    }
}
