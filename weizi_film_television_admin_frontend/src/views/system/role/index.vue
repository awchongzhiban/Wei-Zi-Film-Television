<template>
  <!-- 头部搜索框 -->
  <el-form :model="queryForm" :inline="true" label-position="top">
    <el-row>
      <el-col :span="12">
        <el-form-item label="角色标识：">
          <el-input v-model="queryForm.roleLabel" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="角色：">
          <el-input v-model="queryForm.roleName" />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <el-form-item label="功能：">
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button type="warning" @click="handleRest">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
  <!-- 列表 -->
  <el-table :data="roleList" style="width: 100%" row-key="roleId" border default-expand-all @selection-change="handleSelectionChange">
    <el-table-column fixed prop="roleLabel" label="角色标识" width="180" />
    <el-table-column prop="roleName" label="角色名称" width="180" />
    <el-table-column prop="status" label="状态" width="80">
      <template #default="{ row }">
        <el-tag v-if="!row.status" type="success">启用</el-tag>
        <el-tag v-else type="danger">禁用</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="remark" label="备注" width="200" />
    <el-table-column prop="createTime" label="创建时间" width="200"/>
    <el-table-column prop="updateTime" label="修改时间" width="200"/>
    <el-table-column fixed="right" label="操作" width="200">
      <template #default="scope">
        <el-button link type="success" size="small" @click="handleEdit(scope.row.roleId)">修改</el-button>
        <el-button link type="danger" size="small" @click="handleRemove(scope.row.roleId, scope.row.roleName)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
  <!-- 分页 -->
  <div class="pagination_container">
    <el-pagination v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize"
                   :page-sizes="[10, 20, 30, 40, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total"
                   @size-change="handleSizeChange" @current-change="handleCurrentChange" />
  </div>

  <!-- 新增和修改的弹窗 -->
  <el-dialog v-model="roleFormShow" :title="roleTitle" width="80%" :before-close="handleClose">
    <!-- 表单 -->
    <el-form :model="form" :rules="rules" label-width="120px" ref="formRef">

      <!-- 左侧表单内容 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="角色标识" prop="roleLabel">
            <el-input v-model="form.roleLabel" placeholder="请输入角色标识"/>
          </el-form-item>

          <el-form-item label="角色名称" prop="roleName">
            <el-input v-model="form.roleName" placeholder="请输入角色名称"/>
          </el-form-item>

          <el-form-item label="状态" prop="status">
            <el-switch
                v-model="form.status"
                active-text="启用"
                inactive-text="禁用"
                :active-value="false"
                :inactive-value="true"
            />
          </el-form-item>

          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" placeholder="请输入备注"/>
          </el-form-item>
        </el-col>

        <!-- 右侧权限选择树 -->
        <el-col :span="12" style="max-height: 400px; overflow-y: auto;">
          <el-form-item label="权限选择">
            <el-tree
                ref="permissionTreeRef"
                :data="formattedMenuList"
                :props="treeProps"
                show-checkbox
                check-strictly
                node-key="menuId"
                @check-change="handleCheckChange"
                :default-checked-keys="checkedMenuIds"
                default-expand-all
            />
          </el-form-item>
        </el-col>
      </el-row>

    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import {onMounted, ref} from 'vue'
// 导入接口
import {removeRole, saveRole, searchRoleById, searchRoleList, updateRole} from '@/api/role/index.js'
import {useMenuStore} from "@/stores/menu.js";
const menuStore = useMenuStore();
// 获取全局的菜单数据
const menuList = menuStore.menuList;
// 存储已选择的菜单ID
const checkedMenuIds = ref([]);
const treeProps = {
  label: 'menuName',
  children: 'children'
}

const formRef = ref(null);
const permissionTreeRef = ref();

let total = ref(0)
let selectRoleIds = ref([])
let selectRoleNames = ref([])

let roleFormShow = ref(false);
let roleTitle = ref("");

let queryForm = ref({
  roleLabel: undefined,
  roleName: undefined,
  pageNum: 1,
  pageSize: 10
})

// 初始化表单值
const initialFormValue = {
  roleId: undefined,
  roleLabel: undefined,
  roleName: undefined,
  sort: 0,
  status: false,
  remark: undefined,
  menuIdList: [], // 添加 menuIdList 参数，默认为空数组
};

// 新增和修改数据时的表单数据
let form = ref({...initialFormValue});

// 表单验证规则
let rules = ref({
  roleLabel: [
    { required: true, message: '请填写角色标识', trigger: 'blur' }
  ],
  roleName: [
    { required: true, message: '请填写角色名称', trigger: 'blur' }
  ],
  // 其他表单项的验证规则
});

let roleList = ref([])
let formattedMenuList = [];
onMounted(() => {
  handleSearchRoleList();
  formattedMenuList = formatMenuList(menuList);
})

function handleCheckChange(data, checked, indeterminate) {
  // 将收集到的ID设置到表单的menuIdList字段
  form.value.menuIdList = permissionTreeRef.value.getCheckedKeys();
}

// 查询所有角色
function handleSearchRoleList() {
  searchRoleList(queryForm.value).then(res => {
    if (res.data.code === 200) {
      roleList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}

function formatMenuList(menuItems) {
  return menuItems.map(item => ({
    menuId: item.menuId,
    menuName: item.menuName,
    path: item.path,
    perms: item.perms,
    children: item.children ? formatMenuList(item.children) : undefined,
  }));
}

// 重置
function handleRest() {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = 10;
  queryForm.value.roleLabel = undefined;
  queryForm.value.roleName = undefined;
  searchRoleList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      roleList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}
// 搜索
function handleQuery() {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = 10;
  searchRoleList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      roleList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}

function handleSizeChange(sizeNumber) {
  queryForm.value.pageSize = sizeNumber;
  searchRoleList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      roleList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}
// 点击下一页，上一页
function handleCurrentChange(pageNumber) {
  queryForm.value.pageNum = pageNumber;
  searchRoleList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      roleList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}
// 多选变化
function handleSelectionChange(selection) {
  selectRoleIds.value = selection.map(item => item.roleId);
  selectRoleNames.value = selection.map(item => item.roleName);
}

// 提交表单,根据form.roleId值判断是新增还是修改【有roleId值】
function handleSubmit() {
  // 做数据校验
  formRef.value.validate((valid) => {
    if (valid) {
      if(form.value.roleId) {
        // 修改
        updateRole(form.value).then(res => {
          if(res.data.code == 200) {
            // 关闭窗口
            roleFormShow.value = false;
            // 刷新列表
            handleSearchRoleList();
            // 弹窗提示新增成功
            ElMessage({
              message: '修改角色成功！',
              type: 'success',
            });
            handleClose();
          }
        })
      } else {
        // 新增，调用新增接口
        saveRole(form.value).then(res => {
          if (res.data.code == 200) {
            // 关闭窗口
            roleFormShow.value = false;
            // 刷新列表
            handleSearchRoleList();
            // 弹窗提示新增成功
            ElMessage({
              message: '新增角色成功！',
              type: 'success',
            });
            handleClose();
          }
        }).catch((error) => {
          ElMessageBox.alert(error, "警告", {
            showConfirmButton: false,
            closeOnClickModal: true
          });
        })
      }
    } else {
      // 表单验证失败
      ElMessage.error('表单验证失败，请检查输入')
    }
  })
}

// 新增按钮，弹出表单
function handleAdd() {
  roleFormShow.value = true;
  roleTitle.value = "新增角色";
}

// 修改按钮，根据roleId查询对应的数据，弹出表单，回显数据
function handleEdit(roleId) {
  // 先查询数据，再弹窗
  searchRoleById(roleId).then(res => {
    if (res.data.code == 200) {
      // 保障后端返回的字段名和前端字段名相同，可以一一赋值
      form.value = res.data.data;
      // 根据返回的角色数据中的 menuIdList 更新 checkedMenuIds
      checkedMenuIds.value = res.data.data.menuIdList;
      roleFormShow.value = true;
      roleTitle.value = "修改角色";
    } else {
      ElMessage({
        message: '数据查询失败！',
        type: 'error',
      })
    }
  })

}

// 删除按钮，弹出是否要删除数据，确定就删除，取消就不删除
function handleRemove(roleId, roleName) {
  // roleId其实是点击操作下的删除按钮时才会有数据
  ElMessageBox.confirm(
      `确定要删除【${roleName}】数据吗?`,
      '删除角色',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error',
      }
  ).then(() => {
    removeRole(roleId).then(res => {
      if (res.data.code == 200) {
        ElMessage({
          message: '删除成功',
          type: 'success',
        })
        // 刷新列表
        handleSearchRoleList();
      } else {
        console.log("res.data: ", res.data)
        ElMessage({
          message: res.data.msg || '',
          type: 'error',
        })
      }
    })
  })
      .catch((error) => {
        ElMessage({
          message: error,
          type: 'error',
        })
      })
}

// 关闭弹窗
function handleClose() {
  form.value = {...initialFormValue};
  roleFormShow.value = false;
  // 清空表单验证状态和错误信息
  formRef.value?.resetFields();
  checkedMenuIds.value = [];
  // 重置所有节点的选中状态
  permissionTreeRef.value.setCheckedNodes(checkedMenuIds.value, false);
  // 展开所有节点（递归方式）
  expandAllNodes(permissionTreeRef.value.root.childNodes);
}

function expandAllNodes(nodes) {
  for (const node of nodes) {
    node.expand();
    if (node.childNodes.length > 0) {
      expandAllNodes(node.childNodes);
    }
  }
}
</script>

<style lang="scss" scoped>
.pagination_container {
  position: relative;
  height: 40px;
  margin-top: 15px;

}

.el-pagination {
  position: absolute;
  right: 110px;
}
</style>