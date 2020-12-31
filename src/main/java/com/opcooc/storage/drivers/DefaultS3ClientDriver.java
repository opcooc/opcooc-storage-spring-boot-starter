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
package com.opcooc.storage.drivers;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.opcooc.storage.client.Client;
import com.opcooc.storage.client.DefaultS3Client;
import com.opcooc.storage.spring.boot.autoconfigure.ClientDriverProperty;
import com.opcooc.storage.exception.StorageException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author shenqicheng
 * @since 1.2.0
 */
@Slf4j
public class DefaultS3ClientDriver implements ClientDriver {

    private final ClientDriverProperty configuration;
    private final AmazonS3 s3;
    private final String name;

    public DefaultS3ClientDriver(ClientDriverProperty configuration) {
        this.name = configuration.getDriverName();
        // 校验配置合法性
        configuration.preCheckThrow();
        this.configuration = configuration;
        this.s3 = init(configuration);
    }

    @Override
    public Client connect() {
        return new DefaultS3Client(s3);
    }

    @Override
    public ClientDriverProperty getConfiguration() {
        return configuration;
    }

    public AmazonS3 init(ClientDriverProperty configuration) {

        AWSCredentials credentials = new BasicAWSCredentials(configuration.getAccessKey(), configuration.getSecretKey());

        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder
                .EndpointConfiguration(configuration.getEndPoint(), configuration.getRegion());

        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(configuration.getPathStyle())
                .withEndpointConfiguration(endpointConfiguration)
                .build();

        log.debug("opcooc-storage - init client driver [{}] success", name);
        return s3;
    }

    @Override
    public void close() throws IOException {
        log.debug("opcooc-storage - shutdown [{}] client driver", name);
        try {
            s3.shutdown();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }
}
