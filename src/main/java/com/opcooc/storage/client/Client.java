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

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.opcooc.storage.args.CopyObjectArgs;
import com.opcooc.storage.args.CreateBucketArgs;
import com.opcooc.storage.args.DeleteBucketArgs;
import com.opcooc.storage.args.DeleteBucketPolicyArgs;
import com.opcooc.storage.args.DeleteObjectArgs;
import com.opcooc.storage.args.DeleteObjectsArgs;
import com.opcooc.storage.args.DoesBucketExistArgs;
import com.opcooc.storage.args.DoesObjectExistArgs;
import com.opcooc.storage.args.GetBucketAclArgs;
import com.opcooc.storage.args.GetBucketPolicyArgs;
import com.opcooc.storage.args.GetObjectAclArgs;
import com.opcooc.storage.args.GetObjectToFileArgs;
import com.opcooc.storage.args.GetObjectToStreamArgs;
import com.opcooc.storage.args.GetPresignedObjectUrlArgs;
import com.opcooc.storage.args.GetUrlArgs;
import com.opcooc.storage.args.ListObjectsArgs;
import com.opcooc.storage.args.ObjectMetadataArgs;
import com.opcooc.storage.args.SetBucketAclArgs;
import com.opcooc.storage.args.SetBucketPolicyArgs;
import com.opcooc.storage.args.SetFolderArgs;
import com.opcooc.storage.args.SetObjectAclArgs;
import com.opcooc.storage.args.UploadFileArgs;
import com.opcooc.storage.args.UploadObjectArgs;
import com.opcooc.storage.args.UploadUrlArgs;
import com.opcooc.storage.model.FileBasicInfo;
import com.opcooc.storage.toolkit.HttpUtils;

/**
 * @author shenqicheng
 * @since 1.0.0
 */
public interface Client {

    /**
     * 创建文件夹
     *
     * @param args 参数
     */
    void createFolder(SetFolderArgs args);

    /**
     * 设置存储空间(主目录)ACL模式(只支持兼容s3协议的服务商)
     *
     * @param args 参数
     */
    void setBucketAcl(SetBucketAclArgs args);

    /**
     * 读取存储空间(主目录)ACL模式(只支持兼容s3协议的服务商)
     *
     * @param args 参数
     * @return ACL 列表
     */
    AccessControlList getBucketAcl(GetBucketAclArgs args);

    /**
     * 设置 存储空间(主目录) 策略
     *
     * @param args 参数
     */
    void setBucketPolicy(SetBucketPolicyArgs args);

    /**
     * 读取 存储空间(主目录) 策略
     *
     * @param args 参数
     * @return ACL 列表
     */
    BucketPolicy getBucketPolicy(GetBucketPolicyArgs args);

    /**
     * 删除 存储空间(主目录) 策略
     * @param args 参数
     */
    void deleteBucketPolicy(DeleteBucketPolicyArgs args);

    /**
     * 创建存储空间(主目录)
     *
     * @param args 参数
     * @return 存储空间(主目录)名称
     */
    String createBucket(CreateBucketArgs args);

    /**
     * 删除存储空间(主目录)
     *
     * @param args 参数
     */
    void deleteBucket(DeleteBucketArgs args);

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
    boolean doesBucketExist(DoesBucketExistArgs args);

    /**
     * 设置文件ACL模式(只支持兼容s3协议的服务商)
     *
     * @param args 参数
     */
    void setObjectAcl(SetObjectAclArgs args);

    /**
     * 读取文件ACL模式(只支持兼容s3协议的服务商)
     *
     * @param args 参数
     * @return ACL 列表
     */
    AccessControlList getObjectAcl(GetObjectAclArgs args);

    /**
     * 上传文件到服务器
     *
     * @param args 参数
     * @return 文件上传后的信息
     */
    FileBasicInfo uploadObject(UploadObjectArgs args);

    /**
     * 上传文件到服务器
     *
     * @param args 参数
     * @return 文件上传后的信息
     */
    FileBasicInfo uploadFile(UploadFileArgs args);

    /**
     * 上传文件到服务器
     *
     * @param args 参数
     * @return 文件上传后的信息
     */
    FileBasicInfo uploadUrl(UploadUrlArgs args);

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
    List<FileBasicInfo> listObjects(ListObjectsArgs args);

    /**
     * 获取对象元数据
     *
     * @param args 参数
     * @return 文件信息
     */
    FileBasicInfo getObjectMetadata(ObjectMetadataArgs args);

    /**
     * 判断对象是否存在
     *
     * @param args 参数
     * @return 结果
     */
    boolean objectExist(DoesObjectExistArgs args);

    /**
     * 获得文件 InputStream
     *
     * @param args 参数
     * @return InputStream
     */
    InputStream getObjectToStream(GetObjectToStreamArgs args);

    /**
     * 获得文件
     *
     * @param args 参数
     * @return 文件
     */
    File geObjectToFile(GetObjectToFileArgs args);

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
    void deleteObjects(DeleteObjectsArgs args);

    /**
     * 生成签名的URL，以使用get的HTTP方法访问文件
     *
     * @param args 参数
     * @return 签名url
     */
    String generatePresignedUrl(GetPresignedObjectUrlArgs args);

    /**
     * 返回存储在指定存储空间(主目录)中的对象的URL
     *
     * @param args 参数
     * @return url
     */
    String getUrl(GetUrlArgs args);

    /**
     * 使用PresignedUrl上传文件
     *
     * @param url  上传url
     * @param file 需要上传的文件
     * @return 是否上传成功
     */
    default Boolean httpUploadFile(String url, File file) {
        return HttpUtils.httpUploadFile(url, file);
    }

}
