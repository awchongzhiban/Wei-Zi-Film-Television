<template>
    <el-upload
        class="upload-demo"
        :on-change="handleFileChange"
        :auto-upload="false"
        :multiple="true"
        :show-file-list="false"
        style="margin-bottom: 1rem;"
    >
      <template #trigger>
        <el-button type="primary">选择文件</el-button>
      </template>

      <el-button style="margin-left: 1rem;" type="success" @click="submitUpload">确认上传</el-button>
    </el-upload>
  <el-table
      :data="fileDataList"    style="width: 100%"
      border
  >
    <el-table-column prop="name" label="文件名"></el-table-column>
    <el-table-column label="状态">
      <template #default="{ row }">
        <el-tag :type="getTagType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
        <el-progress v-if="row.isUpload && (row.status === 'uploading' || row.status === 'paused' || row.status === 'ready')" :percentage="row.progress" />
        <el-progress v-if="row.md5 === null && row.status === 'processing'" :percentage="30" :indeterminate="true">计算md5中...</el-progress>
      </template>
    </el-table-column>
    <el-table-column v-if="row.status !== 'pending'" label="操作" fixed="right" width="180">
      <template #default="{ row }">
        <el-button v-if="row.status === 'uploading' || row.status === 'ready'" @click="handlePpauseUpload(row)">
          暂停
        </el-button>
        <el-button v-if="row.status === 'paused'" @click="handleResumeUpload(row)">
          继续
        </el-button>
        <el-button v-if="row.status !== 'pending'" type="danger" @click="handleRemoveUpload(row)">
          删除
        </el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import {breakpointUpload, removeFile, verifyFileNameAndTypeAndGetFileId} from '@/api/uploadFile/index.js';
import {reactive, ref} from 'vue';
import SparkMD5 from 'spark-md5';

let globalCounter = ref(0); // 全局计数变量，从0开始
const fileDataList = ref([]);

let queryFileInfo = reactive({
  fileName: null,
  fileType: null,
  fileSize: null,
  movieMd5: null,
  movieShardTotal: 0,
});
let uploadQueue = ref([]); // 用于存储待上传的文件数据
const isUploading = ref(false);

function handleFileChange(file) {
  if (!file || !file.raw) {
    console.error('未接收到有效的文件');
    return;
  }
  fileDataList.value.push({
    id: globalCounter.value,
    file: file.raw,
    name: file.name,
    status: 'pending',
    progress: 0,
    isUpload: false,
    isExist: false,
    isPaused: false,
    md5: null
  });
  globalCounter.value++
}

async function submitUpload() {
  await Promise.all(fileDataList.value.map(async (fileData) => {
    if (fileData.status === 'pending') {
      await processFile(fileData);
    }
  }));
  console.log('所有文件处理完成');
}

async function processFile(fileData) {
  if (fileData.status === 'pending') {
    fileData.status = 'processing';
    try {
      fileData.md5 = await calculateFileMD5(fileData.file);
      console.log(`md5 ${fileData.name}: `, fileData.md5);
      fileData.status = 'ready';
      handleFileSuccess(fileData.file.name);

      // 将文件数据直接加入到上传队列并开始上传
      uploadQueue.value.push(fileData);
      console.log("startUploadProcess: ",fileData.name)
      startUploadProcess(); // 增加这一行，处理成功后立即开始上传
    } catch (error) {
      fileData.status = 'error';
      handleFileError(fileData.file.name, error);
    }
  }
}


async function startUploadProcess() {
  if (uploadQueue.value.length === 0 || isUploading.value) return; // 如果队列为空或正在上传则不执行
  isUploading.value = true;
  while (uploadQueue.value.length > 0) {
    const fileData = uploadQueue.value.shift();
    if (fileData.isPaused) continue;
    try {
      await uploadFileSequentially(fileData);
    } catch (error) {
      console.error(`上传失败: ${error}`);
    }
  }
  isUploading.value = false;
}

async function uploadFileSequentially(fileData) {
  try {
    const fileSize = fileData.file.size;
    const shardSize = 1024 * 1024 * 5; // 分片大小（5MB）
    const shardTotal = Math.ceil(fileSize / shardSize); // 切片后的总量
    fileData.status = 'uploading';
    const fileName = fileData.file.name;
    queryFileInfo.value = {
      fileName: fileName,
      fileType: fileName.slice((fileName.lastIndexOf(".") - 1 >>> 0) + 2) || '',
      fileSize: fileSize,
      movieMd5: fileData.md5,
      movieShardTotal: shardTotal,
    };
    const response = await verifyFileNameAndTypeAndGetFileId(queryFileInfo.value);
    if (response.data.code !== 200) {
      throw new Error(response.data.message);
    }
    const data = response.data.data;
    fileData.movieId = data.movieId;
    fileData.movieIndex = data.movieIndex;
    fileData.isExist = data.isExist;
    fileData.isUpload = data.isUpload;
    if (fileData.isExist === true && fileData.isUpload === false) {
      fileData.status = 'uploaded';
      ElNotification({
        message: `文件${fileData.file.name}已存在`,
        type: 'success',
        position: 'bottom-right',
      });
      return;
    }
    await uploadShards(fileData);
  } catch (error) {
    fileData.status = 'uploadFailed';
    ElNotification({
      message: `文件${fileData.file.name}上传过程中发生错误: ${error.message}`,
      type: 'error',
      position: 'bottom-right',
    });
  }
}

function handleFileSuccess(fileName) {
  ElNotification({
    message: `${fileName} 准备上传`,
    type: 'success',
    position: 'bottom-right',
  });
}


function handleFileError(fileName, error) {
  ElNotification({
    message: `${fileName} ${error}`,
    type: 'error',
    position: 'bottom-right',
  });
}

// 在uploadShards函数中，您需要将shardList与fileInfo分开 还差分辨文件ID看是第一次上传还是第二次上传
async function uploadShards(fileInfo) {
  const file = fileInfo.file;
  const fileSize = file.size;
  const shardSize = 1024 * 1024 * 5; // 分片大小（5MB）
  const shardTotal = Math.ceil(fileSize / shardSize); // 切片后的总量
  for (let i = fileInfo.movieIndex; i < shardTotal; i++) {
    if (fileInfo.isPaused) {
      const foundFileInfo = fileDataList.value.find(item => item.id === fileInfo.id);
      if (foundFileInfo) foundFileInfo.movieIndex = i;
      break;
    }
    const shardStart = i * shardSize;
    const shardEnd = Math.min((i + 1) * shardSize, fileSize);
    const shardFile = file.slice(shardStart, shardEnd);

    const data = new FormData();
    data.append('movieId', fileInfo.movieId); // 假设fileInfo在外部定义
    data.append('movieFile', shardFile);
    data.append('shardIndex', i);
    data.append('movieMd5', fileInfo.md5);
    try {
      await breakpointUpload(data).then(res => {
        if (res.data.code !== 200) {
          ElNotification({
            message: res.data.msg || '',
            type: 'error',
          });
          break;
        }
      }).catch(error => {
        ElNotification({
          message: error || '',
          type: 'error',
        });
        break;
      });
      updateProgress(fileInfo, i, shardTotal);
      if (i === shardTotal - 1) {
        ElNotification({
          message: '上传成功',
          type: 'success',
        });
        isUploading.value = false;
        console.log(`文件${fileInfo.file.name}上传成功`);
        fileInfo.status = 'uploadSuccess';
        break; // 最后一个分片
      }
    } catch (error) {
      console.error(`Upload shard ${i} failed:`, error);
      break; // 处理错误或中断上传
    }
  }
}

async function calculateFileMD5(file) {
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
}

function handlePpauseUpload(fileData) {
  fileData.isPaused = true;
  fileData.status = 'paused';
}

function handleResumeUpload(fileData) {
  fileData.isPaused = false;
  fileData.status = 'uploading';
  uploadQueue.value.push(fileData);
  startUploadProcess();
}

function handleRemoveUpload(fileData) {
  handlePpauseUpload(fileData);
  let isDelete = false;
  // 队列中清除
  const index = uploadQueue.value.indexOf(fileData);
  if (index !== -1) uploadQueue.value.splice(index, 1);
  // 队列中找不到或存在且不是第一片就删除
  if (index === -1 || fileData.movieIndex > 0) isDelete = true;
  if (isDelete && fileData.md5) {
    const data = new FormData();
    data.append('movieMd5', fileData.md5);
    removeFile(data).then(res => {
      if(res.data.code !== 200) {
        ElMessage({
          message: `${fileData.name} 删除失败!`,
          type: 'error',
        });
        return;
      }
    }).catch((error) => {
      ElMessage({
        message: error,
        type: 'error',
      });
      return;
    })
  }

  fileDataList.value = fileDataList.value.filter(item => item.id !== fileData.id)
}


// 获取Tag类型
function getTagType(status) {
  switch (status) {
    case 'processing':
      return 'primary';
    case 'ready':
    case 'uploadSuccess':
      return 'success';
    case 'uploading':
      return 'warning';
    case 'error':
    case 'uploaded':
    case 'uploadFailed':
      return 'danger';
    case 'paused':
    default:
      return 'info';
  }
}

// 获取状态标签
function getStatusLabel(status) {
  switch (status) {
    case 'processing':
      return '处理中';
    case 'ready':
      return '准备上传';
    case 'uploading':
      return '正在上传';
    case 'error':
    case 'uploadFailed':
      return '上传失败';
    case 'paused':
      return '已暂停';
    case 'uploadSuccess':
      return '上传成功';
    case 'uploaded':
      return '已存在';
    default:
      return '等待中';
  }
}

function updateProgress(fileInfo, currentIndex, total) {
  fileInfo.progress = Math.floor(((currentIndex + 1) / total) * 100);
}

</script>

<style scoped>
.progress-bar {
  width: 100%;
}

.pagination_container {
  position: relative;
  height: 40px;
  margin-top: 15px;

}

.el-pagination {
  position: absolute;
  right: 110px;
}
</style>

