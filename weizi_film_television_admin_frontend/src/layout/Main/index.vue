<template>
<!--  <el-tabs
    v-model="activeTab"
    type="card"
    closable
    class="demo-tabs"
    @tab-change="gotoTab"
    @tab-remove="closeTab"
  >
    <el-tab-pane
      v-for="item in tabList"
      :key="item.path"
      :label="item.title"
      :name="item.path"
      :closable="!item.isClose"
    >
    <RouterView/>
    </el-tab-pane>
  </el-tabs>-->

  <RouterView/>
</template>

<script setup>
    import {RouterView} from 'vue-router'
    // 从stores中获取tab数据
    import { useMenuStore } from '@/stores/menu.js'
    import { storeToRefs } from 'pinia';
    import { useRouter } from 'vue-router';
    const router = useRouter()
    const menuStore = useMenuStore();
    let { tabList,activeTab } = storeToRefs(menuStore)
    
    // 选中
    function gotoTab(path) {
      // 修改activeTab
      menuStore.setActive(path);
      // 路由页面
      router.push(path);
    }
    // 移除
    function closeTab(path) {
      // 移除的是否是当前激活的tab,激活首页
      if(path == menuStore.activeTab) {
        menuStore.setActive("/admin-index");
        router.push("/admin-index");
      }
      menuStore.delTabList(path);
    }
</script>

<style lang="scss" scoped>

</style>