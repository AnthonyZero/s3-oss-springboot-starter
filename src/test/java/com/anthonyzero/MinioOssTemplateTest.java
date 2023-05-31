package com.anthonyzero;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.anthonyzero.core.OssTemplate;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@SpringBootApplication(scanBasePackages = "com.anthonyzero")
@SpringBootTest
@ActiveProfiles("minio")
public class MinioOssTemplateTest {

    /**
     * 测试用OSS名字
     */
    private static final String TEST_BUCKET_NAME = "s3-oss";

    /**
     * 测试用文件名,该文件在测试资源文件夹下
     */
    private static final String TEST_OBJECT_NAME = "test.txt";

    @Autowired
    private OssTemplate ossTemplate;

    /**
     * 创建存储桶
     */
    @BeforeEach
    @SneakyThrows
    //@Test
    public void init() {
        boolean success = ossTemplate.createBucket(TEST_BUCKET_NAME);
        Assertions.assertEquals(true, success);
    }

    /**
     * 上传测试文件
     */
    @SneakyThrows
    @Test
    public void uploadFile(){
        boolean ok = ossTemplate.existBucket(TEST_BUCKET_NAME);
        if(ok) {
            ossTemplate.putObject(TEST_BUCKET_NAME, TEST_OBJECT_NAME,
                    new FileInputStream(ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + TEST_OBJECT_NAME)));
        }
    }

    /**
     * 测试获取存储桶
     */
    @Test
    public void getBucket() {
        Optional<Bucket> bucket = ossTemplate.getBucket(TEST_BUCKET_NAME);
        Assertions.assertEquals(TEST_BUCKET_NAME, bucket.get().getName());
    }


    /**
     * 获取对象下载URL
     */
    @Test
    public void getObjectUrl() {
        String url = ossTemplate.getObjectUrl(TEST_BUCKET_NAME, TEST_OBJECT_NAME, 10);
        System.out.println("URL: " + url);
        // 断言生成的链接必定包含过期时间字段
        Assertions.assertTrue(url.contains("X-Amz-Expires"));
    }


    /**
     * 获取对象
     */
    @Test
    @SneakyThrows
    public void getObject() {
        S3Object s3Object = ossTemplate.getObject(TEST_BUCKET_NAME, TEST_OBJECT_NAME);
        Assertions.assertEquals(TEST_BUCKET_NAME, s3Object.getBucketName());
        Assertions.assertEquals(TEST_OBJECT_NAME, s3Object.getKey());
        String content = IOUtils.toString(s3Object.getObjectContent().getDelegateStream());
        // 断言返回的文本包含文件的内容
        Assertions.assertTrue(content.contains("Hello,S3 Oss!"));
    }

    /**
     * 获取存储桶下面的所有文件摘要信息
     */
    @Test
    @SneakyThrows
    public void getObjectsAll(){
        List<S3ObjectSummary> allObjectsByPrefix = ossTemplate.getAllObjectsByPrefix(TEST_BUCKET_NAME, "");
        allObjectsByPrefix.forEach(x -> {
            System.out.println(x.getKey());
        });
    }

    /**
     * 获取上传URL 进行上传
     */
    @Test
    @SneakyThrows
    public void getObjectUpload() {
        String testUploadObjectName = "upload.txt"; //key(objectName)
        String testObjectContent = "it is a new content(for upload url)"; //内容
        String uploadUrl = ossTemplate.getPresignedObjectPutUrl(TEST_BUCKET_NAME, testUploadObjectName, 5);
        // 断言生成的链接必定包含过期时间字段
        Assertions.assertTrue(uploadUrl.contains("X-Amz-Expires"));
        System.out.println("URL: " + uploadUrl);

        Assertions.assertThrows(Exception.class,
                () -> ossTemplate.getObject(TEST_BUCKET_NAME, testUploadObjectName));
        Assertions.assertEquals(200, upload(uploadUrl, testObjectContent));

        S3Object s3Object = ossTemplate.getObject(TEST_BUCKET_NAME, testUploadObjectName);
        Assertions.assertEquals(TEST_BUCKET_NAME, s3Object.getBucketName());
        Assertions.assertEquals(testUploadObjectName, s3Object.getKey());
        String content = IOUtils.toString(s3Object.getObjectContent().getDelegateStream());
        // 断言返回的文本包含文件的内容
        Assertions.assertTrue(content.contains(testObjectContent));
    }



    @SneakyThrows
    private int upload(String url, String content) {
        // Create the connection and use it to upload the new object using the pre-signed
        // URL.
        URL opurl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) opurl.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(content);
        out.close();

        // Check the HTTP response code. To complete the upload and make the object
        // available,
        // you must interact with the connection object in some way.
        System.out.println("HTTP response code: " + connection.getResponseCode());
        return connection.getResponseCode();
    }
}
