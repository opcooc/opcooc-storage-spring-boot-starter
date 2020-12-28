<p align="center">
  <a href="https://github.com/opcooc/opcooc-storage">
   <img alt="opcooc-storage-logo" src="https://gitee.com/opcooc/opcooc-storage/raw/master/doc/img/opcooc-storage.png">
  </a>
</p>

<p align="center">
  <strong>opcooc-storage-boot-starter是一个基于aws s3快速集成多storage client的启动器</strong>
</p>

<p align="center">
	<a target="_blank" href="https://search.maven.org/search?q=g:com.opcooc%20AND%20a:opcooc-storage-boot-starter">
		<img alt='maven' src="https://img.shields.io/maven-central/v/com.opcooc/opcooc-storage-boot-starter" />
	</a>
	<a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.html">
		<img alt='license' src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=square" />
	</a>
	<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
		<img alt='JDK' src="https://img.shields.io/badge/JDK-1.8+-green.svg" />
	</a>
	<a target="_blank" href="https://github.com/opcooc/opcooc-storage/wiki" title="参考文档">
		<img alt='Docs' src="https://img.shields.io/badge/Docs-latest-blueviolet.svg" />
	</a>
	<a target="_blank" href='https://gitee.com/opcooc/opcooc-storage/stargazers'>
	  <img alt='gitee star' src='https://gitee.com/opcooc/opcooc-storage/badge/star.svg?theme=white'/>
	</a>
	<a target="_blank" href='https://github.com/opcooc/opcooc-storage'>
		<img alt="github star" src="https://img.shields.io/github/stars/opcooc/opcooc-storage?style=social"/>
	</a>
	<a target="_blank" href='https://gitter.im/opcooc/opcooc-storage?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge'>
		<img alt="gitter" src="https://img.shields.io/gitter/room/opcooc/opcooc-storage"/>
	</a>

</p>

<div style="text-align: center;">
    <table>
        <tr>
            <td align="center" width="200"><img src="https://gitee.com/opcooc/opcooc-storage/raw/master/doc/img/aws-s3.png" width="36" alt="aws-s3"/></td>
            <td align="center" width="200"><img src="https://gitee.com/opcooc/opcooc-storage/raw/master/doc/img/tencent-cos.png" width="70" alt="tencent-cos"/></td>
            <td align="center" width="200"><img src="https://gitee.com/opcooc/opcooc-storage/raw/master/doc/img/minio.png" width="70" alt="minio"/></td>
            <td align="center" width="200"><img src="https://gitee.com/opcooc/opcooc-storage/raw/master/doc/img/aliyun-oss.png" width="70" alt="aliyun-oss"/></td>
            <td align="center" width="200"><img src="https://gitee.com/opcooc/opcooc-storage/raw/master/doc/img/qiliu.png" width="70" alt="qiliu"/></td>
        </tr>
    </table>
</div>

-------------------------------------------------------------------------------

- QQ交流群 `789585778`，可获取各项目详细图文文档、疑问解答
[![](http://pub.idqqimg.com/wpa/images/group.png)](https://jq.qq.com/?_wv=1027&k=iRannIfW)

## 文档 | Documentation

[![CN doc](https://img.shields.io/badge/文档-中文版-blue.svg)](README.md)
[![EN doc](https://img.shields.io/badge/document-English-blue.svg)](README.md)


## 特性

1. 支持 **多客户端动态切换** (使用内置的spel动态参数，session，header获取客户端驱动, 还支持支持自定义获取哦)。
2. 支持客户端敏感配置信息 **加密**  ENC(), DecryptCallback(自定义解密回调)。
3. 支持 **自定义注解** ，需继承OS(支撑多客户端动态切换的关键)。
4. 提供 **自定义客户端驱动来源** 方案。
5. 提供项目启动后 **动态增加移除客户端驱动** 方案(增加移除后会有Event消息通知)。
6. 支持  **多层客户端嵌套切换** 。（ServiceA >>>  ServiceB >>> ServiceC）。
7. 提供基于Spring的客户端驱动 **健康检查**。
8. 提供 **bucketConverter** bucketName 自定义转换器(有自动创建bucketName判断，会通过环境变量判断)。
9. 提供 **objectConverter** objectName 自定义转换器。

## 快速开始

-   引用依赖
    -   Maven:
        ```xml
            <dependency>
              <groupId>com.opcooc</groupId>
              <artifactId>opcooc-storage-boot-starter</artifactId>
              <version>1.2.3</version>
            </dependency>
        ```
    -   Gradle
        ```groovy
        compile group: 'com.opcooc', name: 'opcooc-storage-boot-starter', version: '1.2.3'
        ```

## 添加配置，在 `application.yml` 中添加配置信息
-   基础yaml配置。

    ```yaml
        spring:
          storage:
            dynamic:
              primary: s3_minio #默认的客户端类型
              strict: true #是否启用严格模式,默认不启动. 严格模式下未匹配到客户端直接报错, 非严格模式下则使用默认客户端primary所设置的客户端
              enabled: true #是否开启 opcooc-storage
              health: true #健康检查
              driver:
                s3_minio: #配置文件key名称
                  driver-name: s3_minio #非必填, 客户端驱动名称唯一标识 (默认为配置文件key名称)
                  type: S3 #默认驱动类型(默认为S3)
                  default-bucket: opcooc #默认主目录(需要保证唯一)
                  end-point: http://xxx.com #访问域名
                  access-key: xxx #访问密钥
                  secret-key: xxx #密钥
                  region: cn-north-1 #区域
                  first-path: first #第一目录层级(默认为空, 当存在时所有路径都以 [firstPath + objectName] 拼接 ** 需要自己实现ObjectConverter **)
                  path-style: true #路径样式(默认为true)
                  auto-create-bucket: true #是否自动创建目标bucket

    ```
-   默认解密yaml配置。

    ```yaml
        spring:
          storage:
            dynamic:
              primary: s3_minio #默认的客户端类型
              strict: true #是否启用严格模式,默认不启动. 严格模式下未匹配到客户端直接报错, 非严格模式下则使用默认客户端primary所设置的客户端
              enabled: true #是否开启 opcooc-storage
              health: true #健康检查
              driver:
                s3_enc:
                  driver-name: s3_minio_enc
                  default-bucket: opcooc
                  end-point: ENC(xxx) #默认解密方式
                  access-key: ENC(xxx)
                  secret-key: ENC(xxx)
                  region: cn-north-1
                  public-key: xxx
                  auto-create-bucket: true

    ```
-   自定义解密yaml配置。

    ```yaml
        spring:
          storage:
            dynamic:
              primary: s3_minio #默认的客户端类型
              strict: true #是否启用严格模式,默认不启动. 严格模式下未匹配到客户端直接报错, 非严格模式下则使用默认客户端primary所设置的客户端
              enabled: true #是否开启 opcooc-storage
              health: true #健康检查
              driver:
                s3_callback:
                  driver-name: s3_callback
                  default-bucket: opcooc
                  end-point: http://xxx.com
                  access-key: xxx
                  secret-key: xxx
                  region: cn-north-1
                  public-key: xxx #解密公匙(如果未设置默认使用全局的)
                  customize-decrypt-callback: com.example.demo.config.DemoDecryptCallback #自定义解密回调clazz
                  auto-create-bucket: true

    ```
-   拓展自定义客户端yaml配置。

    ```yaml
        spring:
          storage:
            dynamic:
              primary: s3_minio #默认的客户端类型
              strict: true #是否启用严格模式,默认不启动. 严格模式下未匹配到客户端直接报错, 非严格模式下则使用默认客户端primary所设置的客户端
              enabled: true #是否开启 opcooc-storage
              health: true #健康检查
              driver:
                s3_customize_client_driver:
                  driver-name: s3_customize_client_driver
                  default-bucket: opcooc
                  end-point: http://xxx.com
                  access-key: xxx
                  secret-key: xxx
                  region: cn-north-1
                  customize-client-driver: com.example.demo.config.DemoClientDriver #自定义客户端clazz
                  auto-create-bucket: true

    ```
-   其他yaml配置(oss, cos, kodo)。
    ```yaml
        spring:
          storage:
            dynamic:
              primary: s3_minio #默认的客户端类型
              strict: true #是否启用严格模式,默认不启动. 严格模式下未匹配到客户端直接报错, 非严格模式下则使用默认客户端primary所设置的客户端
              enabled: true #是否开启 opcooc-storage
              health: true #健康检查
              driver:
                s3_oss:
                  driver-name: s3_oss
                  default-bucket: opcooc
                  end-point: http://oss-cn-shanghai.aliyuncs.com
                  access-key: xxx
                  secret-key: xxx
                  path-style: false
                  region: cn-north-1
                  auto-create-bucket: true
                s3_cos:
                  driver-name: s3_cos
                  default-bucket: opcooc
                  end-point: https://bucketname.cos.ap-shanghai.myqcloud.com
                  access-key: xxx
                  secret-key: xxx
                  region: cn-north-1
                  auto-create-bucket: true
                s3_kodo:
                  driver-name: s3_kodo
                  default-bucket: opcooc
                  end-point: http://s3-cn-south-1.qiniucs.com
                  access-key: xxx
                  secret-key: xxx
                  region: cn-north-1
                  auto-create-bucket: true
    ```
-   健康检查yaml配置。

    ```yaml
    spring:
      storage:
        dynamic:
          health: true
    
    #健康检查
    management:
      endpoints:
        web:
          exposure:
            include: "*"
      endpoint:
        health:
          show-details: always

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
        @AllArgsConstructor
        @RequestMapping("/api")
        @Api("api测试")
        @OS("#tenantName")
        public class ClientController {
        
            private final StorageClient client;
    
            @GetMapping("/createFolder")
            @ApiOperation("创建文件夹")
            @OS("#tenantName")
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
   |AccessKey|AccessKeyId和AccessKeySecret的统称，访问密钥|
   |Object Meta|文件元信息。用来描述文件信息，例如长度，类型等|
   |Data|文件数据|
   |Key|文件名|
   |ACL (Access Control List)|存储空间或者文件的权限|
   
-------------------------------------------------------------------------------

> 该展示只是 opcooc-storage 功能的一小部分。如果您想了解更多信息，请参阅项目demo [documentation](https://gitee.com/opcooc/opcooc-storage/tree/master/demo/opcooc-storage-boot-starter-example/src/main/java/com/example/demo/controller).

## 友情链接

- [UMS（用户管理脚手架集成）](https://gitee.com/pcore/UMS)

- [justAuth-spring-security-starter（spring security 集成 JustAuth 实现第三方授权登录）](https://gitee.com/pcore/just-auth-spring-security-starter)

## 鸣谢

| Project                                                              |
| -------------------------------------------------------------------- |
| [dynamic-datasource-spring-boot-starter](https://gitee.com/baomidou/dynamic-datasource-spring-boot-starter)|

## 参与贡献

1. Fork [本项目](https://github.com/opcooc/opcooc-storage)
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

## License

opcooc-storage is under the Apache 2.0 license. See the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0) file for details.
