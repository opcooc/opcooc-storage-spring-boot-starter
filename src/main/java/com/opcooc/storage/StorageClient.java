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
package com.opcooc.storage;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ObjectUtils;

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
import com.opcooc.storage.client.Client;
import com.opcooc.storage.drivers.ClientDriver;
import com.opcooc.storage.exception.StorageException;
import com.opcooc.storage.model.FileBasicInfo;
import com.opcooc.storage.spring.boot.autoconfigure.ClientDriverProperty;
import com.opcooc.storage.support.BucketConverter;
import com.opcooc.storage.support.ObjectConverter;
import com.opcooc.storage.toolkit.ContentTypeUtils;
import com.opcooc.storage.toolkit.StorageChecker;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 存储客户端对外调用类
 * 1.clientDriver 当前driver
 * 2.bucketConverter bucketName 自定义转换器
 * 2.objectConverter objectName 自定义转换器
 *
 * @author shenqicheng
 * @since 1.0.0
 */
@Slf4j
public class StorageClient implements InitializingBean, Client {

    private final ClientDriver clientDriver;

    @Setter
    private BucketConverter bucketConverter = (config, bucket) ->
            ObjectUtils.isEmpty(bucket.getBucketName()) && config != null ? config.getDefaultBucket() : bucket.getBucketName();
    @Setter
    private ObjectConverter objectConverter = (config, object) -> object.getObjectName();

    public StorageClient(ClientDriver clientDriver) {
        this.clientDriver = clientDriver;
    }

    private Client getConnect() {
        return clientDriver.connect();
    }

    private ClientDriverProperty getConfiguration() {
        return clientDriver.getConfiguration();
    }

    /**
     * 确定 bucket
     *
     * @param args 参数
     * @return 参数
     */
    private String determineBucket(BucketArgs args) {
        log.debug("opcooc-storage - determine bucket name before [{}]", args.getBucketName());

        ClientDriverProperty config = getConfiguration();
        BucketConverter converter = args.getBucketConverter() == null ? bucketConverter : args.getBucketConverter();
        String bucketName = converter.convert(config, args);

        log.debug("opcooc-storage - determine bucket name after [{}]", bucketName);

        if (ObjectUtils.isEmpty(bucketName)) {
            throw new StorageException(" bucketName cannot be empty");
        }

        if (Boolean.FALSE.equals(args.getCheckExist())) {
            return bucketName;
        }

        boolean doesExist = doesBucketExist(BucketArgs.builder().bucketName(bucketName).build());

        if (Boolean.FALSE.equals(config.getAutoCreateBucket()) && !doesExist) {
            throw new StorageException("there is no bucket named [%s] ", bucketName);
        }

        if (Boolean.TRUE.equals(config.getAutoCreateBucket()) && !doesExist) {
            createBucket(BucketArgs.builder().bucketName(bucketName).build());
        }

        return bucketName;
    }

    /**
     * 确认 object
     *
     * @param args 参数
     * @return 参数
     */
    private String determineObject(ObjectArgs args) {
        log.debug("opcooc-storage - determine object name before [{}]", args.getObjectName());

        ObjectConverter converter = args.getObjectConverter() == null ? objectConverter : args.getObjectConverter();
        String objectName = converter.convert(getConfiguration(), args);

        log.debug("opcooc-storage - determine object name after [{}]", objectName);
        return objectName;
    }

    @SuppressWarnings("unchecked")
    private <T extends BucketArgs> T determineArgs(T args) {
        String bucketName = determineBucket(args);
        return (T) args.toBuilder().bucketName(bucketName).build();
    }

    @SuppressWarnings("unchecked")
    private <T extends ObjectArgs> T determineArgs(T args) {
        String bucketName = determineBucket(args);
        String objectName = determineObject(args);
        return (T) args.toBuilder().bucketName(bucketName).objectName(objectName).build();
    }

    private UploadArgs determineContentType(UploadArgs args) {
        if (args.getContentType() != null) {
            return args;
        }
        String contentType = ContentTypeUtils.getContentType(args.getObjectName());
        return args.toBuilder().contentType(contentType).build();
    }

    @Override
    public void createFolder(ObjectArgs args) {
        args = determineArgs(args);
        args.validate();
        StorageChecker.validateFolderName(args.getObjectName());
        getConnect().createFolder(args);
    }

    @Override
    public String getDefaultBucketName() {
        return getConfiguration().getDefaultBucket();
    }

    @Override
    public void setBucketAcl(BucketAclArgs args) {
        args = determineArgs(args);
        args.validate();
        getConnect().setBucketAcl(args);
    }

    @Override
    public AccessControlList getBucketAcl(BucketAclArgs args) {
        args = determineArgs(args);
        args.validate();
        return getConnect().getBucketAcl(args);
    }

    @Override
    public void setBucketPolicy(BucketPolicyArgs args) {
        args = determineArgs(args);
        args.validate();
        getConnect().setBucketPolicy(args);
    }

    @Override
    public BucketPolicy getBucketPolicy(BucketPolicyArgs args) {
        args = determineArgs(args);
        args.validate();
        return getConnect().getBucketPolicy(args);
    }

    @Override
    public void deleteBucketPolicy(BucketPolicyArgs args) {
        args = determineArgs(args);
        args.validate();
        getConnect().deleteBucketPolicy(args);
    }

    @Override
    public String createBucket(BucketArgs args) {
        args = determineArgs(args);
        args.validate();
        return getConnect().createBucket(args);
    }

    @Override
    public void deleteBucket(BucketArgs args) {
        args = determineArgs(args);
        args.validate();
        getConnect().deleteBucket(args);
    }

    @Override
    public List<String> listBuckets() {
        return getConnect().listBuckets();
    }

    @Override
    public boolean doesBucketExist(BucketArgs args) {
        args = determineArgs(args);
        args.validate();
        return getConnect().doesBucketExist(args);
    }

    @Override
    public void setObjectAcl(ObjectAclArgs args) {
        args = determineArgs(args);
        args.validate();
        getConnect().setObjectAcl(args);
    }

    @Override
    public AccessControlList getObjectAcl(ObjectAclArgs args) {
        args = determineArgs(args);
        args.validate();
        return getConnect().getObjectAcl(args);
    }

    @Override
    public FileBasicInfo uploadObject(UploadArgs args) {
        args = determineArgs(args);
        args = determineContentType(args);
        args.validate();
        return getConnect().uploadObject(args);
    }

    @Override
    public FileBasicInfo uploadFile(UploadArgs args) {
        args = determineArgs(args);
        args.validate();
        return getConnect().uploadFile(args);
    }

    @Override
    public void copyObject(CopyObjectArgs args) {
        args = determineArgs(args);
        args.validate();
        getConnect().copyObject(args);
    }

    @Override
    public List<FileBasicInfo> listObjects(ListObjectArgs args) {
        args = determineArgs(args);
        args.validate();
        return getConnect().listObjects(args);
    }

    @Override
    public FileBasicInfo getObjectMetadata(ObjectArgs args) {
        args = determineArgs(args);
        args.validate();
        return getConnect().getObjectMetadata(args);
    }

    @Override
    public boolean objectExist(ObjectArgs args) {
        args = determineArgs(args);
        args.validate();
        return getConnect().objectExist(args);
    }

    @Override
    public InputStream getObjectToStream(ObjectArgs args) {
        args = determineArgs(args);
        args.validate();
        return getConnect().getObjectToStream(args);
    }

    @Override
    public File geObjectToFile(ObjectToFileArgs args) {
        args = determineArgs(args);
        args.validate();
        return getConnect().geObjectToFile(args);
    }

    @Override
    public void deleteObject(DeleteObjectArgs args) {
        args = determineArgs(args);
        args.validate();
        getConnect().deleteObject(args);
    }

    @Override
    public void deleteObjects(DeleteObjectArgs args) {
        args = determineArgs(args);
        args.validate();
        getConnect().deleteObjects(args);
    }

    @Override
    public String generatePresignedUrl(PresignedUrlArgs args) {
        args = determineArgs(args);
        args.validate();
        return getConnect().generatePresignedUrl(args);
    }

    @Override
    public String getUrl(ObjectArgs args) {
        args = determineArgs(args);
        args.validate();
        return getConnect().getUrl(args);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if (clientDriver == null) {
            throw new StorageException("clientSource must not be null");
        }

        if (bucketConverter == null) {
            throw new StorageException("bucketConverter must not be null");
        }

        if (objectConverter == null) {
            throw new StorageException("objectConverter must not be null");
        }
    }

}
