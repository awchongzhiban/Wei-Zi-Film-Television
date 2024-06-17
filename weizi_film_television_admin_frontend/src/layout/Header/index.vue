<template>
    <div class="header_container">
        <!-- 面包屑 -->
        <div class="header_left">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item v-for="(item,index) in breadcrumbList" :key="index">{{ item }}</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <!-- 右侧用户名、头像 -->
        <div class="header_right">
            <!-- 头像 -->
            <div class="avatar">
              <el-avatar :size="40" :src="getAvatarUrl(avatar)" />
            </div>
            <!-- 用户名 -->
            <div>
                <el-dropdown>
                    <span class="el-dropdown-link">
                    {{ nickname }}
                    <el-icon class="el-icon--right">
                        <arrow-down />
                    </el-icon>
                    </span>
                    <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item>个人中心</el-dropdown-item>
                        <el-dropdown-item @click="handleLogout" divided>退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                    </template>
                </el-dropdown>

            </div>
        </div>
    </div>
</template>

<script setup>
    import { useAdminStore } from '@/stores/admin.js'
    import { storeToRefs } from 'pinia';
    import {ArrowDown} from "@element-plus/icons-vue";
    const adminStore = useAdminStore();
    let { avatar,nickname } = storeToRefs(adminStore)
    import { adminMenuStore } from '@/stores/menu.js'
    import router from "@/router/index.js";
    const menuStore = adminMenuStore();
    const { breadcrumbList } = storeToRefs(menuStore)
    // 导入logout方法
    import { adminLogout } from '@/api/auth/index.js';
    import {removeToken} from "@/utils/token/index.js";

    function getAvatarUrl(base64String) {
      if (!base64String) return "/defaultimg/default_avatar.png";
      return base64String; // 将 base64 字符串拼接为图片 URL
    }

    // 声明 登出 方法
    function handleLogout() {
      // 调用login方法
      adminLogout().then((res) => {
        // 判断是否成功
        if (res.data.code === 200) {
          removeToken("weiziToken")
          // 清除 menuStore 中的信息 TODO 后期这里也要改成localStorage.removeItem
          menuStore.setMenuList([]);
          menuStore.setButtonPermissions([]);
          // 清除 adminStore 中的信息
          localStorage.removeItem("adminInfo");
          router.push("/");
        }
      })
    }
</script>

<style lang="scss" scoped>
.header_container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-sizing: border-box;
    height: 100%;

    .header_right {
        display: flex;
        justify-content: space-between;
        align-items: center;
        box-sizing: border-box;
        .avatar {
            cursor: pointer;
            margin-right: 12px;
        }
        .el-dropdown-link {
            cursor: pointer;
            display: flex;
            align-items: center;
            border: none;
            outline: none; // 取出边框
        }
    }
}
</style>