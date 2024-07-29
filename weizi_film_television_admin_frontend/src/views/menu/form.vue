<script setup lang="ts">
import { ref } from "vue";
import ReCol from "@/components/ReCol";
import { formRules } from "./utils/rule";
import { FormProps } from "./utils/types";
import { transformI18n } from "@/plugins/i18n";
import { IconSelect } from "@/components/ReIcon";
import Segmented from "@/components/ReSegmented";
import {
  menuTypeOptions,
  statusOptions,
} from "./utils/enums";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    higherMenuOptions: [],
    menuId: 0,
    parentId: 0,
    menuName: "",
    menuType: 0,
    path: "",
    componentPath: "",
    icon: "",
    perms: "",
    status: false,
    sort: 0,
  })
});

const ruleFormRef = ref();
const newFormInline = ref(props.formInline);

function getRef() {
  return ruleFormRef.value;
}

defineExpose({ getRef });
</script>

<template>
  <el-form
    ref="ruleFormRef"
    :model="newFormInline"
    :rules="formRules"
    label-width="82px"
  >
    <el-row :gutter="30">
      <re-col>
        <el-form-item label="菜单类型">
          <Segmented
            v-model="newFormInline.menuType"
            :options="menuTypeOptions"
          />
        </el-form-item>
      </re-col>

      <re-col>
        <el-form-item label="上级菜单">
          <el-cascader
            v-show="newFormInline.menuType !== 2"
            v-model="newFormInline.parentId"
            :options="newFormInline.higherMenuOptions"
            class="w-full"
            :props="{
              value: 'menuId',
              label: 'menuName',
              emitPath: false,
              checkStrictly: true
            }"
            clearable
            filterable
            placeholder="请选择上级菜单"
          >
            <template #default="{ node, data }">
              <span>{{ transformI18n(data.menuName) }}</span>
              <span v-if="!node.isLeaf"> ({{ data.children.length }}) </span>
            </template>
          </el-cascader>
        </el-form-item>
      </re-col>

      <re-col :value="12" :xs="24" :sm="24">
        <el-form-item label="菜单名称" prop="menuName">
          <el-input
            v-model="newFormInline.menuName"
            clearable
            placeholder="请输入菜单名称"
          />
        </el-form-item>
      </re-col>
      <re-col v-if="newFormInline.menuType !== 2" :value="12" :xs="24" :sm="24">
        <el-form-item label="路由路径" prop="path">
          <el-input
            v-model="newFormInline.path"
            clearable
            placeholder="请输入路由路径"
          />
        </el-form-item>
      </re-col>
      <re-col
        v-show="newFormInline.menuType === 1"
        :value="12"
        :xs="24"
        :sm="24"
      >
        <el-form-item label="组件路径">
          <el-input
            v-model="newFormInline.componentPath"
            clearable
            placeholder="请输入组件路径"
          />
        </el-form-item>
      </re-col>

      <re-col :value="12" :xs="24" :sm="24">
        <el-form-item label="菜单排序">
          <el-input-number
            v-model="newFormInline.sort"
            class="!w-full"
            :min="0"
            :max="999999"
            controls-position="right"
          />
        </el-form-item>
      </re-col>

      <re-col
        :value="12"
        :xs="24"
        :sm="24"
      >
        <el-form-item label="图标">
          <IconSelect v-model="newFormInline.icon" class="w-full" />
        </el-form-item>
      </re-col>
      <re-col v-if="newFormInline.menuType !== 'DIRECTORY'" :value="12" :xs="24" :sm="24">
        <!-- 权限设置 -->
        <el-form-item label="权限标识" prop="perms">
          <el-input
            v-model="newFormInline.perms"
            clearable
            placeholder="请输入权限标识"
          />
        </el-form-item>
      </re-col>

      <re-col
        :value="12"
        :xs="24"
        :sm="24"
      >
        <el-form-item label="状态">
          <Segmented
            :modelValue="newFormInline.status"
            :options="statusOptions"
            @change="
              ({ option: { value } }) => {
                newFormInline.status = value;
              }
            "
          />
        </el-form-item>
      </re-col>
    </el-row>
  </el-form>
</template>
