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
package com.opcooc.storage;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.opcooc.storage.args.BaseArgs;
import com.opcooc.storage.args.BucketArgs;
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
import com.opcooc.storage.args.ObjectArgs;
import com.opcooc.storage.args.ObjectMetadataArgs;
import com.opcooc.storage.args.SetBucketAclArgs;
import com.opcooc.storage.args.SetBucketPolicyArgs;
import com.opcooc.storage.args.SetFolderArgs;
import com.opcooc.storage.args.SetObjectAclArgs;
import com.opcooc.storage.args.UploadFileArgs;
import com.opcooc.storage.args.UploadObjectArgs;
import com.opcooc.storage.args.UploadUrlArgs;
import com.opcooc.storage.client.Client;
import com.opcooc.storage.drivers.ClientDriver;
import com.opcooc.storage.exception.StorageException;
import com.opcooc.storage.model.FileBasicInfo;
import com.opcooc.storage.spring.boot.autoconfigure.ClientDriverProperty;
import com.opcooc.storage.support.BucketConverter;
import com.opcooc.storage.support.ObjectConverter;
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
 * @since 1.2.0
 */
@Slf4j
public class StorageClient implements InitializingBean, Client {

    private final ClientDriver clientDriver;

    @Setter
    private BucketConverter bucketConverter = (config, bucket) -> StringUtils.isEmpty(bucket.getBucketName()) && config != null
            ? config.getDefaultBucket() : bucket.getBucketName();
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
    private BucketArgs determineBucket(BucketArgs args) {
        log.debug("opcooc-storage - determine bucket name before [{}]", args.getBucketName());

        ClientDriverProperty config = getConfiguration();
        String bucketName = bucketConverter.convert(config, args);

        log.debug("opcooc-storage - determine bucket name after [{}]", bucketName);

        if (StringUtils.isEmpty(bucketName)) {
            throw new StorageException("opcooc-storage - bucketName cannot be empty");
        }

        boolean doesExist = doesBucketExist(DoesBucketExistArgs.builder().bucketName(bucketName).build());

        if (!config.getAutoCreateBucket() && !doesExist) {
            throw new StorageException("opcooc-storage - there is no bucket named [%s] ", bucketName);
        }
        //create bucket
        if (config.getAutoCreateBucket() && !doesExist) {
            createBucket(CreateBucketArgs.builder().bucketName(bucketName).build());
        }

        return StorageChecker.equals(args.getBucketName(), bucketName) ? args : args.toBuilder().bucketName(bucketName).build();
    }

    /**
     * 确认 object
     *
     * @param args 参数
     * @return 参数
     */
    private ObjectArgs determineObject(ObjectArgs args) {
        log.debug("opcooc-storage - determine object name before [{}]", args.getObjectName());

        String objectName = objectConverter.convert(getConfiguration(), args);

        log.debug("opcooc-storage - determine object name after [{}]", objectName);
        return StorageChecker.equals(args.getObjectName(), objectName) ? args : args.toBuilder().objectName(objectName).build();
    }

    /**
     * 确定参数并检验参数是否合法
     *
     * @param args 参数
     * @param <T>  类型
     * @return 参数
     */
    @SuppressWarnings("unchecked")
    private <T extends BaseArgs> T determineInfoAndValidate(T args) {
        if (args instanceof BucketArgs) {
            args = (T) determineBucket((BucketArgs) args);
        }
        if (args instanceof ObjectArgs) {
            args = (T) determineObject((ObjectArgs) args);
        }
        args.validate();
        return args;
    }

    @Override
    public void createFolder(SetFolderArgs args) {
        getConnect().createFolder(determineInfoAndValidate(args));
    }

    @Override
    public void setBucketAcl(SetBucketAclArgs args) {
        args.validate();
        getConnect().setBucketAcl(args);
    }

    @Override
    public AccessControlList getBucketAcl(GetBucketAclArgs args) {
        args.validate();
        return getConnect().getBucketAcl(args);
    }

    @Override
    public void setBucketPolicy(SetBucketPolicyArgs args) {
        args.validate();
        getConnect().setBucketPolicy(args);
    }

    @Override
    public BucketPolicy getBucketPolicy(GetBucketPolicyArgs args) {
        args.validate();
        return getConnect().getBucketPolicy(args);
    }

    @Override
    public void deleteBucketPolicy(DeleteBucketPolicyArgs args) {
        args.validate();
        getConnect().deleteBucketPolicy(args);
    }

    @Override
    public String createBucket(CreateBucketArgs args) {
        args.validate();
        return getConnect().createBucket(args);
    }

    @Override
    public void deleteBucket(DeleteBucketArgs args) {
        args.validate();
        getConnect().deleteBucket(args);
    }

    @Override
    public List<String> listBuckets() {
        return getConnect().listBuckets();
    }

    @Override
    public boolean doesBucketExist(DoesBucketExistArgs args) {
        args.validate();
        return getConnect().doesBucketExist(args);
    }

    @Override
    public void setObjectAcl(SetObjectAclArgs args) {
        args.validate();
        getConnect().setObjectAcl(args);
    }

    @Override
    public AccessControlList getObjectAcl(GetObjectAclArgs args) {
        args.validate();
        return getConnect().getObjectAcl(args);
    }

    @Override
    public FileBasicInfo uploadObject(UploadObjectArgs args) {
        return getConnect().uploadObject(determineInfoAndValidate(args));
    }

    @Override
    public FileBasicInfo uploadFile(UploadFileArgs args) {
        return getConnect().uploadFile(determineInfoAndValidate(args));
    }

    @Override
    public FileBasicInfo uploadUrl(UploadUrlArgs args) {
        return getConnect().uploadUrl(determineInfoAndValidate(args));
    }

    @Override
    public void copyObject(CopyObjectArgs args) {
        getConnect().copyObject(determineInfoAndValidate(args));
    }

    @Override
    public List<FileBasicInfo> listObjects(ListObjectsArgs args) {
        return getConnect().listObjects(determineInfoAndValidate(args));
    }

    @Override
    public FileBasicInfo getObjectMetadata(ObjectMetadataArgs args) {
        return getConnect().getObjectMetadata(determineInfoAndValidate(args));
    }

    @Override
    public boolean objectExist(DoesObjectExistArgs args) {
        return getConnect().objectExist(determineInfoAndValidate(args));
    }

    @Override
    public InputStream getObjectToStream(GetObjectToStreamArgs args) {
        return getConnect().getObjectToStream(determineInfoAndValidate(args));
    }

    @Override
    public File geObjectToFile(GetObjectToFileArgs args) {
        return getConnect().geObjectToFile(determineInfoAndValidate(args));
    }

    @Override
    public void deleteObject(DeleteObjectArgs args) {
        getConnect().deleteObject(determineInfoAndValidate(args));
    }

    @Override
    public void deleteObjects(DeleteObjectsArgs args) {
        getConnect().deleteObjects(determineInfoAndValidate(args));
    }

    @Override
    public String generatePresignedUrl(GetPresignedObjectUrlArgs args) {
        return getConnect().generatePresignedUrl(determineInfoAndValidate(args));
    }

    @Override
    public String getUrl(GetUrlArgs args) {
        return getConnect().getUrl(determineInfoAndValidate(args));
    }

    @Override
    public Boolean httpUploadFile(String url, File file) {
        return getConnect().httpUploadFile(url, file);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (clientDriver == null) {
            throw new StorageException("opcooc-storage - clientSource must not be null");
        }

        if (bucketConverter == null) {
            throw new StorageException("opcooc-storage - bucketConverter must not be null");
        }

        if (objectConverter == null) {
            throw new StorageException("opcooc-storage - objectConverter must not be null");
        }
    }
}
