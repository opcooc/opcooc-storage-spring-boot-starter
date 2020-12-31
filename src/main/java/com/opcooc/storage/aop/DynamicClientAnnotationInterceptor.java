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
package com.opcooc.storage.aop;

import com.opcooc.storage.holder.DynamicClientContextHolder;
import com.opcooc.storage.processor.OsProcessor;
import com.opcooc.storage.support.ClientClassResolver;
import org.aopalliance.intercept.MethodInterceptor;
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
 * <pre/>
 */
public class DynamicClientAnnotationInterceptor implements MethodInterceptor {

    /**
     * The identification of SPEL.
     */
    private static final String DYNAMIC_PREFIX = "#";

    private final ClientClassResolver clientClassResolver;
    private final OsProcessor osProcessor;

    public DynamicClientAnnotationInterceptor(Boolean allowedPublicOnly, OsProcessor osProcessor) {
        clientClassResolver = new ClientClassResolver(allowedPublicOnly);
        this.osProcessor = osProcessor;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            String scKey = determineClientKey(invocation);
            DynamicClientContextHolder.push(scKey);
            return invocation.proceed();
        } finally {
            DynamicClientContextHolder.poll();
        }
    }

    private String determineClientKey(MethodInvocation invocation) {
        String key = clientClassResolver.findOsKey(invocation.getMethod(), invocation.getThis());
        return (!key.isEmpty() && key.startsWith(DYNAMIC_PREFIX)) ? osProcessor.determineClient(invocation, key) : key;
    }

}
