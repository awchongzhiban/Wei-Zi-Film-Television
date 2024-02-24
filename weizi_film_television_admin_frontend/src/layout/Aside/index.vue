<template>
  <div>
    <el-menu
        background-color="#304157"
        text-color="#b1becd"
        active-text-color="#336cab"
        style="border-right: none;"
    >
      <!-- 遍历子元素 -->
      <template v-for="menu in menuList" :key="menu.path">
        <!-- 判断是否有子菜单 -->
        <el-sub-menu v-if="hasChildren(menu) && menu.menuType !== 'BUTTON'" :index="menu.path">
          <template #title>
            <!-- <Edit style="width: 1em; height: 1em; margin-right: 8px" /> -->
            <!-- <el-icon><Setting /></el-icon> -->
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
              <!-- <Edit style="width: 1em; height: 1em; margin-right: 8px" /> -->
              <!-- <el-icon><Setting /></el-icon> -->
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
          <!-- <Edit style="width: 1em; height: 1em; margin-right: 8px" /> -->
          <!-- <el-icon><Setting /></el-icon> -->
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
  // 向tabList中添加数据，已经添加过的就不需要添加了
  // 数据结构：{title:'首页',path:'/index'}
  let hasNode = menuStore.tabList.filter(item => item.path === menu.path)
  // 没有
  if (hasNode == null || hasNode.length == 0) {
    let data = {title: menu.menuName, path: menu.path};
    menuStore.setTabList(data)
  }
  router.push(menu.path);
}
</script>

<style lang="scss" scoped>

</style>