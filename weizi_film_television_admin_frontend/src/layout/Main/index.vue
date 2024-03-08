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
</script>

<style lang="scss" scoped>

</style>