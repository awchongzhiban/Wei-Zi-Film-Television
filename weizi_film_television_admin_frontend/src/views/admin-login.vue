<template>
  <!-- 根div -->
  <div class="login_container">
    <h3 class="title">微子视界后台管理平台</h3>
    <div class="content-wrapper">
      <!-- 登陆表单 -->
      <div class="login_form">
        <div class="title">登录</div>
        <el-form ref="formRef" :model="loginForm">
          <!-- 用户名 -->
          <el-form-item>
            <el-input v-model="loginForm.account" placeholder="请输入账号">
              <template #prefix>
                <el-icon class="el-input__icon"><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <!-- 密码 -->
          <el-form-item>
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码">
              <template #prefix>
                <el-icon class="el-input__icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <!-- 按钮 -->
            <el-button style="width: 100%;" type="primary" @click="handleLogin">登陆</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>

</template>

<script setup>
// 导入 ref
import { ref } from  'vue';
// 导入login方法
import { adminLogin } from '@/api/auth/index.js';
import {searchSelfInfo, searchSelfRouter} from '@/api/admin/index.js';
// 引入token的js
import { setToken } from '@/utils/token/index.js';
// 引入store
import { adminMenuStore } from '@/stores/menu.js'
import { useAdminStore } from '@/stores/admin.js'
import { useRouter } from 'vue-router';
import {ChromeFilled, ElemeFilled, User, WindPower} from "@element-plus/icons-vue";
// 引入vue-router


// 构建store
const menuStore = adminMenuStore();
const adminStore = useAdminStore();
const router = useRouter();
// 声明表单绑定值
const loginForm = ref({
  account: undefined,
  password: undefined,
  rememberMe: undefined
})

// 声明 登录 方法
function handleLogin() {
  // 调用login方法
  adminLogin(loginForm.value).then((res) => {
    // 判断是否成功
    if (res.data.code === 200) {
      // 将token存储到localStorage中
      setToken("weiziToken", res.data.token);
      searchSelfRouter().then(res => {
        if (res.data.code === 200) {
          // 将路由信息存储到pinia中
          menuStore.setMenuList(res.data.data)
          menuStore.setButtonPermissions(res.data.data)
          // 设置动态路由
          // 跳转页面  /index
          // 1、渲染动态路由【在路由守卫上渲染】
          // 2、开发项目主页面 【左侧导航、头部、主体部分】
          menuStore.updateBreadcrumbList(["首页"])
          menuStore.setCurrentRoute("/admin-index")
          router.push("/admin-index")
        }
      })
      // 查询个人信息
      searchSelfInfo().then(res => {
        // 存到pinia中
        if(res.data.code === 200) {
          adminStore.setAdminInfo(res.data.data);
        }
      })
    }
  })
}

</script>

<style lang="scss" scoped>
.login_container {
  background-image: url('../assets/bgimg/login.jpg');
  background-size:100% 100%;
  display: flex;
  min-height: 100vh;
  max-height: 1000vh;
  background-attachment: fixed;
  flex-direction: column; /* 使内容沿垂直方向排列 */
  align-items: center; /* 水平居中 */

  /* 添加一个内部容器来单独控制表单的居中 */
  .content-wrapper {
    display: flex;
    flex-direction: column;
    width: 30%;
    padding: 20px;
    align-items: center; /* 使表单在该容器内水平居中 */
    background-color: #fff;
    border-radius: 16px; /* 设置圆角大小，按需调整 */
    box-shadow: 0px 0px 20px #FFFFFF;
    margin-bottom: 8%;
    .title {
      font-size: 30px;
      text-align: center; /* 使标题文本居中 */
    }
  }

  .title {
    font-size: 60px;
    text-align: center; /* 使标题文本居中 */
    text-shadow: 0 0 8px rgb(255, 255, 255);
    margin-bottom: 8%;
  }
}


</style>