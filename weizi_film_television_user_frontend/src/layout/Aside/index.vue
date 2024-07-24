<template>
  <el-menu
      background-color="rgba(0, 0, 0, 0)"
      text-color="#ffffff99"
      active-text-color="#00D1D1"
      style="border-right: none; font-family: 'PingFang SC',sans-serif;"
      :default-active="defaultActive"
  >
    <el-menu-item index="/channel/choice">
      <el-icon><House /></el-icon>
      <span style="font-size: 16px;">首页</span>
    </el-menu-item>
    <!-- 遍历子元素 -->
    <template v-for="channel in channelList" :key="channel.path">
      <el-menu-item index="/channel/{{channel.path}}">
        <!-- 添加icon -->
        <el-icon v-if="channel.icon"><component :is="$icon[channel.icon]" /></el-icon>
        <span style="font-size: 16px;margin-left: 5px">{{ channel.channelName }}</span>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script setup lang="ts">
import { getChannelList } from "@/api/channel/index.ts";
import { onMounted, ref } from "vue";
import { WeiZiResult } from "@/utils/result/WeiZiResult.ts";
import { useRoute } from 'vue-router';

const route = useRoute();
// 获取默认选中的菜单项
const defaultActive = route.path;
let channelList = ref([]);

onMounted(() => {
  getChannelList().then((res: WeiZiResult) => {
    if (res.data.code === 200) {
      channelList.value = res.data.data;
    } else {
      ElMessage.error("获取频道列表失败请刷新页面")
    }
  })
});

</script>

<style lang="scss" scoped>
/* 未选中的菜单项在鼠标悬停时的样式 */
.el-menu:not(.el-menu--collapse) .el-menu-item:not(.is-active):hover,
.el-menu:not(.el-menu--collapse) .el-submenu__title:not(.is-active):hover {
  background-color: transparent !important; /* 保持背景色不变 */
  color: #FFFFFF !important; /* 鼠标悬停时字体变为白色 */
}

/* 确保选中项在任何情况下颜色不变 */
.el-menu:not(.el-menu--collapse) .el-menu-item.is-active,
.el-menu:not(.el-menu--collapse) .el-submenu__title.is-active {
  background-color: transparent !important; /* 保持背景色不变 */
  font-size: 16px;
  font-family: 'PingFang SC',sans-serif;
}

.custom-content {
  /* 在这里定义你的自定义样式 */
  background-color: transparent; /* 举例：自定义背景色 */
  color: white; /* 举例：自定义文字颜色 */
  border-radius: 4px;
}
</style>
