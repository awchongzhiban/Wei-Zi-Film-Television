<template>
  <div>
    <el-menu
        background-color="#304157"
        text-color="#b1becd"
        active-text-color="#336cab"
        style="border-right: none;"
        :default-active="defaultActive"
    >
      <!-- 硬编码的“首页”菜单 -->
      <el-menu-item index="/admin-index" @click="handleRouter({ path: '/admin-index', menuName: '首页', icon: 'home' })">
        <svg-icon
            slot="prefix"
            name="home"
            width="18px"
            height="18px"
        />
        <span>首页</span>
      </el-menu-item>
      <!-- 遍历子元素 -->
      <template v-for="menu in menuList" :key="menu.path">
        <!-- 判断是否有子菜单 -->
        <el-sub-menu v-if="hasChildren(menu) && menu.menuType !== 'BUTTON'" :index="menu.path">
          <template #title>
            <!-- 添加icon -->
            <svg-icon
                v-if="menu.icon"
                slot="prefix"
                :name="menu.icon"
                width="18px"
                height="18px"
            />
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
              <svg-icon
                  v-if="children.icon"
                  slot="prefix"
                  :name="children.icon"
                  width="18px"
                  height="18px"
              />
              <span style="margin-left: 5px">{{ children.menuName }}</span>
            </el-menu-item>
          </el-menu-item-group>
        </el-sub-menu>

        <!-- 没有子菜单 -->
        <el-menu-item v-else-if="menu.menuType !== 'BUTTON'" :index="menu.path" @click="handleRouter(menu)">
          <svg-icon
              v-if="menu.icon"
              slot="prefix"
              :name="menu.icon"
              width="18px"
              height="18px"
          />
          <span style="margin-left: 5px">{{ menu.menuName }}</span>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script setup>
import { useMenuStore } from '@/stores/menu.js'
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router';

const router = useRouter()
const menuStore = useMenuStore();
const { menuList } = storeToRefs(menuStore)

function hasChildren(menu) {
  return menu.children && menu.children.length > 0;
}

function handleRouter(menu) {
  router.push(menu.path);
}

// 在路由加载时更新当前路由信息
router.beforeEach((to, from, next) => {
  const currentMenu = menuList.value.find(menu => menu.path === to.path);
  if (currentMenu) {
    menuStore.setCurrentRoute(currentMenu);
  }
  next();
});

// 获取默认选中的菜单项
const defaultActive = menuStore.currentRoute;
</script>

<style lang="scss" scoped>
/* 样式 */
</style>
