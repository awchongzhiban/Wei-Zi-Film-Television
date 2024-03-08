<template>
  <div>
    <el-menu
        background-color="#304157"
        text-color="#b1becd"
        active-text-color="#336cab"
        style="border-right: none;"
        :default-active="defaultActive"
    >
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
// 获取所有的菜单,在pinia中
import { useMenuStore } from '@/stores/menu.js'
// 放到ref里边pinia提供了storeToRefs()方法可以将数据取出放到ref中
import { storeToRefs } from 'pinia';
// 引入vue-router
import { useRouter } from 'vue-router';
const router = useRouter()
const menuStore = useMenuStore();
const { menuList } = storeToRefs(menuStore)
// 判断是否有子菜单
function hasChildren(menu) {
  return menu.children && menu.children.length > 0;
}

// 切换路由
function handleRouter(menu) {
  generateBreadcrumbList(menu);
  router.push(menu.path);
}
// 根据当前路由信息生成面包屑列表
function generateBreadcrumbList(menu) {
  const breadcrumbList = [];
  // 如果 parentId 不为 0，则获取父级菜单名称并加入面包屑列表
  if (menu.parentId !== 0) {
    const parentMenuName = getParentMenuName(menu.parentId);
    breadcrumbList.push(parentMenuName);
  }
  // 递归获取当前菜单项及其父菜单的名称
  const getCurrentAndParentName = (item) => {
    // 检查当前菜单项的名称是否已经在 breadcrumbList 中
    if (!breadcrumbList.includes(item.menuName)) {
      breadcrumbList.push(item.menuName);
    }
    if (item.parentId !== 0) {
      const parentMenuName = getParentMenuName(item.parentId);
      // 检查父级菜单名称是否已经在 breadcrumbList 中
      if (!breadcrumbList.includes(parentMenuName)) {
        breadcrumbList.unshift(parentMenuName);
      }
    }
    if (item.parent) {
      getCurrentAndParentName(item.parent);
    }
  };
  getCurrentAndParentName(menu);
  menuStore.updateBreadcrumbList(breadcrumbList);
}

// 根据 parentId 获取父级菜单的名称
function getParentMenuName(parentId) {
  const parentMenu = menuList.value.find(item => item.menuId === parentId);
  return parentMenu ? parentMenu.menuName : '';
}

// 获取默认选中的菜单项
const defaultActive = getDefaultActiveMenu();

function getDefaultActiveMenu() {
  // 获取当前路由信息
  const currentRoute = router.currentRoute.value;
  // 根据当前路由信息找到对应的菜单项
  const defaultMenu = menuList.value.find(menu => menu.path === currentRoute.path);
  // 如果找到了菜单项，则返回其 index 属性值
  if (defaultMenu) {
    return defaultMenu.path;
  }
  // 如果未找到菜单项，则返回空字符串
  return '';
}
</script>

<style lang="scss" scoped>

</style>
