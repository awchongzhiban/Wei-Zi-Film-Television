<script setup lang="ts">
import { ref } from "vue";
import { useMenu } from "./utils/hook";
import { transformI18n } from "@/plugins/i18n";
import { PureTableBar } from "@/components/RePureTableBar";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { OperationType } from "@/utils/enum";

import Delete from "@iconify-icons/ep/delete";
import EditPen from "@iconify-icons/ep/edit-pen";
import Refresh from "@iconify-icons/ep/refresh";
import AddFill from "@iconify-icons/ri/add-circle-line";

defineOptions({
  name: "SystemMenu"
});

const formRef = ref();
const tableRef = ref();
const {
  form,
  loading,
  columns,
  dataList,
  onSearch,
  resetForm,
  openDialog,
  handleDelete,
  handleSelectionChange
} = useMenu();
</script>

<template>
  <div class="main">
    <el-form
      ref="formRef"
      :inline="true"
      :model="form"
      class="search-form bg-bg_color w-[99/100] pl-8 pt-[12px]"
    >
      <el-form-item :label="$t('module.menuManagement.menuName')" prop="menuName">
        <el-input
          v-model="form.menuName"
          :placeholder="$t('module.menuManagement.menuNamePlaceholder')"
          clearable
          class="!w-[180px]"
        />
      </el-form-item>
      <el-form-item :label="$t('module.menuManagement.perms')" prop="perms">
        <el-input
          v-model="form.perms"
          :placeholder="$t('module.menuManagement.permsPlaceholder')"
          clearable
          class="!w-[180px]"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          :icon="useRenderIcon('ri:search-line')"
          :loading="loading"
          @click="onSearch"
        >
          {{ $t('buttons.hssearch') }}
        </el-button>
        <el-button :icon="useRenderIcon(Refresh)" @click="resetForm(formRef)">
          {{ $t('buttons.hsreset') }}
        </el-button>
      </el-form-item>
    </el-form>

    <PureTableBar
      :title="$t('menus.menuManagement')"
      :columns="columns"
      :isExpandAll="false"
      :tableRef="tableRef?.getTableRef()"
      @refresh="onSearch"
    >
      <template #buttons>
        <el-button
          type="primary"
          :icon="useRenderIcon(AddFill)"
          @click="openDialog(OperationType.ADD)"
        >
          {{ $t('buttons.hsadd') }}
        </el-button>
      </template>
      <template v-slot="{ size, dynamicColumns }">
        <pure-table
          ref="tableRef"
          adaptive
          :adaptiveConfig="{ offsetBottom: 45 }"
          align-whole="center"
          row-key="menuId"
          showOverflowTooltip
          table-layout="auto"
          :loading="loading"
          :size="size"
          :data="dataList"
          :columns="dynamicColumns"
          :header-cell-style="{
            background: 'var(--el-fill-color-light)',
            color: 'var(--el-text-color-primary)'
          }"
          @selection-change="handleSelectionChange"
        >
          <template #operation="{ row }">
            <el-button
              class="reset-margin"
              link
              type="primary"
              :size="size"
              :icon="useRenderIcon(EditPen)"
              @click="openDialog(OperationType.EDIT, row)"
            >
              {{ $t('buttons.hsedit') }}
            </el-button>
            <el-button
              v-show="row.menuType !== 'BUTTON'"
              class="reset-margin"
              link
              type="primary"
              :size="size"
              :icon="useRenderIcon(AddFill)"
              @click="openDialog(OperationType.ADD, { parentId: row.menuId } as any)"
            >
              {{ $t('buttons.hsadd') }}
            </el-button>
            <el-popconfirm
              :title="`${$t('module.menuManagement.deleteConfirm', {
               menuName: transformI18n(row.menuName),
               additionalInfo: row?.children?.length > 0 ? transformI18n('module.menuManagement.deleteConfirmAdditionalInfo') : ''
              })}`"
              width="220"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button
                  class="reset-margin"
                  link
                  type="primary"
                  :size="size"
                  :icon="useRenderIcon(Delete)"
                >
                  {{ $t('buttons.hsdelete') }}
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>

<style lang="scss" scoped>
:deep(.el-table__inner-wrapper::before) {
  height: 0;
}

.main-content {
  margin: 24px 24px 0 !important;
}

.search-form {
  :deep(.el-form-item) {
    margin-bottom: 12px;
  }
}
</style>
