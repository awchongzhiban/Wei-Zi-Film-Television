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
                <el-image :src="avatar" class="avatar_img"/>
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
                        <el-dropdown-item divided>退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                    </template>
                </el-dropdown>

            </div>
        </div>
    </div>
</template>

<script setup>
    import {ref} from 'vue'
    import { useAdminStore } from '@/stores/admin.js'
    import { storeToRefs } from 'pinia';
    import {ArrowDown} from "@element-plus/icons-vue";
    const userStore = useAdminStore();
    let { avatar,nickname } = storeToRefs(userStore)
    const breadcrumbList = ref(['系统管理','用户管理'])
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
            .avatar_img {
                width: 30px;
                height: 30px;
            }
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