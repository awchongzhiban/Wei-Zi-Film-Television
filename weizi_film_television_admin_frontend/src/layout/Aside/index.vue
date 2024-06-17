<template>
  <el-menu
      background-color="#304157"
      text-color="#b1becd"
      active-text-color="#336cab"
      style="border-right: none;"
      :default-active="defaultActive"
  >
    <!-- 硬编码的“首页”菜单 -->
    <el-menu-item index="/admin-index" @click="handleRouter({ path: '/admin-index', menuName: '首页', icon: 'home' })">
      <el-icon><House /></el-icon>
      <span>首页</span>
    </el-menu-item>
    <!-- 遍历子元素 -->
    <template v-for="menu in menuList" :key="menu.path">
      <!-- 判断是否有子菜单 -->
      <el-sub-menu v-if="hasChildren(menu) && menu.menuType === 'DIRECTORY'" :index="menu.path">
        <template #title>
          <!-- 添加icon -->
          <el-icon v-if="menu.icon"><component :is="$icon[menu.icon]" /></el-icon>
          <span style="margin-left: 5px">{{ menu.menuName }}</span>
        </template>
        <!-- 渲染子菜单 -->
        <el-menu-item-group>
          <el-menu-item
              v-for="children in menu.children.filter(child => child?.menuType !== 'BUTTON')"
              :key="children.path"
              :index="children.path"
              @click="handleRouter(children)"
          >
            <el-icon v-if="children.icon"><component :is="$icon[children.icon]" /></el-icon>
            <span style="margin-left: 5px">{{ children.menuName }}</span>
          </el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>
      <!-- 没有子菜单 -->
      <el-menu-item v-else-if="menu.menuType !== 'BUTTON'" :index="menu.path" @click="handleRouter(menu)">
        <!-- 添加icon -->
        <el-icon v-if="menu.icon"><component :is="$icon[menu.icon]" /></el-icon>
        <span style="margin-left: 5px">{{ menu.menuName }}</span>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script setup>
import { adminMenuStore } from '@/stores/menu.js'
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router';

const router = useRouter()
const menuStore = adminMenuStore();
const { menuList } = storeToRefs(menuStore)

function hasChildren(menu) {
  return menu.children && menu.children.length > 0;
}

function handleRouter(menu) {
  router.push(menu.path);
}

// 获取默认选中的菜单项
const defaultActive = menuStore.currentRoute;
</script>

<style lang="scss" scoped>
/* 样式 */
</style>
