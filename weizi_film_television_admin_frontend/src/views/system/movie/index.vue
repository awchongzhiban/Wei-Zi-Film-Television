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
        row-key="movieId"
        :loading="tableLoading"
        :header-cell-style="{'text-align':'center'}"
        border
    >
      <el-table-column fixed prop="movieName" label="影片名称:" width="500">
        <template #default="scope">
          <div style="text-align:center;">
            <el-image
                style="width: 100px; height: 100px"
                :src="scope.row.poster || defaultPoster"
                fit="contain"
                :error="defaultPoster"
                :default-src="defaultPoster"
                preview-teleported="true"
                :preview-src-list="[scope.row.poster || defaultPoster]"
                hide-on-click-modal="true"
            />
          </div>
          <div style="text-align:center;">
            <el-tag
                v-for="(genreId, index) in scope.row.genreIdList"
                :type="getTagType(index)"
                :key="index"
                :style="index === 0 ? 'margin-bottom: 5px;' : 'margin-left: 5px; margin-bottom: 5px;'"
                effect="dark"
                round
            >
              {{ genres[genreId] ?? '—/—' }}
            </el-tag>
          </div>
          <div style="text-align:center;">
            <span style="margin-right: 10px">{{ scope.row.movieName }}</span>
            <el-tag round type="warning">
              {{ getFormattedFileSize(scope.row.fileSize) }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="isMerge" align="center" label="状态:" width="250">
        <template #default="scope">
          <el-tag
              :type="scope.row.isMerge  ? 'success' : 'warning'"
          >{{ scope.row.isMerge ? "上传完成" : "暂未完成" }} {{ scope.row.isPlayer ? '可播放' : '暂不可播放' }}</el-tag>
          <el-tag :type="scope.row.status ? 'danger' : 'primary'" style="margin-left: 10px">{{ scope.row.status ? "下架" : "上架" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="上传时间" width="180"/>
      <el-table-column fixed="right" align="center" label="操作">
          <template #default="scope">
            <el-button v-if="scope.row.isPlayer" type="primary" size="small" @click="handlePlayer(scope.row.movieMd5)">播放</el-button>
            <el-button type="success" size="small" @click="handleEdit(scope.row.movieId)">编辑</el-button>
            <el-button v-if="scope.row.isPlayer" type="danger" size="small" @click="handleRemove(scope.row.movieMd5, scope.row.movieName)">删除</el-button>
          </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <div class="pagination_container">
        <el-pagination v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize"
            :page-sizes="[2, 10, 20, 30, 40, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total"
            @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>
    <!-- 编辑的抽屉 -->
    <el-drawer v-model="movieFormShow" title="编辑影片信息" width="50%" :before-close="handleClose">
      <!-- 表单 -->
      <el-form :model="form" :rules="rules" label-width="120px" ref="formRef" style="padding-right: 10px">
        <el-row>
          <el-col :span="24">
            <!-- 影片海报编辑时显示 -->
            <el-form-item prop="poster">
              <el-upload
                  action="#"
                  ref="uploadRef"
                  class="avatar-uploader"
                  :show-file-list="false"
                  :http-request="uploadImage"
                  :before-upload="beforePosterUpload"
              >
                <img @error="handleFormPosterImageError" :src="form.poster || defaultPoster" class="avatar" alt="" style="max-width: 200px; max-height: 200px;">
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="影片名称" prop="movieName">
              <el-input v-model="form.movieName" placeholder="请输入影片名称"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="电影类型" prop="genreIdList">
              <el-select
                  v-model="form.genreIdList"
                  multiple
                  filterable
                  collapse-tags
                  collapse-tags-tooltip
                  :max-collapse-tags="3"
                  placeholder="请选择电影类型"
              >
                <el-option
                    v-for="(value, key) in genres"
                    :key="key"
                    :label="value"
                    :value="parseInt(key)"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="状态" prop="status">
              <el-switch
                  v-model="form.status"
                  inline-prompt
                  active-text="上架"
                  inactive-text="下架"
                  :active-value="false"
                  :inactive-value="true"
                  style="--el-switch-off-color: #ff4949"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
            <span class="dialog-footer">
                <el-button @click="handleClose">取消</el-button>
                <el-button type="primary" @click="handleSubmit">提交</el-button>
            </span>
      </template>
    </el-drawer>
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
import {
  searchMovieList,
  getGenreTagMap,
  update,
  uploadPoster,
  removeMovie,
  getDetail
} from '@/api/movie/index.js'

import Player from 'xgplayer'
import HlsPlugin from "xgplayer-hls"
import 'xgplayer/dist/index.min.css';
import { getToken } from "@/utils/token/index.js";
import request from '@/utils/request.js';

onMounted(() => {
  tableLoading.value = true;
  getGenreTagMap().then(res => {
    if (res.data.code === 200) genres.value = res.data.data;
  }).finally(
      () => {
        handleSearchMovieList();
      }
  )
})

// 常量
const defaultPoster = "/defaultimg/default_poster.jpg";
// 定义一个颜色类型数组，根据你的需要增减
const tagTypes = ['primary', 'success', 'info', 'warning', 'danger'];
const baseURL = request.defaults.baseURL;

// 响应式变量
let tableLoading = ref(false);
let total = ref(0);
let queryForm = ref({
    movieName: undefined,
    pageNum: 1,
    pageSize: 10
})
let player = null;
const formRef = ref(null);
const dialogVisible = ref(false);
let movieList = ref([]);
let genres = ref({});
let movieFormShow = ref(false);

// 初始化表单值
const initialFormValue = {
  movieId: undefined,
  movieName: undefined,
  poster: undefined,
  status: false,
  genreIdList: undefined
};

// 新增和编辑数据时的表单数据
let form = ref({...initialFormValue});

// 表单验证规则
let rules = ref({
  movieName: [
    { required: true, message: '请输入影片名称', trigger: 'blur' }
  ],
  status: [
    { type: 'boolean', message: '请选择状态', trigger: 'change' }
  ],
  genreIdList: [
    { type: 'array', required: true, message: '请选择电影类型', trigger: 'change' }
  ]
});

// 查询所有影片
function handleSearchMovieList() {
  tableLoading.value = true;
  searchMovieList(queryForm.value).then(res => {
    if (res.data.code === 200) {
      movieList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  }).finally(() => {
    tableLoading.value = false;
  })
}

// 搜索
function handleQuery() {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = 10;
  tableLoading.value = true;
  searchMovieList(queryForm.value).then(res => {
    if(res.data.code === 200) {
      // 获取数据
      movieList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
  tableLoading.value = false;
}

// 重置
function handleRest() {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = 10;
  queryForm.value.movieName = undefined;
  searchMovieList(queryForm.value).then(res => {
    if(res.data.code === 200) {
      // 获取数据
      movieList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}

function handleSizeChange(sizeNumber) {
  queryForm.value.pageSize = sizeNumber;
  searchMovieList(queryForm.value).then(res => {
    if(res.data.code === 200) {
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
    if(res.data.code === 200) {
      // 获取数据
      movieList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}

// 播放按钮
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

// 弹窗关闭时，销毁播放器
function onDialogClosed() {
  // 销毁播放器
  if (player) {
    player.destroy();
    player = null;
  }
}

// 编辑按钮，根据movieId查询对应的数据，弹出表单，回显数据
function handleEdit(movieId) {
  // 先查询数据，再弹窗
  getDetail(movieId).then(res => {
    if(res.data.code === 200) {
      // 保障后端返回的字段名和前端字段名相同，可以一一赋值
      form.value = res.data.data;
      movieFormShow.value = true;
    } else {
      ElMessage({
        message: '数据查询失败！',
        type: 'error',
      })
    }
  })
}

// 提交表单,根据form.movieId值判断是新增还是编辑【有movieId值】
function handleSubmit() {
  // 做数据校验
  formRef.value.validate((valid) => {
    if (valid && form.value.movieId) {
      // 编辑
      update(form.value).then(res => {
        if(res.data.code === 200) {
          // 关闭窗口
          movieFormShow.value = false;
          // 刷新列表
          handleSearchMovieList();
          // 弹窗提示新增成功
          ElMessage({
            message: '编辑影片成功！',
            type: 'success',
          });
          handleClose();
        }
        else if (res.data.code === 422) {
          getGenreTagMap().then(res => {
            if (res.data.code === 200) genres.value = res.data.data;
          })
          ElMessage.error('请检查电影类型是否存在')
        }
      })
    } else {
      // 表单验证失败
      ElMessage.error('表单验证失败，请检查输入')
    }
  })
}

// 关闭弹窗
function handleClose() {
  movieFormShow.value = false;
  form.value = {...initialFormValue};
  // 清空表单验证状态和错误信息
  formRef.value?.resetFields();
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
          if(res.data.code === 200) {
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

// 海报上传前的验证
function beforePosterUpload(file) {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
  if (!isJPG) {
    ElMessage.error('上传影片海报图片只能是 JPG 或 PNG 格式！');
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    ElMessage.error('上传影片海报图片大小不能超过 2MB！');
  }
  return isJPG && isLt2M;
}

async function uploadImage(file) {
  const formData = new FormData();
  // 添加影片ID到 FormData 中
  formData.append('movieId', form.value.movieId); // 确保传递影片ID
  // 添加文件到 FormData 中
  formData.append('file', file.file);
  try {
    const response = await uploadPoster(formData);
    if (response.status === 200) {
      // 使用FileReader读取文件并显示
      const reader = new FileReader();
      reader.onload = (e) => {
        // 将Base64编码的URL赋值给form.posterUrl
        form.value.poster = e.target.result;
      };
      reader.readAsDataURL(file.file);
      handleSearchMovieList();
    } else {
      throw new Error(response.statusText); // 抛出错误以触发 catch 分支
    }
  } catch (error) {
    console.error('上传图片失败:', error.message);
    ElMessage.error(error.message);
  }
}

// 获取图片URL
function getPosterUrl(base64String) {
  if (!base64String) return defaultPoster;
  return `data:image/png;base64, ${base64String}`; // 将 base64 字符串拼接为图片 URL
}

// 图片加载失败时，显示默认图片
function handlePosterImageError(row) {
  row.Poster = defaultPoster; // 替换成你的展位图路径或其他提示信息
}

// 获取图片URL
function handleFormPosterImageError(event) {
  event.target.src = defaultPoster; // 设置默认图像路径
}

function getTagType(index) {
  // 使用genreIdList的长度对颜色类型数组长度取余，以确保循环使用
  const typeIndex = index % tagTypes.length;
  // 返回当前索引对应的颜色类型
  return tagTypes[typeIndex];
}

// 格式化文件大小
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