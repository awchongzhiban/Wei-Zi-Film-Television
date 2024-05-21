// 引入pinia
import { defineStore } from 'pinia'
import {breakpointUpload, verifyFileNameAndTypeAndGetFileId} from "@/api/uploadFile/index.js";
import SparkMD5 from "spark-md5";

export const adminMovieUpload = defineStore('adminMovieUpload', {
    // 定义值
    state: () => ({
        queryFileInfo: {
            fileName: null,
            fileType: null,
            fileSize: null,
            movieMd5: null,
            movieShardTotal: 0,
        },
        fileData: {
            file: null
        },
        uploadProgress: 0,
        isPreparing: false,
        isUploading: false,
        uploadFileInfo: {
            fileName: null,
            fileType: null,
            fileSize: null,
            movieMd5: null,
            movieShardTotal: 0,
        },
        uploadFileList: [],
        uploadFileName: '',
        isPaused: false,
    }),

    getters: {
        Array: (state) => state.queryFileInfo,
        Array: (state) => state.fileData,
        Number: (state) => state.uploadProgress,
        boolean: (state) => state.isPreparing,
        boolean: (state) => state.isUploading,
        Array: (state) => state.uploadFileList,
        String: (state) => state.uploadFileName,
        boolean: (state) => state.isPaused,
    },

    actions: {
        setFileData(file) {
            this.fileData.file = file;
        },

        setQueryFileInfo(fileName, fileType, fileSize, movieMd5, movieShardTotal) {
            this.queryFileInfo.fileName = fileName;
            this.queryFileInfo.fileType = fileType;
            this.queryFileInfo.fileSize = fileSize;
            this.queryFileInfo.movieMd5 = movieMd5;
            this.queryFileInfo.movieShardTotal = movieShardTotal;
        },

        setUploadFileInfo(fileName, fileType, fileSize, movieMd5, movieShardTotal) {
            this.uploadFileInfo.fileName = fileName;
            this.uploadFileInfo.fileType = fileType;
            this.uploadFileInfo.fileSize = fileSize;
            this.uploadFileInfo.movieMd5 = movieMd5;
            this.uploadFileInfo.movieShardTotal = movieShardTotal;
        },

        triggerIsPaused() {
            this.isPaused = !this.isPaused;
        },

        async onUpload() {
            if (!this.fileData.file) return;
            const realFile = this.fileData.file;
            const formData = new FormData();
            formData.append('file', realFile);
            // 开始准备中
            this.isPreparing = true;
            // 暂停上传
            this.isPaused = false;
            const fileInfo = await this.verifyFile(formData);
            console.log("fileInfo: ", fileInfo);
            if (!fileInfo || fileInfo.isUpload === false) {
                ElNotification({
                    message: '文件已存在，请勿重复上传！',
                    type: 'error',
                    position: 'bottom-right',
                });
                this.isPreparing = false;
                return;
            }
            try {
                this.setQueryFileInfo(null, null, null, null, 0);
                this.isPreparing = false;
                this.isUploading = true;
                await this.uploadShards(realFile, fileInfo);
            } catch (error) {
                console.error('Upload error:', error);
            }
        },

        verifyFile(formData) {
            return new Promise(async (resolve, reject) => {
                const file = formData.get('file');
                const fileSize = file.size;
                const shardSize = 1024 * 1024 * 5; // 分片大小（5MB）
                const shardTotal = Math.ceil(fileSize / shardSize); // 切片后的总量
                // 获取文件名和后缀
                const fileName = file.name;
                const extension = fileName.split('.').pop();
                try {
                    ElNotification({
                        message: '准备上传中...',
                        type: 'success',
                        position: 'bottom-right',
                    });
                    const movieMd5 = await this.calculateFileMD5(file);
                    this.setQueryFileInfo(fileName, extension, file.size, movieMd5, shardTotal)
                    this.uploadFileName = fileName;
                    verifyFileNameAndTypeAndGetFileId(this.queryFileInfo)
                        .then(res => {
                            if (res.data.code !== 200) {
                                reject(new Error('验证文件信息失败'));
                            }
                            const data = res.data.data;
                            resolve({
                                movieId: data.movieId,
                                movieIndex: data.movieIndex,
                                isExist: data.isExist,
                                movieMd5: movieMd5,
                                isUpload: data.isUpload,
                            });
                        })
                        .catch(reject);
                } catch (error) {
                    this.isPreparing = false;
                    this.isUploading = false;
                    reject(error.message || '计算文件MD5时发生错误');
                }
            });
        },

        // 在uploadShards函数中，您需要将shardList与fileInfo分开 还差分辨文件ID看是第一次上传还是第二次上传
        async uploadShards(file, fileInfo) {
            const fileSize = file.size;
            const shardSize = 1024 * 1024 * 5; // 分片大小（5MB）
            const shardTotal = Math.ceil(fileSize / shardSize); // 切片后的总量
            for (let i = fileInfo.movieIndex; i < shardTotal; i++) {
                // 检查是否暂停
                while (this.isPaused) {
                    await new Promise(resolve => setTimeout(resolve, 100)); // 等待直到恢复
                }

                const shardStart = i * shardSize;
                const shardEnd = Math.min((i + 1) * shardSize, fileSize);
                const shardFile = file.slice(shardStart, shardEnd);

                const data = new FormData();
                data.append('movieId', fileInfo.movieId); // 假设fileInfo在外部定义
                data.append('movieFile', shardFile);
                data.append('shardIndex', i);
                data.append('movieMd5', fileInfo.movieMd5);

                try {
                    await breakpointUpload(data);
                    this.updateProgress(i, shardTotal);
                    if (i === shardTotal - 1) {
                        ElNotification({
                            message: '上传成功',
                            type: 'success',
                        });
                        this.uploadProgress = 0;
                        this.isUploading = false;
                        break; // 最后一个分片
                    }
                } catch (error) {
                    console.error(`Upload shard ${i} failed:`, error);
                    break; // 处理错误或中断上传
                }
            }
        },

        async calculateFileMD5(file) {
            const blockSize = 1024 * 1024 * 5; // 每次读取5MB
            const spark = new SparkMD5.ArrayBuffer();
            const reader = new FileReader();

            let blockStart = 0;
            while (blockStart < file.size) {
                const blockEnd = Math.min(blockStart + blockSize, file.size);
                const blob = file.slice(blockStart, blockEnd);

                reader.readAsArrayBuffer(blob);
                await new Promise(resolve => {
                    reader.onload = resolve;
                });

                const blockBuffer = reader.result;
                spark.append(blockBuffer);

                blockStart = blockEnd;
            }

            return spark.end();
        },

        updateProgress(currentIndex, total) {
            this.uploadProgress = Math.floor(((currentIndex + 1) / total) * 100);
        }
    }
})