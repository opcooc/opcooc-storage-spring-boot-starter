package com.opcooc.storage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlResult {

    /**
     * 可直接访问路径
     */
    private String openUrl;

    /**
     * 签名路径
     */
    private String preUrl;

    /**
     * bucketName
     */
    private String bucketName;

    /**
     * objectName
     */
    private String objectName;

    /**
     * 文件类型
     */
    private String contentType;

}
