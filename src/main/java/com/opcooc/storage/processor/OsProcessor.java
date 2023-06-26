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
package com.opcooc.storage.processor;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Copyright © 2018 organization baomidou
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
 * </pre>
 */
public abstract class OsProcessor {

    private OsProcessor nextProcessor;

    public void setNextProcessor(OsProcessor osProcessor) {
        this.nextProcessor = osProcessor;
    }

    /**
     * 抽象匹配条件 匹配才会走当前执行器否则走下一级执行器
     *
     * @param key OS注解里的内容
     * @return 是否匹配
     */
    public abstract boolean matches(String key);

    /**
     * 决定客户端
     * <pre>
     *     调用底层doDetermineDatasource，
     *     如果返回的是null则继续执行下一个，否则直接返回
     * </pre>
     *
     * @param invocation 方法执行信息
     * @param key        OS注解里的内容
     * @return 客户端名称
     */
    public String determineDriver(MethodInvocation invocation, String key) {
        if (matches(key)) {
            String datasource = doDetermineDriver(invocation, key);
            if (datasource == null && nextProcessor != null) {
                return nextProcessor.determineDriver(invocation, key);
            }
            return datasource;
        }
        if (nextProcessor != null) {
            return nextProcessor.determineDriver(invocation, key);
        }
        return null;
    }

    /**
     * 抽象最终决定客户端
     *
     * @param invocation 方法执行信息
     * @param key        OS注解里的内容
     * @return 客户端名称
     */
    public abstract String doDetermineDriver(MethodInvocation invocation, String key);
}
