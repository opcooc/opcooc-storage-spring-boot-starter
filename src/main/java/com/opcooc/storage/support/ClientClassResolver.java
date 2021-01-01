/*
 * Copyright © 2020-2029 organization opcooc
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
 */

package com.opcooc.storage.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodClassKey;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ClassUtils;

import com.opcooc.storage.annotation.OS;

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
public class ClientClassResolver {

    /**
     * 缓存方法对应的client
     */
    private final Map<Object, String> cache = new ConcurrentHashMap<>();
    private final boolean allowedPublicOnly;

    /**
     * 加入扩展, 给外部一个修改aop条件的机会
     *
     * @param allowedPublicOnly 只允许公共的方法, 默认为true
     */
    public ClientClassResolver(boolean allowedPublicOnly) {
        this.allowedPublicOnly = allowedPublicOnly;
    }

    /**
     * 从缓存获取数据
     *
     * @param method       方法
     * @param targetObject 目标对象
     * @return storage
     */
    public String findOsKey(Method method, Object targetObject) {
        if (method.getDeclaringClass() == Object.class) {
            return "";
        }
        Object cacheKey = new MethodClassKey(method, targetObject.getClass());
        String storage = this.cache.get(cacheKey);
        if (storage == null) {
            storage = computeStorage(method, targetObject);
            if (storage == null) {
                storage = "";
            }
            this.cache.put(cacheKey, storage);
        }
        return storage;
    }

    /**
     * 查找注解的顺序
     * 1. 当前方法
     * 2. 桥接方法
     * 3. 当前类开始一直找到Object
     *
     * @param method       方法
     * @param targetObject 目标对象
     * @return storage
     */
    private String computeStorage(Method method, Object targetObject) {
        if (allowedPublicOnly && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        Class<?> targetClass = targetObject.getClass();
        Class<?> userClass = ClassUtils.getUserClass(targetClass);
        // JDK代理时,  获取实现类的方法声明.  method: 接口的方法, specificMethod: 实现类方法
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);

        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        // 从当前方法查找
        String attribute = findAttribute(specificMethod);
        if (attribute != null) {
            return attribute;
        }
        // 从当前方法声明的类查找
        attribute = findAttribute(specificMethod.getDeclaringClass());
        if (attribute != null && ClassUtils.isUserLevelMethod(method)) {
            return attribute;
        }
        // 如果存在桥接方法
        if (specificMethod != method) {
            // 从桥接方法查找
            attribute = findAttribute(method);
            if (attribute != null) {
                return attribute;
            }
            // 从桥接方法声明的类查找
            attribute = findAttribute(method.getDeclaringClass());
            if (attribute != null && ClassUtils.isUserLevelMethod(method)) {
                return attribute;
            }
        }
        return getDefaultStorageAttr(targetObject);
    }

    /**
     * 默认的获取client名称方式
     *
     * @param targetObject 目标对象
     * @return storage
     */
    private String getDefaultStorageAttr(Object targetObject) {
        Class<?> targetClass = targetObject.getClass();
        // 如果不是代理类, 从当前类开始, 不断的找父类的声明
        if (!Proxy.isProxyClass(targetClass)) {
            Class<?> currentClass = targetClass;
            while (currentClass != Object.class) {
                String attribute = findAttribute(currentClass);
                if (attribute != null) {
                    return attribute;
                }
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 通过 AnnotatedElement 查找标记的注解, 映射为  StorageHolder
     *
     * @param ae AnnotatedElement
     * @return client映射持有者
     */
    private String findAttribute(AnnotatedElement ae) {
        AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(ae, OS.class);
        if (attributes != null) {
            return attributes.getString("value");
        }
        return null;
    }
}
