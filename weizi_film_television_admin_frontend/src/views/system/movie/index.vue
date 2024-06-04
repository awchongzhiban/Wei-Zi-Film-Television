<template>
  <!-- 头部搜索框 -->
  <el-form :model="queryForm" :inline="true" label-position="top" style="display: block;" @submit.prevent>
    <el-row>
      <el-col :span="18">
        <el-form-item label="影片名称：">
          <el-input v-model="queryForm.movieName" @keyup.enter.native="handleQuery"/>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="功能：">
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button type="warning" @click="handleRest">重置</el-button>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
    <!-- 列表 -->
    <el-table
        :data="movieList"
        style="width: 100%"
        table-layout="auto"
        row-key="movieId" border
        :header-cell-style="{'text-align':'center'}"
    >
      <el-table-column fixed prop="movieName" label="影片名称" >
        <template #default="scope">
          <span style="margin-right: 10px">{{ scope.row.movieName }}</span>
          <el-tag>
            {{ getFormattedFileSize(scope.row.fileSize) }}
          </el-tag>
        </template>
      </el-table-column>
        <el-table-column prop="isMerge" align="center" label="是否合并">
          <template #default="scope">
            <el-tag
                :type="scope.row.isMerge  ? 'success' : 'warning'"
                disable-transitions
            >{{ scope.row.isMerge ? "已合并" : "未合并" }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间"/>
        <el-table-column fixed="right" align="center" label="操作">
            <template #default="scope">
              <el-button v-if="scope.row.isPlayer" link type="primary" size="small" @click="handlePlayer(scope.row.movieMd5)">播放</el-button>
              <el-button v-if="scope.row.isPlayer" link type="danger" size="small" @click="handleRemove(scope.row.movieMd5, scope.row.movieName)">删除</el-button>

            </template>
        </el-table-column>
    </el-table>
    <!-- 分页 -->
    <div class="pagination_container">
        <el-pagination v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize"
            :page-sizes="[2, 10, 20, 30, 40, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total"
            @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <el-dialog
        v-model="dialogVisible"
        title="播放影片"
        width="50%"
        @closed="onDialogClosed"
    >
      <div id="playerContainer"></div>
    </el-dialog>
</template>

<script setup>
import {onMounted, ref, nextTick} from 'vue'
// 导入接口
import {removeMovie, searchMovieList} from '@/api/movie/index.js'

import Player from 'xgplayer'
import HlsPlugin from "xgplayer-hls"
import 'xgplayer/dist/index.min.css';
import {getToken} from "@/utils/token/index.js";
import request from '@/utils/request.js';
let total = ref(0);
let player = null;
let queryForm = ref({
    movieName: undefined,
    pageNum: 1,
    pageSize: 10
})

const baseURL = request.defaults.baseURL;

const dialogVisible = ref(false);

onMounted(() => {
  handleSearchMovieList();
})

function handlePlayer(movieMd5) {
  dialogVisible.value = true;
  nextTick(() => {
    try {
      player = new Player({
        id: "playerContainer",
        lang: "zh",
        volume: 0.6,
        autoplay: true,
        screenShot: true,
        playsinline: true,
        url: baseURL + '/admin/movie/get/movie/' + getToken("weiziToken") + "/" + movieMd5 + "/m3u8",
        plugins: [HlsPlugin],
        fluid: true,
        playbackRate: [2, 1.5, 1, 0.5],
        pip: true
      });
    } catch (error) {
      console.error('获取M3U8数据失败', error);
    }
  });
}

function onDialogClosed() {
  // 销毁播放器
  if (player) {
    player.destroy();
    player = null;
  }
}
let defaultAvatar = "/defaultimg/default_avatar.png";

let movieList = ref([])

function getAvatarUrl(base64String) {
  if (!base64String) return defaultAvatar;
  return `data:image/png;base64, ${base64String}`; // 将 base64 字符串拼接为图片 URL
}

function handleAvatarImageError(row) {
  row.avatar = defaultAvatar; // 替换成你的展位图路径或其他提示信息
}

function handleFormAvatarImageError(event) {
  event.target.src = defaultAvatar; // 设置默认图像路径
}

// 查询所有影片
function handleSearchMovieList() {
  searchMovieList(queryForm.value).then(res => {
    if (res.data.code === 200) {
      movieList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}

// 重置
function handleRest() {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = 10;
  queryForm.value.movieName = undefined;
  searchMovieList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      movieList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}
// 搜索
function handleQuery() {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = 10;
  searchMovieList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      movieList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}

function handleSizeChange(sizeNumber) {
  queryForm.value.pageSize = sizeNumber;
  searchMovieList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      movieList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}
// 点击下一页，上一页
function handleCurrentChange(pageNumber) {
  queryForm.value.pageNum = pageNumber;
  searchMovieList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      movieList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}

// 删除按钮，弹出是否要删除数据，确定就删除，取消就不删除
function handleRemove(movieMd5, movieName) {
  // movieId其实是点击操作下的删除按钮时才会有数据
  ElMessageBox.confirm(
      `确定要删除【${movieName}】数据吗?`,
      '删除影片',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error',
      }
  ).then(() => {
        removeMovie(movieMd5).then(res => {
          if(res.data.code == 200) {
            ElMessage({
              message: '删除成功',
              type: 'success',
            })
            // 刷新列表
            handleSearchMovieList();
          } else {
            ElMessage({
              message: res.data.msg || '',
              type: 'error',
            })
          }
        })
      })
      .catch((error) => {
        ElMessage({
          message: error,
          type: 'error',
        })
      })
}

// 头像上传前的验证
/*function beforeAvatarUpload(file) {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
  if (!isJPG) {
    this.$message.error('上传头像图片只能是 JPG 或 PNG 格式！');
  }
  const isLt2M = file.size / 1024 / 1024 < 5;
  if (!isLt2M) {
    this.$message.error('上传头像图片大小不能超过 5MB！');
  }
  return isJPG && isLt2M;
}

async function uploadPosterFile(file) {
  const formData = new FormData();
  // 添加影片ID到 FormData 中
  formData.append('movieId', form.value.movieId); // 确保传递影片ID
  // 添加文件到 FormData 中
  formData.append('file', file.file);
  try {
    const response = await uploadPoster(formData);
    if (response.status === 200) {
      handleSearchMovieList();
    } else {
      throw new Error(response.statusText); // 抛出错误以触发 catch 分支
    }
  } catch (error) {
    console.error('上传图片失败:', error.message);
  }
}*/

//
function getFormattedFileSize(fileSizeInBytes) {
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  let i = 0;

  while (fileSizeInBytes >= Math.pow(1024, i + 1) && i < units.length - 1) {
    i++;
  }

  const roundedFileSize = Math.round(fileSizeInBytes / Math.pow(1024, i) * 100) / 100;
  return `${roundedFileSize} ${units[i]}`;
}

</script>

<style lang="scss" scoped>
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