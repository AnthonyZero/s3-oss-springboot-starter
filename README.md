## s3-oss-springboot-starter
兼容S3协议的通用文件存储SDK，支持兼容S3协议的云存储方案

- MINIO
- 阿里云
- 华为云
- 腾讯云
- 京东云

...

## 使用

### 配置文件

```yaml
oss:
  endpoint: your endpoint
  access-key: your access-key
  secret-key: your secret-key
```

### 代码使用

```java
@Autowired
private OssTemplate ossTemplate;
/**
 * 上传文件
 * FILE_KEY采用uuid,避免原始文件名中带"-"符号导致下载的时候解析出现异常
 *
 * @param file 资源
 */
@PostMapping("/upload")
public Result upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
    ossTemplate.putObject(BUCKET_NAME, FILE_KEY, file.getInputStream());
    return Result.ok();
}
```
