<template>
  <div class="poster_carousel_div" ref="posterCarouselDivRef">

    <el-carousel
        ref="carousel"
        class="poster_carousel_content"
        indicator-position="none"
        arrow="never"
        :autoplay="isAutoplay"
        @change="handleChangeCarousel"
        height="100vh">
      <el-carousel-item v-for="item in 4" :key="item">
        <div>
{{item}}
        </div>
      </el-carousel-item>
    </el-carousel>
    <div class="poster_mask"></div>
    <div class="poster_carousel_indicator">
      <div  style="background-color: blue; display: inline-block; width: 10px; height: 10px; margin-left: 5px; cursor: pointer;" @mouseover="isAutoplay = false" @mouseleave="isAutoplay = true"
           v-for="index in 4"
           :key="index"
           :class="{ active: carouselCurrentIndex === index }"
           @click="console.log(index,isAutoplay)"
           @mouseover.native="changeIndicator(index)"
      >{{index}}</div>
    </div>
    <div style="padding-left: 180px;padding-top: 10px">
      <div class="filter-labels-wrap">
        <span class="banner-mod-title">重磅热播</span>
        <span class="banner-filter-placeholder"><div class="thin-line"></div></span>
        <span
            @click="changeColor(null)"
            :class="{ active: selected === null }"
            class="tab-item"
            :style="{ color: selected === null ? '#fff' : 'rgba(255, 255, 255, 0.6)' }"
        >
        全部
      </span>
        <span
            v-for="(item, index) in hotTags"
            :key="index"
            @click="changeColor(index)"
            :class="{ active: selected === index }"
            class="tab-item"
            :style="{ color: selected === index ? '#fff' : 'rgba(255, 255, 255, 0.6)' }"
        >
        {{ item.channelName }}
      </span>
      </div>
      <div class="banner-list-wrap horizontal-list">
        <div class="video-banner-item"
             v-for="(item, index) in resultData"
             :key="index"
        >
          <div class="image-container" style="position: relative; display: inline-block;">
            <el-image
                style="width: 235.4px;
                        height: 100%;
                        object-fit: contain;
                        border-radius: 8px;"
                :src="item.mainPoster"
                fit="cover"
            >
              <template #error>
                <img style="width: 100%; height: 100%;" src="@/assets/defaultImage/default_main_poster.jpg" alt=""/>
              </template>
            </el-image>
            <div v-if="item.isVip" class="vip_tag_class">
              VIP
            </div>
            <div class="bottom_right_tag" style="z-index: 11; position: absolute; right: 3px; bottom: 4px;font-size: 12px;
                  padding: 2px 7px;
                  line-height: normal;
                  color: rgb(255, 255, 255);">
              {{ item.movieId }}分
            </div>
            <div class="bottom_right_mask">
            </div>
          </div>
        </div>
      </div>
      <div v-if="isLoading" :element-loading-spinner="loadingSVG" v-loading="isLoading" element-loading-background="rgba(16, 16, 16)" style="height: 50px;">
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";
import { getHotTags, getMovieChannelList } from "@/api/channel/index.ts";
import { WeiZiResult } from "@/utils/result/WeiZiResult.ts";
import { useScrollAndLoadStore } from "@/stores/scrollAndLoadStroe.ts"
import {storeToRefs} from "pinia";
const scrollAndLoadStore = useScrollAndLoadStore();
const { isLoading, params, resultData, customMethod } = storeToRefs(scrollAndLoadStore);
const carousel = ref(); // 轮播图组件的引用
const isAutoplay = ref(true); // 是否自动播放
const carouselCurrentIndex = ref(0); // 用来记录当前轮播图的索引
const hotTags = ref([]); // 用来记录热门标签的数据
const selected = ref<number | null>(null); // 用来记录当前选中的索引
const posterCarouselDivRef = ref<HTMLElement | null>(null); // 获取列表容器的引用
const loadingSVG = `<svg t="1719495916706" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4306" width="50" height="50"><path d="M876.864 782.592c3.264 0 6.272-3.2 6.272-6.656 0-3.456-3.008-6.592-6.272-6.592-3.264 0-6.272 3.2-6.272 6.592 0 3.456 3.008 6.656 6.272 6.656z m-140.544 153.344c2.304 2.432 5.568 3.84 8.768 3.84a12.16 12.16 0 0 0 8.832-3.84 13.76 13.76 0 0 0 0-18.56 12.224 12.224 0 0 0-8.832-3.84 12.16 12.16 0 0 0-8.768 3.84 13.696 13.696 0 0 0 0 18.56zM552.32 1018.24c3.456 3.648 8.32 5.76 13.184 5.76a18.368 18.368 0 0 0 13.184-5.76 20.608 20.608 0 0 0 0-27.968 18.368 18.368 0 0 0-13.184-5.824 18.368 18.368 0 0 0-13.184 5.76 20.608 20.608 0 0 0 0 28.032z m-198.336-5.76c4.608 4.8 11.072 7.68 17.6 7.68a24.448 24.448 0 0 0 17.536-7.68 27.456 27.456 0 0 0 0-37.248 24.448 24.448 0 0 0-17.536-7.68 24.448 24.448 0 0 0-17.6 7.68 27.52 27.52 0 0 0 0 37.184z m-175.68-91.84c5.76 6.08 13.824 9.6 21.952 9.6a30.592 30.592 0 0 0 22.016-9.6 34.368 34.368 0 0 0 0-46.592 30.592 30.592 0 0 0-22.016-9.6 30.592 30.592 0 0 0-21.952 9.6 34.368 34.368 0 0 0 0 46.592z m-121.152-159.36c6.912 7.36 16.64 11.648 26.368 11.648a36.736 36.736 0 0 0 26.432-11.584 41.28 41.28 0 0 0 0-55.936 36.736 36.736 0 0 0-26.432-11.584 36.8 36.8 0 0 0-26.368 11.52 41.28 41.28 0 0 0 0 56zM12.736 564.672a42.88 42.88 0 0 0 30.784 13.44 42.88 42.88 0 0 0 30.784-13.44 48.128 48.128 0 0 0 0-65.216 42.88 42.88 0 0 0-30.72-13.44 42.88 42.88 0 0 0-30.848 13.44 48.128 48.128 0 0 0 0 65.216z m39.808-195.392a48.96 48.96 0 0 0 35.2 15.36 48.96 48.96 0 0 0 35.2-15.36 54.976 54.976 0 0 0 0-74.56 48.96 48.96 0 0 0-35.2-15.424 48.96 48.96 0 0 0-35.2 15.424 54.976 54.976 0 0 0 0 74.56zM168.32 212.48c10.368 11.008 24.96 17.408 39.68 17.408 14.592 0 29.184-6.4 39.552-17.408a61.888 61.888 0 0 0 0-83.84 55.104 55.104 0 0 0-39.616-17.408c-14.656 0-29.248 6.4-39.616 17.408a61.888 61.888 0 0 0 0 83.84zM337.344 124.8c11.52 12.16 27.712 19.264 43.968 19.264 16.256 0 32.448-7.04 43.968-19.264a68.672 68.672 0 0 0 0-93.184 61.248 61.248 0 0 0-43.968-19.264 61.248 61.248 0 0 0-43.968 19.264 68.736 68.736 0 0 0 0 93.184z m189.632-1.088c12.672 13.44 30.528 21.248 48.448 21.248s35.712-7.808 48.384-21.248a75.584 75.584 0 0 0 0-102.464A67.392 67.392 0 0 0 575.36 0c-17.92 0-35.776 7.808-48.448 21.248a75.584 75.584 0 0 0 0 102.464z m173.824 86.592c13.824 14.592 33.28 23.104 52.736 23.104 19.584 0 39.04-8.512 52.8-23.104a82.432 82.432 0 0 0 0-111.744 73.472 73.472 0 0 0-52.8-23.168c-19.52 0-38.912 8.512-52.736 23.168a82.432 82.432 0 0 0 0 111.744z m124.032 158.528c14.976 15.872 36.032 25.088 57.216 25.088 21.12 0 42.24-9.216 57.152-25.088a89.344 89.344 0 0 0 0-121.088 79.616 79.616 0 0 0-57.152-25.088c-21.184 0-42.24 9.216-57.216 25.088a89.344 89.344 0 0 0 0 121.088z m50.432 204.032c16.128 17.088 38.784 27.008 61.632 27.008 22.784 0 45.44-9.92 61.568-27.008a96.256 96.256 0 0 0 0-130.432 85.76 85.76 0 0 0-61.568-27.072c-22.848 0-45.44 9.984-61.632 27.072a96.192 96.192 0 0 0 0 130.432z" fill="#00D1D1" p-id="4307"></path></svg>`
let queryForm = ref({
  pageNum: 1,
  pageSize: 30,
  channelId: null,
})

const changeColor = (index: number|null) => {
  selected.value = index;
  queryForm.value.pageNum = 1;
  // 这里这么写是因为可能会有null的情况，但0也会被判断为false
  queryForm.value.channelId = (index || index == 0) ? hotTags.value[index].channelId ?? null : null;
  // 重置一些数据和设置参数
  scrollAndLoadStore.resetIsLoading();
  scrollAndLoadStore.resetIsFinish();
  scrollAndLoadStore.resetResultData();
  scrollAndLoadStore.setParams(queryForm.value);
};

onMounted(() => {
  scrollAndLoadStore.setParams(queryForm.value);
  scrollAndLoadStore.setCustomMethod(getMovieChannelList);
  getHotTags().then((res: WeiZiResult) => {
    hotTags.value = res.data.data;
  });
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
    scrollAndLoadStore.setIsHasCustomMethod(true);
  });
  // 初始化时调用一次
  adjustMaskPosition();
  // 只添加一次事件监听器
  window.addEventListener('resize', adjustMaskPosition);
});

// 不要忘记在组件卸载时移除事件监听器，以避免内存泄漏
onUnmounted(() => {
  window.removeEventListener('resize', adjustMaskPosition);
});

// 实时根据poster_carousel_content容器高度计算蒙版位置
function adjustMaskPosition() {
  const posterContent = document.querySelector('.poster_carousel_content') as HTMLDivElement;
  const posterMask = document.querySelector('.poster_mask') as HTMLDivElement;
  const posterCarouselIndicator = document.querySelector('.poster_carousel_indicator') as HTMLDivElement;

  // 确保查询到了元素
  if (!posterContent || !posterMask || !posterCarouselIndicator) return;

  // 计算内容区域的高度
  const contentHeight = posterContent.offsetHeight;
  const contentWidth = posterContent.offsetWidth;
  // 设置蒙版的top为自身高度减去蒙版高度，使其位于内容底部上方
  posterMask.style.top = `${contentHeight - 260}px`;
  posterMask.style.width = `${contentWidth}px`;
  posterCarouselIndicator.style.top = `${contentHeight - 260}px`;
  posterCarouselIndicator.style.width = `${contentWidth - 180}px`;
}

// 切换轮播
function handleChangeCarousel(e: number) {
  carouselCurrentIndex.value = e
}
// 通过自定义指示器切换，赋值给轮播图
const changeIndicator = (index: number) => {
  carousel.value.setActiveItem(index)
  carouselCurrentIndex.value = index
}
</script>


<style scoped lang="scss">
.poster_carousel_content{
  background-color: #747bff;
  min-height: 576px;
  height: 100vh
}
.carousel-container {
  position: relative;
}

.custom-indicator {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.el-carousel__item h3 {
  color: #475669;
  opacity: 0.75;
  line-height: 150px;
  margin: 0;
  text-align: center;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}

.custom-indicator div {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #ccc;
  margin: 0 5px;
  cursor: pointer;
}

.custom-indicator div.active {
  background-color: #333;
}
.poster_mask {
  height: 260px; /* 蒙版高度 */
  position: absolute; /* 使用绝对定位 */
  left: 0; /* 左侧对齐 */
  background-image: linear-gradient(180deg, rgba(0, 0, 0, 0), #141414); /* 渐变背景 */
  z-index: 12; /* 确保蒙版在其他内容之上 */
  transition: bottom 0.5s; /* 可选：添加过渡效果，使蒙版出现更平滑 */
}

.poster_carousel_indicator {
  height: 260px; /* 与蒙版相同高度，可按需调整 */
  position: absolute; /* 使用绝对定位 */
  padding-left: 180px;
  z-index: 13; /* 确保指示器在蒙版之上 */
}

.banner-mod-title {
  align-items: center;
  font-family: PingFangSC-Medium;
  font-size: 30px;
  color: #fff;
  letter-spacing: 0;
}

.banner-filter-placeholder {
  font-family: PingFangSC-Medium;
  margin-left: 40px;
}

.thin-line {
  border-left: 1px solid rgba(255, 255, 255, 0.6);
  height: 25px; /* 根据需要调整高度 */
  width: 0; /* 使元素变为细线 */
  display: inline-block; /* 保持与其他内联元素同行显示 */
}

.filter-labels-wrap {
  min-height: 45px;
  position: relative;
  width: 100%;
  min-width: 738px;
  justify-content: space-between;
  align-items: center;
  margin: 0 24px 15px 0;
}

.filter-labels-wrap .tab-item {
  margin-left: 40px;
  margin-top: 20px;
  font-family: PingFang SC,serif;
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 22px;
  text-align: center;
  color: rgba(255, 255, 255, .6);
  cursor: pointer;
  display: inline-block;
}

.filter-labels-wrap .tab-item:hover,
.filter-labels-wrap .tab-item.active {
  color: #fff;
}

.video-banner-module .horizontal-list {
  max-height: calc((var(--banner-card-height) + 40px + 52px)* 2);
  min-height: 162px;
}

.video-banner-module .banner-list-wrap {
  display: flex;
  width: 100%;
  flex-wrap: wrap;
  overflow: hidden;
}

.banner-list-wrap.horizontal-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(226px, 1fr)); // 保持网格自适应，但可以考虑是否需要固定列宽
  grid-gap: 10px;
}

.video-banner-item {
  position: relative;
  margin-bottom: 40px;
  margin-right: 20px;
  flex: none;
  border-radius: 8px;
  width: 235.4px;
  height: 132.41px;
}

.vip_tag_class {
  color: rgb(255, 255, 255); 
  background-color: rgb(255, 101, 15); 
  position: absolute; 
  top: 0; 
  right: 0; 
  padding: 2px 6px;
  border-top-right-radius: 8px;
  border-bottom-left-radius: 8px;
}

.bottom_right_mask{
  position: absolute;
  height: 48px;
  bottom: 0;
  background: linear-gradient(360deg, rgba(0, 0, 0, .399257) 2.29%, rgba(0, 0, 0, .0001) 99.71%);
  z-index: 10;
  width: 100%;
  border-bottom-right-radius: 8px;
  border-bottom-left-radius: 8px;
}

.image-container {
  width: 100%;
  height: 100%;
  position: relative;
}
</style>

