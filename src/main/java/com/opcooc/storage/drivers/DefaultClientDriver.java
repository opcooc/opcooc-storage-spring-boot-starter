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
package com.opcooc.storage.drivers;

import java.io.IOException;

import com.opcooc.storage.client.Client;
import com.opcooc.storage.exception.StorageException;
import com.opcooc.storage.spring.boot.autoconfigure.ClientDriverProperty;

import lombok.extern.slf4j.Slf4j;

/**
 * @author shenqicheng
 * @since 1.0.0
 */
@Slf4j
public class DefaultClientDriver implements ClientDriver {

    private final ClientDriverProperty configuration;
    private final Client connect;
    private final String name;

    public DefaultClientDriver(ClientDriverProperty property, Client connect) {
        this.name = property.getDriver();
        this.configuration = property;
        this.connect = connect;
    }

    @Override
    public Client connect() {
        return connect;
    }

    @Override
    public ClientDriverProperty getConfiguration() {
        return configuration;
    }

    @Override
    public void close() throws IOException {
        log.debug("opcooc-storage - shutdown [{}] client driver", name);
        try {
            connect.close();
        } catch (Exception e) {
            throw new StorageException(e);
        }
    }
}
