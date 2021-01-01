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
package com.opcooc.storage.toolkit;

import java.lang.reflect.Constructor;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.opcooc.storage.args.BucketArgs;
import com.opcooc.storage.args.ObjectArgs;
import com.opcooc.storage.model.FileBasicInfo;

/**
 * 公共的工具类
 *
 * @author shenqicheng
 * @since 1.2.0
 */
public class StorageUtil {

    public static FileBasicInfo createFileBasicInfo(PutObjectResult result, ObjectArgs args, Long contentLength) {
        FileBasicInfo info = new FileBasicInfo();
        info.setETag(result.getETag());
        info.setBucketName(args.getBucketName());
        info.setKey(args.getObjectName());
        info.setContentLength(contentLength);
        info.setContentMd5(result.getContentMd5());
        info.setMetadata(result.getMetadata().getRawMetadata());
        info.setUserMetadata(result.getMetadata().getUserMetadata());
        return info;
    }

    public static FileBasicInfo createFileBasicInfo(S3ObjectSummary result, BucketArgs args) {
        FileBasicInfo info = new FileBasicInfo();
        info.setKey(result.getKey());
        info.setContentLength(result.getSize());
        info.setBucketName(args.getBucketName());
        info.setLastModified(result.getLastModified());
        return info;
    }

    public static FileBasicInfo createFileBasicInfo(ObjectMetadata metadata, ObjectArgs args) {
        FileBasicInfo info = new FileBasicInfo();
        info.setETag(metadata.getETag());
        info.setKey(args.getObjectName());
        info.setContentLength(args.getObjectSize());
        info.setBucketName(args.getBucketName());
        info.setContentMd5(metadata.getContentMD5());
        info.setMetadata(metadata.getRawMetadata());
        info.setUserMetadata(metadata.getUserMetadata());
        return info;
    }

    /**
     * 获得对象数组的类数组
     *
     * @param objects 对象数组，如果数组中存在{@code null}元素，则此元素被认为是Object类型
     * @return 类数组
     */
    public static Class<?>[] getObjectClass(Object... objects) {
        Class<?>[] classes = new Class<?>[objects.length];
        Object obj;
        for (int i = 0; i < objects.length; i++) {
            obj = objects[i];
            if (null == obj) {
                classes[i] = Object.class;
            } else {
                classes[i] = obj.getClass();
            }
        }
        return classes;
    }

    public static <T> T instantiateClass(Class<T> clazz, Object... params) {
        try {
            if (params == null || params.length == 0) {
                return clazz.getDeclaredConstructor().newInstance();
            }
            Class<?>[] objectClass = StorageUtil.getObjectClass(params);
            Constructor<T> constructor = ClassUtils.getConstructorIfAvailable(clazz, objectClass);
            if (constructor == null) {
                return clazz.getDeclaredConstructor().newInstance();
            }
            return BeanUtils.instantiateClass(constructor, params);
        } catch (Exception e) {
            // 无自定义匹配
            return null;
        }
    }

}
