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
import com.opcooc.storage.exception.StorageException;
import com.opcooc.storage.model.FileBasicInfo;
import com.opcooc.storage.model.UrlResult;
import com.opcooc.storage.spring.boot.autoconfigure.ClientDriverProperty;
import com.opcooc.storage.toolkit.ContentTypeUtils;
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
    public String getDefaultBucketName() {
        return configuration.getDefaultBucket();
    }

    @Override
    public void setBucketAcl(BucketAclArgs args) {
        try {
            client.setBucketAcl(args.getBucketName(), args.getCannedAcl());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public AccessControlList getBucketAcl(BucketAclArgs args) {
        try {
            return client.getBucketAcl(args.getBucketName());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void setBucketPolicy(BucketPolicyArgs args) {
        try {
            client.setBucketPolicy(args.getBucketName(), args.getPolicyText());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public BucketPolicy getBucketPolicy(BucketPolicyArgs args) {
        try {
            return client.getBucketPolicy(args.getBucketName());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteBucketPolicy(BucketPolicyArgs args) {
        try {
            client.deleteBucketPolicy(args.getBucketName());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void setObjectAcl(ObjectAclArgs args) {
        try {
            client.setObjectAcl(args.getBucketName(), args.getObjectName(), args.getCannedAcl());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public AccessControlList getObjectAcl(ObjectAclArgs args) {
        try {
            return client.getObjectAcl(args.getBucketName(), args.getObjectName());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void createFolder(ObjectArgs args) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(args.getBucketName(), args.getObjectName(), new ByteArrayInputStream(new byte[]{}), metadata);
        try {
            client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public String createBucket(BucketArgs args) {
        try {
            Bucket bucket = client.createBucket(args.getBucketName());
            return bucket.getName();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void deleteBucket(BucketArgs args) {
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
    public boolean doesBucketExist(BucketArgs args) {
        try {
            return client.doesBucketExistV2(args.getBucketName());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }


    @Override
    public FileBasicInfo uploadObject(UploadArgs args) {
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
    public FileBasicInfo uploadFile(UploadArgs args) {
        try {
            PutObjectResult result = client.putObject(args.getBucketName(), args.getObjectName(), args.getFile());
            return StorageUtil.createFileBasicInfo(result, args, args.getObjectSize());
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void copyObject(CopyObjectArgs args) {
        ObjectArgs source = args.getSource();
        try {
            client.copyObject(new CopyObjectRequest(source.getBucketName(), source.getObjectName(), args.getBucketName(), args.getObjectName()));
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public boolean objectExist(ObjectArgs args) {
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
            throw new StorageException("bucket name: [%s], object name [%s] does not exist", args.getBucketName(), args.getObjectName());
        }
    }

    @Override
    public FileBasicInfo getObjectMetadata(ObjectArgs args) {
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
    public InputStream getObjectToStream(ObjectArgs args) {
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
    public File geObjectToFile(ObjectToFileArgs args) {
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
    public void deleteObjects(DeleteObjectArgs args) {
        try {
            List<DeleteObjectsRequest.KeyVersion> objects = args.getObjectNames().stream().map(DeleteObjectsRequest.KeyVersion::new).collect(toList());
            DeleteObjectsRequest request = new DeleteObjectsRequest(args.getBucketName()).withKeys(objects);
            client.deleteObjects(request);
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public List<FileBasicInfo> listObjects(ListObjectArgs args) {
        ListObjectsV2Request req = new ListObjectsV2Request()
                .withBucketName(args.getBucketName())
                .withPrefix(args.getObjectName())
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
    public String getUrl(ObjectArgs args) {
        try {
            URL url = client.getUrl(args.getBucketName(), args.getObjectName());
            return url.toExternalForm();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public UrlResult generatePresignedUrl(PresignedUrlArgs args) {
        try {
            //过期时间
            String fileType = ContentTypeUtils.getContentType(args.getObjectName());
            Date expiry = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(args.getExpiry()));
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(args.getBucketName(), args.getObjectName())
                    .withMethod(args.getMethod())
                    .withExpiration(expiry);

            if (Boolean.TRUE.equals(args.getSpecType()) && (args.getMethod() == HttpMethod.PUT || args.getMethod() == HttpMethod.POST)) {
                //强制前端需要在的上传方法添加对应的 Request Header( key: Content-Type, value: {fileType} )
                //不开启需要前端自行添加没有强制要求
                //用于解决文件上传到文件服务器之后没有对应的文件类型问题
                request.putCustomRequestHeader(ContentTypeUtils.CONTENT_TYPE, fileType);
            }
            String openUrl = "";
            if (args.getObtainOpenUrl()) {
                URL url = client.getUrl(args.getBucketName(), args.getObjectName());
                openUrl = url.toExternalForm();
            }
            URL url = client.generatePresignedUrl(request);
            return UrlResult.builder()
                    .bucketName(args.getBucketName())
                    .objectName(args.getObjectName())
                    .preUrl(url.toExternalForm())
                    .openUrl(openUrl)
                    .contentType(fileType)
                    .build();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void close() throws IOException {
        client.shutdown();
    }
}
