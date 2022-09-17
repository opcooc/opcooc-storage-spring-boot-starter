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

import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.opcooc.storage.args.CopyObjectArgs;
import com.opcooc.storage.args.CopySource;
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
import com.opcooc.storage.exception.StorageException;
import com.opcooc.storage.model.FileBasicInfo;
import com.opcooc.storage.spring.boot.autoconfigure.ClientDriverProperty;
import com.opcooc.storage.toolkit.ContentTypeUtils;
import com.opcooc.storage.toolkit.HttpUtils;
import com.opcooc.storage.toolkit.StorageUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author shenqicheng
 * @since 1.0.0
 */
@Slf4j
public class S3Client implements Client {

    /**
     * client
     */
    private final AmazonS3 client;

    /**
     * configuration
     */
    private final ClientDriverProperty configuration;

    public S3Client(ClientDriverProperty configuration) {
        AWSCredentials credentials = new BasicAWSCredentials(configuration.getUsername(), configuration.getPassword());

        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder
                .EndpointConfiguration(configuration.getEndpoint(), configuration.getRegion());

        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(configuration.getPathStyle())
                .withEndpointConfiguration(endpointConfiguration)
                .build();

        log.debug("opcooc-storage - init client driver [{}] success", configuration.getDriver());
        this.configuration = configuration;
        this.client = s3;
    }

    @Override
    public void createFolder(SetFolderArgs args) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(args.getBucketName(), args.getFolderName(), new ByteArrayInputStream(new byte[]{}), metadata);
        try {
            client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void setBucketAcl(SetBucketAclArgs args) {
        try {
            client.setBucketAcl(args.getBucketName(), args.getCannedAcl());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public AccessControlList getBucketAcl(GetBucketAclArgs args) {
        try {
            return client.getBucketAcl(args.getBucketName());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void setBucketPolicy(SetBucketPolicyArgs args) {
        try {
            client.setBucketPolicy(args.getBucketName(), args.getPolicyText());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public BucketPolicy getBucketPolicy(GetBucketPolicyArgs args) {
        try {
            return client.getBucketPolicy(args.getBucketName());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteBucketPolicy(DeleteBucketPolicyArgs args) {
        try {
            client.deleteBucketPolicy(args.getBucketName());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public String createBucket(CreateBucketArgs args) {
        try {
            Bucket bucket = client.createBucket(args.getBucketName());
            return bucket.getName();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteBucket(DeleteBucketArgs args) {
        try {
            ObjectListing objectListing = client.listObjects(args.getBucketName());
            while (true) {
                for (S3ObjectSummary s3ObjectSummary : objectListing.getObjectSummaries()) {
                    client.deleteObject(args.getBucketName(), s3ObjectSummary.getKey());
                }

                if (objectListing.isTruncated()) {
                    objectListing = client.listNextBatchOfObjects(objectListing);
                } else {
                    break;
                }
            }
            client.deleteBucket(args.getBucketName());
        } catch (Exception e) {
            throw new StorageException(e);
        }

    }

    @Override
    public List<String> listBuckets() {
        try {
            List<Bucket> buckets = client.listBuckets();
            return buckets.stream().map(Bucket::getName).collect(toList());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public boolean doesBucketExist(DoesBucketExistArgs args) {
        try {
            return client.doesBucketExistV2(args.getBucketName());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void setObjectAcl(SetObjectAclArgs args) {
        try {
            client.setObjectAcl(args.getBucketName(), args.getObjectName(), args.getCannedAcl());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public AccessControlList getObjectAcl(GetObjectAclArgs args) {
        try {
            return client.getObjectAcl(args.getBucketName(), args.getObjectName());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public FileBasicInfo uploadObject(UploadObjectArgs args) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(args.getObjectSize());
            metadata.setContentType(args.getContentType());
            PutObjectResult result = client.putObject(args.getBucketName(), args.getObjectName(), args.getStream(), metadata);
            return StorageUtil.createFileBasicInfo(result, args, args.getObjectSize());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public FileBasicInfo uploadFile(UploadFileArgs args) {
        try {
            PutObjectResult result = client.putObject(args.getBucketName(), args.getObjectName(), args.getFile());
            return StorageUtil.createFileBasicInfo(result, args, args.getObjectSize());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public FileBasicInfo uploadUrl(UploadUrlArgs args) {
        try {
            File file = args.getFile();
            HttpUtils.downloadToFile(args.getUrl(), file);
            PutObjectResult result = client.putObject(args.getBucketName(), args.getObjectName(), file);
            return StorageUtil.createFileBasicInfo(result, args, file.length());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void copyObject(CopyObjectArgs args) {
        CopySource source = args.getSource();
        try {
            client.copyObject(new CopyObjectRequest(source.getBucketName(), source.getObjectName(), args.getBucketName(), args.getObjectName()));
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public List<FileBasicInfo> listObjects(ListObjectsArgs args) {
        ListObjectsV2Request req = new ListObjectsV2Request()
                .withBucketName(args.getBucketName())
                .withPrefix(args.getPrefix())
                .withMaxKeys(args.getMaxKeys());

        ListObjectsV2Result result;
        List<FileBasicInfo> objectList = new ArrayList<>();
        do {
            result = client.listObjectsV2(req);

            for (S3ObjectSummary object : result.getObjectSummaries()) {
                objectList.add(StorageUtil.createFileBasicInfo(object, args));
            }
            // If there are more than maxKeys keys in the bucket, get a continuation token
            // and list the next objects.
            req.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());
        return objectList;
    }

    @Override
    public boolean objectExist(DoesObjectExistArgs args) {
        try {
            return client.doesObjectExist(args.getBucketName(), args.getObjectName());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    /**
     * 判断对象是否存在
     * @param args
     */
    private void checkObjectExist(ObjectArgs args) {
        boolean objectExist = client.doesObjectExist(args.getBucketName(), args.getObjectName());
        if (!objectExist) {
            throw new StorageException("opcooc-storage - bucket name: [%s], object name [%s] does not exist", args.getBucketName(), args.getObjectName());
        }
    }

    @Override
    public FileBasicInfo getObjectMetadata(ObjectMetadataArgs args) {
        try {
            //判断对象是否存在
            checkObjectExist(args);
            ObjectMetadata object = client.getObjectMetadata(args.getBucketName(), args.getObjectName());
            return StorageUtil.createFileBasicInfo(object, args);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public InputStream getObjectToStream(GetObjectToStreamArgs args) {
        try {
            //判断对象是否存在
            checkObjectExist(args);
            S3Object s3Object = client.getObject(args.getBucketName(), args.getObjectName());
            return s3Object.getObjectContent();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public File geObjectToFile(GetObjectToFileArgs args) {
        try {
            //判断对象是否存在
            checkObjectExist(args);
            client.getObject(new GetObjectRequest(args.getBucketName(), args.getObjectName()), args.getFile());
            return args.getFile();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteObject(DeleteObjectArgs args) {
        try {
            client.deleteObject(args.getBucketName(), args.getObjectName());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteObjects(DeleteObjectsArgs args) {
        try {
            List<DeleteObjectsRequest.KeyVersion> objects = args.getObjects().stream().map(DeleteObjectsRequest.KeyVersion::new).collect(toList());
            DeleteObjectsRequest request = new DeleteObjectsRequest(args.getBucketName()).withKeys(objects);
            client.deleteObjects(request);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public String getUrl(GetUrlArgs args) {
        try {
            URL url = client.getUrl(args.getBucketName(), args.getObjectName());
            return url.toExternalForm();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public String generatePresignedUrl(GetPresignedObjectUrlArgs args) {
        try {
            //过期时间
            Date expiry = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(args.getExpiry()));
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(args.getBucketName(), args.getObjectName())
                    .withMethod(args.getMethod())
                    .withExpiration(expiry);

            if (args.isSpecType() && (args.getMethod() == HttpMethod.PUT || args.getMethod() == HttpMethod.POST)) {
                //强制前端需要在的上传方法添加对应的 Request Header( key: Content-Type, value: {fileType} )
                //不开启需要前端自行添加没有强制要求
                //用于解决文件上传到文件服务器之后没有对应的文件类型问题
                String fileType = ContentTypeUtils.getContentType(args.getObjectName());
                request.putCustomRequestHeader(ContentTypeUtils.CONTENT_TYPE, fileType);
            }

            URL url = client.generatePresignedUrl(request);
            return url.toExternalForm();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void close() throws IOException {
        client.shutdown();
    }
}
