<template>
    <!-- 头部搜索框 -->
  <el-form :model="queryForm" :inline="true" label-position="top">
    <el-row>
      <el-col :span="6">
        <el-form-item label="用户名：">
          <el-input v-model="queryForm.username" />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="昵称：">
          <el-input v-model="queryForm.nickname" />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="邮箱：">
          <el-input v-model="queryForm.email" />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="手机号：">
          <el-input v-model="queryForm.mobile" />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <el-form-item label="功能：">
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button type="warning" @click="handleRest">重置</el-button>
          <el-button v-if="showSave" type="primary" @click="handleAdd()">新增</el-button>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
    <!-- 列表 -->
    <el-table :data="adminList" style="width: 100%" row-key="adminId" border>
        <el-table-column fixed prop="username" label="用户名" width="180" />
        <el-table-column prop="nickname" label="昵称" width="180" />
        <el-table-column prop="email" label="邮箱" width="180">
          <template #default="scope">
            {{ scope.row.email || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="mobile" label="手机号码" width="180">
          <template #default="scope">
            {{ scope.row.mobile || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="scope">
            <!-- 使用 Base64 编码的头像数据作为图片的 src 属性值 -->
            <el-image :src="getAvatarUrl(scope.row.avatar)" preview-teleported="true" :preview-src-list="[getAvatarUrl(scope.row.avatar)]" style="max-width: 50px; max-height: 50px;" @error="handleAvatarImageError(scope.row)" >
              <div slot="error" class="image-slot img-err" style="height: 50px; width:50px;">
                <img :src="defaultAvatar" alt="">
              </div>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <span v-if="!row.status" style="color: green">可用</span>
            <span v-else style="color: red">不可用</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="200"/>
        <el-table-column prop="updateTime" label="修改时间" width="200"/>
        <el-table-column prop="remark" label="备注" width="200" />
        <el-table-column v-if="showUpdate || showDelete" fixed="right" label="操作" width="200">
            <template #default="scope">
                <el-button v-if="showUpdate" link type="success" size="small" @click="handleEdit(scope.row.adminId)">修改</el-button>
                <el-button v-if="showDelete" link type="danger" size="small" @click="handleRemove(scope.row.adminId, scope.row.username)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页 -->
    <div class="pagination_container">
        <el-pagination v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize"
            :page-sizes="[10, 20, 30, 40, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total"
            @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <!-- 新增和修改的弹窗 -->
  <el-dialog v-if="showSave || showUpdate" v-model="adminFormShow" :title="adminTitle" width="50%" :before-close="handleClose">
    <!-- 表单 -->
    <el-form :model="form" :rules="rules" label-width="120px" ref="formRef">
      <el-row v-if="isEditMode && showUploadAvatar">
        <el-col :span="24">
          <!-- 头像编辑时显示 -->
          <el-form-item label="头像" prop="avatar">
            <el-upload
                action="#"
                ref="uploadRef"
                class="avatar-uploader"
                :show-file-list="false"
                :http-request="uploadImage"
                :before-upload="beforeAvatarUpload"
            >
              <img @error="handleFormAvatarImageError" :src="form.avatar || defaultAvatar" class="avatar" alt="" style="max-width: 50px; max-height: 50px;">
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="form.nickname" placeholder="请输入昵称"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="!isEditMode">
        <el-col :span="24">
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" placeholder="请输入密码" type="password"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="!isEditMode">
        <el-col :span="24">
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" placeholder="请确认密码" type="password"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="手机号" prop="mobile">
            <el-input v-model="form.mobile" placeholder="请输入手机号"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="性别" prop="sex">
            <el-switch
                v-model="form.sex"
                active-text="男"
                inactive-text="女"
                :active-value="true"
                :inactive-value="false"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-switch
                v-model="form.status"
                active-text="可用"
                inactive-text="不可用"
                :active-value="false"
                :inactive-value="true"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" placeholder="请输入备注"/>
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
  </el-dialog>

</template>

<script setup>
import {onMounted, ref} from 'vue'
// 导入接口
import {removeAdmin, saveAdmin, searchAdminById, searchAdminList, updateAdmin, uploadAvatar} from '@/api/admin/index.js'
import {useAdminStore} from "@/stores/admin.js";
import {useMenuStore} from "@/stores/menu.js";
import { storeToRefs } from 'pinia';
const adminStore = useAdminStore();
let { adminId } = storeToRefs(adminStore);
const menuStore = useMenuStore();
const { buttonPermissions } = storeToRefs(menuStore)

const formRef = ref(null);
const uploadRef = ref(null);
// 初始化按钮显示状态为false
const showUploadAvatar = ref(false);
const showSave = ref(false);
const showUpdate = ref(false);
const showDelete = ref(false);

let total = ref(0)

let adminFormShow = ref(false);
let isEditMode = ref(false);
let adminTitle = ref("");

let queryForm = ref({
    username: undefined,
    nickname: undefined,
    email: undefined,
    mobile: undefined,
    pageNum: 1,
    pageSize: 10
})

// 初始化表单值
const initialFormValue = {
  adminId: undefined,
  avatar: undefined,
  username: undefined,
  nickname: undefined,
  password: undefined,
  confirmPassword: undefined,
  email: undefined,
  mobile: undefined,
  sex: false,
  sort: 0,
  status: false,
  remark: undefined,
};

// 新增和修改数据时的表单数据
let form = ref({...initialFormValue});

// 表单验证规则
let rules = ref({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] }
  ],
  mobile: [
    { pattern: /^[1][3,4,5,7,8][0-9]{9}$/, message: '请输入有效的手机号码', trigger: ['blur', 'change'] }
  ],
  sex: [ // 如果需要必填，根据具体逻辑添加 required 规则
    { type: 'boolean', message: '请选择性别', trigger: 'change' }
  ],
  status: [
    { type: 'boolean', message: '请选择状态', trigger: 'change' }
  ],
  remark: [
    { max: 100, message: '备注不能超过100个字符', trigger: 'blur' }
  ]
});

function validatePassword(rule, value, callback) {
  const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d!@#$%^&*()_+}{":?><,.]{7,13}$/;
  if (!value || passwordRegex.test(value)) {
    callback();
  } else {
    callback(new Error('密码必须由字母和数字组成，可以包含不是特殊符号的符号，长度在7到13位之间'));
  }
}
function validateConfirmPassword(rule, value, callback) {
  if (value === '') {
    callback(new Error('请确认密码'));
  } else if (value !== form.value.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
}

let defaultAvatar = "/defaultimg/default_avatar.png";

let adminList = ref([])
onMounted(() => {
    handleSearchAdminList();
    showUploadAvatar.value = buttonPermissions.value.includes('admin:admin:uploadAvatar');
    showSave.value = buttonPermissions.value.includes('admin:admin:save');
    showUpdate.value = buttonPermissions.value.includes('admin:admin:update');
    showDelete.value = buttonPermissions.value.includes('admin:admin:delete');
  console.log("permissionButtonList: ",buttonPermissions.value)
  console.log("showSave.value: ",showSave.value);
})

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

// 查询所有管理员
function handleSearchAdminList() {
  searchAdminList(queryForm.value).then(res => {
    if (res.data.code === 200) {
      adminList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}
// 重置
function handleRest() {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = 10;
  queryForm.value.username = undefined;
  queryForm.value.nickname = undefined;
  queryForm.value.email = undefined;
  queryForm.value.mobile = undefined;
  searchAdminList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      adminList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}
// 搜索
function handleQuery() {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = 10;
  searchAdminList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      adminList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}

function handleSizeChange(sizeNumber) {
  queryForm.value.pageSize = sizeNumber;
  searchAdminList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      adminList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}
// 点击下一页，上一页
function handleCurrentChange(pageNumber) {
  queryForm.value.pageNum = pageNumber;
  searchAdminList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      adminList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}
// 提交表单,根据form.adminId值判断是新增还是修改【有adminId值】
function handleSubmit() {
  // 做数据校验
  formRef.value.validate((valid) => {
    if (valid) {
      if(form.value.adminId) {
        // 修改
        updateAdmin(form.value).then(res => {
          if(res.data.code == 200) {
            // 关闭窗口
            isEditMode.value = false;
            adminFormShow.value = false;
            // 刷新列表
            handleSearchAdminList();
            // 弹窗提示新增成功
            ElMessage({
              message: '修改管理员成功！',
              type: 'success',
            });
            handleClose();
          }
        })
      }else {
        // 新增，调用新增接口
        saveAdmin(form.value).then(res => {
          if(res.data.code == 200) {
            // 关闭窗口
            adminFormShow.value = false;
            // 刷新列表
            handleSearchAdminList();
            // 弹窗提示新增成功
            ElMessage({
              message: '新增管理员成功！',
              type: 'success',
            });
            handleClose();
          }
        }).catch((error) => {
          ElMessageBox.alert(error, "警告", {
            showConfirmButton: false,
            closeOnClickModal: true
          });
        })
      }
    } else {
      // 表单验证失败
      ElMessage.error('表单验证失败，请检查输入')
    }
  })
}

// 新增按钮，弹出表单
function handleAdd() {
  isEditMode.value = false;
  adminFormShow.value = true;
  adminTitle.value = "新增管理员";
}

// 修改按钮，根据adminId查询对应的数据，弹出表单，回显数据
function handleEdit(adminId) {
  // 先查询数据，再弹窗
  searchAdminById(adminId).then(res => {
    if(res.data.code == 200) {
      // 保障后端返回的字段名和前端字段名相同，可以一一赋值
      form.value = res.data.data;
      isEditMode.value = true;
      adminFormShow.value = true;
      adminTitle.value = "修改管理员";
    } else {
      ElMessage({
        message: '数据查询失败！',
        type: 'error',
      })
    }
  })

}
// 删除按钮，弹出是否要删除数据，确定就删除，取消就不删除
function handleRemove(adminId, username) {
  // adminId其实是点击操作下的删除按钮时才会有数据
  ElMessageBox.confirm(
      `确定要删除【${username}】数据吗?`,
      '删除管理员',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error',
      }
  ).then(() => {
        removeAdmin(adminId).then(res => {
          if(res.data.code == 200) {
            ElMessage({
              message: '删除成功',
              type: 'success',
            })
            // 刷新列表
            handleSearchAdminList();
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

// 关闭弹窗
function handleClose() {
  adminFormShow.value = false;
  form.value = {...initialFormValue};
  // 清空表单验证状态和错误信息
  formRef.value?.resetFields();
}

// 头像上传前的验证
function beforeAvatarUpload(file) {
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

async function uploadImage(file) {
  const formData = new FormData();
  // 添加管理员ID到 FormData 中
  formData.append('adminId', form.value.adminId); // 确保传递管理员ID
  // 添加文件到 FormData 中
  formData.append('file', file.file);
  try {
    const response = await uploadAvatar(formData);
    if (response.status === 200) {
      if (adminId.value == form.value.adminId)
        adminStore.setAdminAvatar(response.data.data);
      // 设置成功上传后的图片 URL 到表单中
      form.value.avatar = response.data.data;
      handleSearchAdminList();
    } else {
      throw new Error(response.statusText); // 抛出错误以触发 catch 分支
    }
  } catch (error) {
    console.error('上传图片失败:', error.message);
  }
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