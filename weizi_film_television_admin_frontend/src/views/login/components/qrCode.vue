<script setup lang="ts">
import { useI18n } from "vue-i18n";
import Motion from "../utils/motion";
import ReQrcode from "@/components/ReQrcode";
import { useAdminStoreHook } from "@/store/modules/admin";

const { t } = useI18n();
</script>

<template>
  <Motion class="-mt-2 -mb-2"> <ReQrcode :text="t('login.test')" /> </Motion>
  <Motion :delay="100">
    <el-divider>
      <p class="text-gray-500 text-xs">{{ t("login.tip") }}</p>
    </el-divider>
  </Motion>
  <Motion :delay="150">
    <el-button
      class="w-full mt-4"
      @click="useAdminStoreHook().SET_CURRENTPAGE(0)"
    >
      {{ t("login.back") }}
    </el-button>
  </Motion>
</template>
