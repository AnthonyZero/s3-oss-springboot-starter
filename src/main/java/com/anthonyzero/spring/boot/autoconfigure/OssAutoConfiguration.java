package com.anthonyzero.spring.boot.autoconfigure;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.anthonyzero.core.OssTemplate;
import com.anthonyzero.spring.boot.autoconfigure.properties.OssProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ OssProperties.class })
@ConditionalOnClass(OssTemplate.class)
@ConditionalOnProperty(prefix = OssProperties.PREFIX, name = "enable", havingValue = "true", matchIfMissing = true)
public class OssAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AmazonS3.class)
    public AmazonS3 amazonS3(OssProperties properties) {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                properties.getEndpoint(), properties.getRegion());
        AWSCredentials awsCredentials = new BasicAWSCredentials(properties.getAccessKey(),
                properties.getSecretKey());
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        return AmazonS3Client.builder().withEndpointConfiguration(endpointConfiguration)
                .withClientConfiguration(clientConfiguration).withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding().withPathStyleAccessEnabled(properties.getPathStyleAccess()).build();
    }

    @Bean
    @ConditionalOnMissingBean(OssTemplate.class)
    @ConditionalOnBean(AmazonS3.class)
    public OssTemplate ossTemplate(OssProperties properties, AmazonS3 amazonS3) {
        return new OssTemplate(properties, amazonS3);
    }
}
