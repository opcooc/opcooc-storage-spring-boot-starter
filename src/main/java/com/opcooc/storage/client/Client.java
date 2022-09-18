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
package com.opcooc.storage.client;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.opcooc.storage.args.BucketAclArgs;
import com.opcooc.storage.args.BucketArgs;
import com.opcooc.storage.args.BucketPolicyArgs;
import com.opcooc.storage.args.CopyObjectArgs;
import com.opcooc.storage.args.DeleteObjectArgs;
import com.opcooc.storage.args.ListObjectArgs;
import com.opcooc.storage.args.ObjectAclArgs;
import com.opcooc.storage.args.ObjectArgs;
import com.opcooc.storage.args.ObjectToFileArgs;
import com.opcooc.storage.args.PresignedUrlArgs;
import com.opcooc.storage.args.UploadArgs;
import com.opcooc.storage.model.FileBasicInfo;

/**
 * @author shenqicheng
 * @since 1.0.0
 */
public interface Client extends Closeable {

    /**
     * 设置存储空间(主目录)ACL模式(只支持兼容s3协议的服务商)
     *
     * @param args 参数
     */
    void setBucketAcl(BucketAclArgs args);

    /**
     * 读取存储空间(主目录)ACL模式(只支持兼容s3协议的服务商)
     *
     * @param args 参数
     * @return ACL 列表
     */
    AccessControlList getBucketAcl(BucketAclArgs args);

    /**
     * 设置文件ACL模式(只支持兼容s3协议的服务商)
     *
     * @param args 参数
     */
    void setObjectAcl(ObjectAclArgs args);

    /**
     * 读取文件ACL模式(只支持兼容s3协议的服务商)
     *
     * @param args 参数
     * @return ACL 列表
     */
    AccessControlList getObjectAcl(ObjectAclArgs args);

    /**
     * 设置 存储空间(主目录) 策略
     *
     * @param args 参数
     */
    void setBucketPolicy(BucketPolicyArgs args);

    /**
     * 读取 存储空间(主目录) 策略
     *
     * @param args 参数
     * @return ACL 列表
     */
    BucketPolicy getBucketPolicy(BucketPolicyArgs args);

    /**
     * 删除 存储空间(主目录) 策略
     * @param args 参数
     */
    void deleteBucketPolicy(BucketPolicyArgs args);

    /**
     * 创建文件夹
     *
     * @param args 参数
     */
    void createFolder(ObjectArgs args);

    /**
     * 创建存储空间(主目录)
     *
     * @param args 参数
     * @return 存储空间(主目录)名称
     */
    String createBucket(BucketArgs args);

    /**
     * 删除存储空间(主目录)
     *
     * @param args 参数
     */
    void deleteBucket(BucketArgs args);

    /**
     * 获取所有存储空间(主目录)名称
     *
     * @return 名称集合
     */
    List<String> listBuckets();

    /**
     * 判断桶是否存在
     *
     * @param args 参数
     * @return 是否存在bucket
     */
    boolean doesBucketExist(BucketArgs args);

    /**
     * 上传文件到服务器
     *
     * @param args 参数
     * @return 文件上传后的信息
     */
    FileBasicInfo uploadObject(UploadArgs args);

    /**
     * 上传文件到服务器
     *
     * @param args 参数
     * @return 文件上传后的信息
     */
    FileBasicInfo uploadFile(UploadArgs args);

    /**
     * 复制文件
     *
     * @param args 参数
     */
    void copyObject(CopyObjectArgs args);

    /**
     * 获取指定存储空间(主目录)名称 指定前缀 的下级所有文件
     *
     * @param args 参数
     * @return 文件信息集合
     */
    List<FileBasicInfo> listObjects(ListObjectArgs args);

    /**
     * 获取对象元数据
     *
     * @param args 参数
     * @return 文件信息
     */
    FileBasicInfo getObjectMetadata(ObjectArgs args);

    /**
     * 判断对象是否存在
     *
     * @param args 参数
     * @return 结果
     */
    boolean objectExist(ObjectArgs args);

    /**
     * 获得文件 InputStream
     *
     * @param args 参数
     * @return InputStream
     */
    InputStream getObjectToStream(ObjectArgs args);

    /**
     * 获得文件
     *
     * @param args 参数
     * @return 文件
     */
    File geObjectToFile(ObjectToFileArgs args);

    /**
     * 删除单个文件
     *
     * @param args 参数
     */
    void deleteObject(DeleteObjectArgs args);

    /**
     * 删除文件集合
     *
     * @param args 参数
     */
    void deleteObjects(DeleteObjectArgs args);

    /**
     * 生成签名的URL，以使用get的HTTP方法访问文件
     *
     * @param args 参数
     * @return 签名url
     */
    String generatePresignedUrl(PresignedUrlArgs args);

    /**
     * 返回存储在指定存储空间(主目录)中的对象的URL
     *
     * @param args 参数
     * @return url
     */
    String getUrl(ObjectArgs args);

    /**
     *
     * @throws IOException
     */
    @Override
    default void close() throws IOException {
    }
}
