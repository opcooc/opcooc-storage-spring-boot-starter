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
package com.opcooc.storage.model;

import com.amazonaws.services.s3.Headers;
import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import static com.amazonaws.util.DateUtils.cloneDate;

/**
 * @author shenqicheng
 * @since 1.2.0
 */
@Data
public class FileBasicInfo {

    /**
     * 上传文件的bucket
     */
    private String bucketName;

    /**
     * 上传的对象地址
     */
    private String key;

    /**
     * 新对象的ETag值
     */
    private String eTag;

    /**
     * The content MD5
     */
    private String contentMd5;
    /**
     * 获取对象的大小（以字节为单位）
     */
    public Long contentLength;

    /**
     * 自定义用户元数据，在响应中以x-amz-meta- 标头前缀表示
     */
    private Map<String, String> userMetadata = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

    /**
     * 所有其他（非用户自定义）标头，例如Content-Length，Content-Type，等
     */
    private Map<String, Object> metadata = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);

    /**
     * 返回指定键的元数据的原始值
     * @param key key
     * @return 信息
     */
    public Object getRawMetadataValue(String key) {
        return metadata.get(key);
    }

    /**
     * 返回指定用户元数据的值
     * @param key key
     * @return 信息
     */
    public String getUserMetaDataOf(String key) {
        return userMetadata == null ? null : userMetadata.get(key);
    }

    /**
     * 文件最后修改时间
     * @return 修改时间
     */
    public Date getLastModified() {
        return cloneDate((Date)metadata.get(Headers.LAST_MODIFIED));
    }

    /**
     * 设置文件最后修改时间
     * @param lastModified 最后修改时间
     */
    public void setLastModified(Date lastModified) {
        metadata.put(Headers.LAST_MODIFIED, lastModified);
    }

    /**
     * 获取对象的大小（以字节为单位）
     *
     * @return 对象的大小
     */
    public Long getContentLength() {
        Long contentLength = this.contentLength == null || this.contentLength == 0L
                ? (Long) metadata.get(Headers.CONTENT_LENGTH) : this.contentLength;

        if (contentLength == null) {
            return 0L;
        }
        return contentLength;
    }

    /**
     * 获取对象类型
     * @return 对象类型
     */
    public String getContentType() {
        return (String)metadata.get(Headers.CONTENT_TYPE);
    }

    /**
     * 获取文件的md5值
     * @return md5值
     */
    public String getContentMd5() {
        return this.contentMd5 == null ? (String) metadata.get(Headers.CONTENT_MD5) : this.contentMd5;
    }

    /**
     * 获取对象的ETag
     * @return 对象的ETag
     */
    public String getETag() {
        return (String) metadata.get(Headers.ETAG);
    }

}
