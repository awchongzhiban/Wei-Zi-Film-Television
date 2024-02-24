<template>
  <!-- 根div -->
  <div class="login_container">
    <!-- 登陆表单 -->
    <div class="login_form">
      <h3 class="title">微子视界管理平台</h3>
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
        <!-- 记住我和忘记密码 -->
        <div class="rememberMe">
          <el-checkbox v-model="loginForm.rememberMe" label="记住我" size="large" />
          <!-- 忘记密码 -->
          <el-text class="forgetpass mx-1" type="primary">忘记密码?</el-text>
        </div>
        <!-- 分割线 -->
        <el-divider>其他登录方式</el-divider>

        <!-- 其他的登录方式 -->
        <div class="other_login">
          <el-icon class="other_login_item"><ChromeFilled /></el-icon>
          <el-icon class="other_login_item"><ElemeFilled /></el-icon>
          <el-icon class="other_login_item"><WindPower /></el-icon>
        </div>
        <el-form-item>
          <!-- 按钮 -->
          <el-button style="width: 100%;" type="primary" @click="handleLogin">登陆</el-button>
        </el-form-item>
      </el-form>

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
import { useMenuStore } from '@/stores/menu.js'
import { useAdminStore } from '@/stores/admin.js'
import { useRouter } from 'vue-router';
import {ChromeFilled, ElemeFilled, User, WindPower} from "@element-plus/icons-vue";
// 引入vue-router


// 构建store
const menuStore = useMenuStore();
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
    if (res.data.code == 200) {
      // 将token存储到sessionStorage中
      setToken("weiziToken", res.data.token);
      searchSelfRouter().then(res => {
        if (res.data.code == 200) {
          // 将路由信息存储到pinia中
          menuStore.setMenuList(res.data.data)
          // 设置动态路由
          // 跳转页面  /index
          // 1、渲染动态路由【在路由守卫上渲染】
          // 2、开发项目主页面 【左侧导航、头部、主体部分】
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
};

</script>

<style lang="scss" scoped>
.login_container {
  // 背景图
  background-image: url('../assets/bgimg/login.jpg');
  background-size: 100%;
  height: 100vh;
  display: flex;
  justify-content: flex-end;

  .login_form {
    display: flex;
    justify-content: center;
    align-items: center;
    //设置换行
    flex-direction: column;
    width: 50%;
    background-color: #fff;

    .title {
      margin-bottom: 20px;
    }

    .rememberMe {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .forgetpass {
        cursor: pointer;
      }
    }

    // 其他登录
    .other_login {
      display: flex;
      justify-content: center;
      margin-bottom: 20px;

      .other_login_item {
        margin-right: 60px;
        cursor: pointer;
      }
    }

  }
}

// 设置form的宽度
.el-form {
  width: 60%;
}

.el-form-item {
  width: 100%;
}

</style>