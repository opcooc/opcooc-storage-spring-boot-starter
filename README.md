<p align="center">
  <a href="https://github.com/opcooc/opcooc-storage-spring-boot-starter">
   <img alt="opcooc-storage-logo" src="https://gitee.com/opcooc/opcooc-storage-spring-boot-starter/raw/main/doc/img/opcooc-storage.png">
  </a>
</p>

<p align="center">
  <strong>opcooc-storage-spring-boot-starter 是一个基于aws s3快速集成多storage client的启动器</strong>
</p>

<p align="center">
	<a target="_blank" href="https://search.maven.org/search?q=g:com.opcooc%20AND%20a:opcooc-storage-spring-boot-starter">
		<img alt='maven' src="https://img.shields.io/maven-central/v/com.opcooc/opcooc-storage-spring-boot-starter" />
	</a>
	<a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.html">
		<img alt='license' src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=square" />
	</a>
	<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
		<img alt='JDK' src="https://img.shields.io/badge/JDK-1.8+-green.svg" />
	</a>
	<a target="_blank" href="https://github.com/opcooc/opcooc-storage-spring-boot-starter/wiki" title="参考文档">
		<img alt='Docs' src="https://img.shields.io/badge/Docs-latest-blueviolet.svg" />
	</a>
	<a target="_blank" href='https://gitee.com/opcooc/opcooc-storage-spring-boot-starter/stargazers'>
	  <img alt='gitee star' src='https://gitee.com/opcooc/opcooc-storage-spring-boot-starter/badge/star.svg?theme=white'/>
	</a>
	<a target="_blank" href='https://github.com/opcooc/opcooc-storage-spring-boot-starter'>
		<img alt="github star" src="https://img.shields.io/github/stars/opcooc/opcooc-storage-spring-boot-starter?style=social"/>
	</a>
	<a target="_blank" href='https://gitter.im/opcooc/opcooc-storage-spring-boot-starter?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge'>
		<img alt="gitter" src="https://img.shields.io/gitter/room/opcooc/opcooc-storage-spring-boot-starter"/>
	</a>

</p>

-------------------------------------------------------------------------------

- QQ交流群 `789585778`，可获取各项目详细图文文档、疑问解答
[![](http://pub.idqqimg.com/wpa/images/group.png)](https://jq.qq.com/?_wv=1027&k=iRannIfW)

## 文档 | Documentation

[![CN doc](https://img.shields.io/badge/文档-中文版-blue.svg)](README.md)
[![EN doc](https://img.shields.io/badge/document-English-blue.svg)](README.md)


## 特性

1. 支持 **多客户端动态切换** (使用内置的spel动态参数，session，header获取客户端驱动, 还支持支持自定义获取哦)。
2. 支持 **自定义注解** ，需继承OS(支撑多客户端动态切换的关键)。
3. 提供 **自定义客户端驱动来源** 方案。
4. 提供项目启动后 **动态增加移除客户端驱动** 方案(增加移除后会有Event消息通知)。
5. 支持  **多层客户端嵌套切换** 。（ServiceA >>>  ServiceB >>> ServiceC）。
6. 提供 **bucketConverter** bucketName 自定义转换器(有自动创建bucketName判断，会通过环境变量判断)。
7. 提供 **objectConverter** objectName 自定义转换器。
8. 支持 [aws s3](https://docs.aws.amazon.com/AmazonS3/latest/gsg/GetStartedWithS3.html), 
[阿里云oss](https://help.aliyun.com/document_detail/64919.html), 
[minio](http://docs.minio.org.cn/docs/master/how-to-use-aws-sdk-for-java-with-minio-server), 
[腾讯云cos](https://cloud.tencent.com/document/product/436/37421), 
[七牛云kodo](https://developer.qiniu.com/kodo/manual/4086/aws-s3-compatible) 等。

## 快速开始

-   引用依赖
    -   Maven:
        ```xml
            <dependency>
              <groupId>com.opcooc</groupId>
              <artifactId>opcooc-storage-spring-boot-starter</artifactId>
              <version>1.2.1</version>
            </dependency>
        ```
    -   Gradle
        ```groovy
        implementation 'com.opcooc:opcooc-storage-spring-boot-starter:1.2.1'
        ```

## 添加配置，在 `application.yml` 中添加配置信息
-   基础yaml配置。

    ```yaml
          storage:
            primary: s3_minio #默认的客户端类型
            strict: true #是否启用严格模式,默认不启动. 严格模式下未匹配到客户端直接报错, 非严格模式下则使用默认客户端primary所设置的客户端
            enabled: true #是否开启 opcooc-storage
            driver:
              s3_minio: #配置文件key名称
                type: S3 #默认驱动类型(默认为S3)
                default-bucket: opcooc #默认主目录(需要保证唯一)
                endpoint: http://xxx.com #访问域名
                username: xxx #账号
                password: xxx #密码
                region: cn-north-1 #区域
                path-style: true #路径样式(默认为true)
                auto-create-bucket: true #是否自动创建目标bucket

    ```
-   其他yaml配置(oss, cos, kodo)。
    ```yaml
          storage:
            primary: s3_minio #默认的客户端类型
            strict: true #是否启用严格模式,默认不启动. 严格模式下未匹配到客户端直接报错, 非严格模式下则使用默认客户端primary所设置的客户端
            enabled: true #是否开启 opcooc-storage
            driver:
              s3_oss:
                default-bucket: opcooc
                endpoint: http://oss-cn-shanghai.aliyuncs.com
                username: xxx
                password: xxx
                path-style: false
                region: cn-north-1
                auto-create-bucket: true
              s3_cos:
                default-bucket: opcooc
                endpoint: https://bucketname.cos.ap-shanghai.myqcloud.com
                username: xxx
                password: xxx
                region: cn-north-1
                auto-create-bucket: true
              s3_kodo:
                default-bucket: opcooc
                endpoint: http://s3-cn-south-1.qiniucs.com
                username: xxx
                password: xxx
                region: cn-north-1
                auto-create-bucket: true
    ```
    
-   使用 @OS 切换客户端驱动。

    **@OS** 可以注解在方法上或类上，**同时存在就近原则 方法上注解 优先于 类上注解**。
    
    |     **注解**   |                   **结果**          |
    | :-----------: | :--------------------------------------: |
    |    没有@OS     |                默认客户端驱动             |
    | @OS("driverName") | driverName为具体某个客户端驱动的名称    |
    
-   然后就开始玩耍吧~

    ```java
        @RestController
        @OS("#tenantName")
        @RequestMapping("/api")
        @RequiredArgsConstructor
        public class ClientController {
        
            private final StorageHelper client;
    
            @OS("#tenantName")
            @PostMapping("/createFolder")
            public void createFolder(@RequestParam(defaultValue = "s3_minio") String tenantName, @RequestParam String folderName) {
                client.createFolder(SetFolderArgs.builder().bucketName(BUCKET_NAME).folderName(folderName).build());
            }
        
        }
    ```
## 术语表

   | **对象存储**    | **文件系统**|
   | :-----------: | :-----------:|
   |Object|对象或者文件|
   |Bucket|主目录(存储空间)|
   |Endpoint|访问域名|
   |Region|地域或者数据中心|
   |Object Meta|文件元信息。用来描述文件信息，例如长度，类型等|
   |Data|文件数据|
   |Key|文件名|
   |ACL (Access Control List)|存储空间或者文件的权限|
   
-------------------------------------------------------------------------------

> 该展示只是 opcooc-storage-spring-boot-starter 功能的一小部分。如果您想了解更多信息，请参阅项目[demo项目](https://github.com/opcooc/opcooc-storage-spring-boot-starter-example) 或 wiki: [documentation](https://github.com/opcooc/opcooc-storage-spring-boot-starter/wiki).

## 友情链接

- [UMS（用户管理脚手架集成）](https://gitee.com/pcore/UMS)

- [justAuth-spring-security-starter（spring security 集成 JustAuth 实现第三方授权登录）](https://gitee.com/pcore/just-auth-spring-security-starter)

## 参考项目

| Project                                                              |
| -------------------------------------------------------------------- |
| [dynamic-datasource-spring-boot-starter](https://gitee.com/baomidou/dynamic-datasource-spring-boot-starter)|

## 参与贡献

1. Fork [本项目](https://github.com/opcooc/opcooc-storage-spring-boot-starter)
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

## License

opcooc-storage-spring-boot-starter is under the Apache 2.0 license. See the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0) file for details.
