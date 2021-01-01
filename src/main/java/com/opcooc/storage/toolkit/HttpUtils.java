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

package com.opcooc.storage.toolkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.opcooc.storage.exception.StorageException;

/**
 * http 上传文件, 下载文件类
 *
 * @author shenqicheng
 * @since 1.2.0
 */
public class HttpUtils {

    private HttpUtils() {
    }

    public static void downloadToFile(String urlStr, File file) throws Exception {
        URL url = new URL(urlStr);
        try (final ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
             final FileOutputStream fos = new FileOutputStream(file)) {
            fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
    }

    public static Boolean httpUploadFile(String urlStr, File file) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            // 不使用缓存机制 直接提交到服务器上
            connection.setChunkedStreamingMode(0);
            connection.setRequestMethod("PUT");
            try (final FileChannel fileChannel = FileChannel.open(Paths.get(file.toURI()), StandardOpenOption.READ);
                 final WritableByteChannel writableByteChannel = Channels.newChannel(connection.getOutputStream())) {
                fileChannel.transferTo(0, fileChannel.size(), writableByteChannel);
            } catch (Exception e) {
                throw new IOException("opcooc-storage - file upload error！");
            }
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            throw new StorageException("opcooc-storage - file upload error！");
        }
    }
}
