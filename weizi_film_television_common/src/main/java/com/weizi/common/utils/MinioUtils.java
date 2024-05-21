package com.weizi.common.utils;

import com.google.common.collect.Sets;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * @Author AWei
 * @Description: Minio工具类
 * @Date 2024/05/14 17:34
 */
@Slf4j
@Component
public class MinioUtils {
    private final MinioClient minioClient;

    public MinioUtils(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * 文件上传/文件分块上传
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param sliceIndex 分片索引
     * @param file       文件
     */
    public Boolean uploadFile(String bucketName, String objectName, Integer sliceIndex, MultipartFile file) {
        try {
            if (sliceIndex != null) {
                objectName = objectName.concat("/").concat(Integer.toString(sliceIndex));
            }
            // 写入文件
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            log.debug("上传到minio文件|uploadFile|参数：bucketName：{}，objectName：{}，sliceIndex：{}"
                    , bucketName, objectName, sliceIndex);
            return true;
        } catch (Exception e) {
            log.error("文件上传到Minio异常|参数：bucketName:{},objectName:{},sliceIndex:{}|异常:{}", bucketName, objectName, sliceIndex, e.getMessage());
            return false;
        }
    }

    /**
     * 创建桶，放文件使用
     *
     * @param bucketName 桶名称
     */
    public Boolean createBucket(String bucketName) {
        try {
            if (!minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            return true;
        } catch (Exception e) {
            log.error("Minio创建桶异常!|参数：bucketName:{}|异常:{}", bucketName, e);
            return false;
        }
    }

    /**
     * 文件合并
     *
     * @param bucketName       桶名称
     * @param objectName       对象名称
     * @param sourceObjectList 源文件分片数据
     */
    public Boolean composeFile(String bucketName, String objectName, List<ComposeSource> sourceObjectList) {
        // 合并操作
        try {
            minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .sources(sourceObjectList)
                            .build());
            log.info("objectName1: {}",objectName);
            return true;
        } catch (Exception e) {
            log.error("Minio文件按合并异常!|参数：bucketName:{},objectName:{}|异常:{}", bucketName, objectName, e);
            return false;
        }
    }

    /**
     * 多个文件删除
     *
     * @param bucketName 桶名称
     */
    public void removeSliceFiles(String bucketName, List<DeleteObject> delObjects) {
        try {
            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(
                            RemoveObjectsArgs.builder().bucket(bucketName).objects(delObjects).build());
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("Error in deleting object {} | {}", error.objectName(), error.message());
            }
        } catch (Exception e) {
            log.error("Minio多个文件删除异常!|参数：bucketName:{},异常:{}", bucketName, e.getMessage());
        }
    }

    /**
     * 单个文件删除
     *
     * @param bucketName 桶名称
     */
    public Boolean removeFile(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return true;
        } catch (Exception e) {
            log.error("Minio单个文件删除异常!|参数：bucketName:{},objectName:{}|异常:{}", bucketName, objectName, e);
            return false;
        }
    }

    /**
     * 获取文件流
     *
     * @param bucketName 桶名称
     */
    public InputStream getFileStream(String bucketName, String objectName) {
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return stream;
        } catch (Exception e) {
            log.error("Minio获取文件流异常!|参数：bucketName:{},objectName:{}|异常:{}", bucketName, objectName, e);
        }
        return null;
    }

    /**
     * 获取桶中的文件对象
     *
     * @param bucketName       桶名称
     * @param prefixObjectName 前缀对象名称
     */
    public Set<String> getFileObjects(String bucketName, String prefixObjectName) {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName)
                            .prefix(prefixObjectName.concat("/")).build());
            Set<String> objectNames = Sets.newHashSet();
            for (Result<Item> item : results) {
                objectNames.add(item.get().objectName());
            }
            return objectNames;
        } catch (Exception e) {
            log.error("Minio获取文件流异常!|参数：bucketName:{},prefixObjectName:{}|异常:{}", bucketName, prefixObjectName, e);
        }
        return null;
    }
}
