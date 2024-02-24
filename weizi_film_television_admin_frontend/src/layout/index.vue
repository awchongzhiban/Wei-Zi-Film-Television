<template>
    <div class="common-layout">
    <el-container>
      <el-aside class="aside" :style="{ height: height + 'px',minHeight: minHeight + 'px' }">
        <Aside/>
      </el-aside>
      <el-container>
        <el-header style="background-color: #f0f0f0;">
            <Header/>
        </el-header>
        <el-main :style="{ height: height - 60 + 'px',minHeight: minHeight - 60 + 'px'}">
            <Main/>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
    // 导入左侧导航组件
    import Aside from './Aside/index.vue'
    import Header from './Header/index.vue'
    import Main from './Main/index.vue'
    import { ref,onMounted } from 'vue';
    const height = ref(0)
    const minHeight = ref(0)

    onMounted(() => {
        getHeight();
        // 监听页面缩放事件
        minHeight.value = document.documentElement.clientHeight;
        window.addEventListener('resize',getHeight)
    })
    // 获取文档高度
    function getHeight() {
        height.value = document.documentElement.clientHeight;
    }
</script>

<style lang="scss" scoped>
.aside {
    background-color: #2f4156;
}
</style>