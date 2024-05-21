package com.weizi.common.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description minio配置
 * @Author df
 * @Date 2023/11/28
 */
@Data
@Configuration
public class MinioConfig {
    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.endpoint}")
    private String url;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.bucket-name-slice}")
    private String bucketNameSlice;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
}

