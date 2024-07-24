<template>
    <div class="common-layout">
    <el-container :style="{ minHeight: minHeight + 'px', minWidth: '1024px' }">
      <el-header
          :style="headerStyle">
        <Header/>
      </el-header>
      <el-aside
          :style="asideStyle">
        <el-scrollbar>
          <Aside/>
        </el-scrollbar>
      </el-aside>
      <el-main
               style="padding-bottom: 15px; background-color: rgb(16,16,16)">
        <div ref="scrollableContainerRef">
          <Main/>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
// 导入左侧导航组件
import Aside from './Aside/index.vue'
import Header from './Header/index.vue'
import Main from './Main/index.vue'
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { storeToRefs } from 'pinia';
import { useScrollAndLoadStore } from "@/stores/scrollAndLoadStroe.ts";
import { WeiZiResult } from "@/utils/result/WeiZiResult.ts";
const scrollAndLoadStore = useScrollAndLoadStore();
const { isHasCustomMethod, isLoading, isFinish, params, customMethod } = storeToRefs(scrollAndLoadStore);
// 使用ref定义响应式数据
const height = ref(0);
// 最小高度用于监听页面缩放事件
const minHeight = ref(0);
// 使用ref定义响应式数据
const isScrolledPastThreshold = ref(false);
const scrollableContainerRef = ref<HTMLElement | null>(null);

let scrollHandlerTimeout: Timeout | null = null; // 在组件作用域外定义计时器引用

onMounted(() => {
    getHeight();
    // 监听页面缩放事件
    minHeight.value = document.documentElement.clientHeight;
    window.addEventListener('resize',getHeight)
    window.addEventListener('scroll', handleScroll);
})

// 在onUnmounted中移除滚动监听器
onUnmounted(() => {
  window.removeEventListener('resize',getHeight)
  window.removeEventListener('scroll', handleScroll);
});

// 创建计算属性来返回el-header的样式
const headerStyle = computed(() => {
  const opacityValue = isScrolledPastThreshold.value ? 0.9 : 0;
  const overallOpacity = isScrolledPastThreshold.value ? 0.9 : 0.2;
  return {
    position: 'fixed',
    minWidth: '1024px',
    top: '0',
    left: '0',
    right: '0',
    height: '84px',
    backgroundImage: `linear-gradient(0deg, rgba(20,20,20,${opacityValue}) 1%, rgba(20,20,20,${overallOpacity}) 99%)`,
    zIndex: 14,
  };
});

// 创建计算属性来返回el-aside的样式
const asideStyle = computed(() => {
  const opacityValue = isScrolledPastThreshold.value ? 0.9 : 0;
  const overallOpacity = isScrolledPastThreshold.value ? 0.9 : 0.2;
  return {
    position: 'fixed',
    height: height + 'px',
    top: '0',
    bottom: '0',
    left: '0',
    width: '180px',
    backgroundImage: `linear-gradient(-90deg,rgba(20,20,20,${opacityValue}) 1%,rgba(20,20,20,${overallOpacity}) 99%)`,
    zIndex: 13,
    paddingTop: '84px',
    paddingBottom: '15px'
  };
});

// 获取当前窗口高度
function getHeight() {
  height.value = document.documentElement.clientHeight;
}

// 直接定义方法
function handleScroll() {
  const scrollTop = document.querySelector('.poster_mask')?.getBoundingClientRect().top;
  // 监听这个高度是否小于100,如果小于100就直接设置为true
  isScrolledPastThreshold.value = scrollTop ? scrollTop < -200 : true;
  if (scrollHandlerTimeout !== null) {
    clearTimeout(scrollHandlerTimeout); // 清除之前的计时器
  }
  // 设置新的计时器，等待一段时间后执行处理逻辑
  scrollHandlerTimeout = setTimeout(() => {
    if (!scrollableContainerRef.value) return;

    const isAtBottom = scrollableContainerRef.value.scrollTop + scrollableContainerRef.value.clientHeight >= scrollableContainerRef.value.scrollHeight;
    if (isAtBottom && !isLoading.value && !isFinish.value && isHasCustomMethod.value) {
      getResultDataForm();
    }
  }, 250); // 防抖等待时间，可根据需要调整
}

function getResultDataForm(){
  scrollAndLoadStore.setIsLoading(true);
  customMethod.value(params.value)?.then((res: WeiZiResult) => {
    if (res.data.code === 200 && res.data.data) {
      scrollAndLoadStore.setResultData(res.data.data);
      const nextPageNum = (params.value.pageNum || 0) + 1;
      scrollAndLoadStore.setParams({ ...params.value, pageNum: nextPageNum });

    } else if (res.data.data?.length < 10 || !res.data.data) {
      // 判断是否已经加载完所有数据
      scrollAndLoadStore.setIsFinish(true);
    }
  }).finally(() => {
    scrollAndLoadStore.setIsLoading(false);
  });
}

</script>

<style lang="scss" scoped>
.el-main {
  padding: 0;
}
</style>