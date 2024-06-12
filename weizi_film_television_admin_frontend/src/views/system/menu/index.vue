<template>
  <!-- 头部搜索框 -->
  <el-form :model="queryForm" :inline="true" label-position="top" style="display: block;">
    <el-row>
      <el-col :span="8">
        <el-form-item label="用户名：">
          <el-input v-model="queryForm.username" />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="昵称：">
          <el-input v-model="queryForm.nickname" />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="功能：">
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button type="warning" @click="handleRest">重置</el-button>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>

  <!-- 功能按钮 -->
    <el-row :gutter="4" style="margin-bottom: 15px;">
        <el-col :span="6">
            <el-button type="primary" @click="handleAdd(0)">新增</el-button>
            <el-button type="danger" @click="handleRemove(0, null)">删除</el-button>
        </el-col>
    </el-row>

    <!-- 列表 -->
    <el-table :data="menuList" style="width: 100%" row-key="menuId" border default-expand-all @selection-change="handleSelectionChange">
        <el-table-column fixed type="selection" width="55" />
<!--        <el-table-column type="index" label="序号" width="55" />-->
        <el-table-column fixed prop="menuName" label="菜单名称" width="200" />
        <el-table-column prop="path" label="路由地址" width="200" />
        <el-table-column prop="perms" label="权限名称" width="200" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-icon v-if="!row.status" style="color: green"><Select/></el-icon>
            <el-icon v-else style="color: red"><CloseBold /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80"/>
        <el-table-column prop="componentPath" label="组件路径" width="200" />
        <el-table-column prop="createTime" label="创建时间" width="200"/>
        <el-table-column prop="updateTime" label="修改时间" width="200"/>
        <el-table-column prop="remark" label="备注" width="200" />
        <el-table-column fixed="right" label="操作" width="200">
            <template #default="scope">
                <el-button link type="primary" size="small" v-if="scope.row.menuType !== 'BUTTON'" @click="handleAdd(scope.row.menuId)">新增</el-button>
                <el-button link type="success" size="small" @click="handleEdit(scope.row.menuId)">修改</el-button>
                <el-button link type="danger" size="small" @click="handleRemove(scope.row.menuId, scope.row.menuName)">删除</el-button>
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
    <el-dialog v-model="menuFormShow" :title="menuTitle" width="50%" :before-close="handleClose">
        <!-- 表单 -->
      <el-form :model="form" :rules="rules" label-width="120px" ref="formRef">
        <el-row>
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio border :label="'DIRECTORY'">目录</el-radio>
                <el-radio border :label="'MENU'">菜单</el-radio>
                <el-radio border :label="'BUTTON'">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
            <el-col :span="12">
                <el-form-item label="菜单图标" prop="icon">
                    <el-popover
                        placement="bottom-start"
                        :width="460"
                        trigger="click"
                    >
                        <template #reference>
                            <!-- 通过插槽实现图标选择，默认是一个有图标的输入框 -->
                            <el-input v-model="form.icon" placeholder="请选择图标">
                                <template #prefix>
                                    <!-- 判断是否选中了icon -->
                                    <svg-icon
                                        v-if="form.icon"
                                        slot="prefix"
                                        :name="form.icon"
                                        width="16px"
                                        height="16px"
                                    />
                                    <!-- 如果未选中，显示默认的搜索图标 -->
                                    <el-icon v-else class="el-input__icon"><search /></el-icon>
                                </template>
                            </el-input>
                        </template>
                        <!-- 显示选择图标的组件 -->
                        <IconSelect ref="iconSelect" @selected="handleSelect" :active-icon="form.icon"/>
                    </el-popover>

                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="菜单名称" prop="menuName">
                    <el-input v-model="form.menuName" placeholder="请输入菜单名称"/>
                </el-form-item>
            </el-col>
        </el-row>

        <el-row>
            <el-col :span="12">
                <el-form-item label="排序" prop="sort">
                    <el-input-number :min="0" v-model="form.sort"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="路由地址" prop="path">
                    <el-input v-model="form.path" placeholder="请输入路由地址"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="组件路径" prop="componentPath">
                    <el-input v-model="form.componentPath" placeholder="请输入页面路径"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="权限标识" prop="perms">
                    <el-input v-model="form.perms" placeholder="请输入权限标识"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="状态" prop="status">
                    <el-switch
                        v-model="form.status"
                        active-text="可用"
                        inactive-text="不可用"
                        :active-value="false"
                        :inactive-value="true"
                    />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="备注" prop="remark">
                    <el-input v-model="form.remark" placeholder="请输入备注"/>
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
// 导入图标选择组件
import IconSelect from '@/components/IconSelect/index.vue'
// 导入接口
import {searchMenuList, searchMenuById, saveMenu, updateMenu, removeMenu} from '@/api/menu/index.js'

const formRef = ref(null);

let total = ref(0)
let selectMenuIds = ref([])
let selectMenuNames = ref([])

let menuFormShow = ref(false);
let menuTitle = ref("");

let queryForm = ref({
    menuName: undefined,
    perms: undefined,
    pageNum: 1,
    pageSize: 10
})

// 初始化表单值
const initialFormValue = {
  menuId: undefined,
  parentId: 0,
  menuName: undefined,
  path: undefined,
  componentPath: undefined,
  perms: undefined,
  icon: undefined,
  menuType: "DIRECTORY",
  sort: 0,
  status: false,
  remark: undefined,
};

// 新增和修改数据时的表单数据
let form = ref({...initialFormValue});

// 表单验证规则
let rules = ref({
  menuType: [
    { required: true, message: '请选择菜单类型', trigger: 'blur' }
  ],
  icon: [
    { required: true, message: '请选择菜单图标', trigger: 'blur' }
  ],
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  perms: [
    {
      trigger: 'blur',
      validator: (rule, value, callback) => {
        if (value && !/^[a-zA-Z0-9]+(?:[:][a-zA-Z0-9]+)*(:)?$/.test(value)) {
          callback(new Error('请输入合法的权限标识'));
        } else {
          callback();
        }
      }
    }
  ],
});

// 树形选择器
let menuSelectData = ref([])

let menuList = ref([])
onMounted(() => {
    handleSearchMenuList();
})

// 查询所有菜单
function handleSearchMenuList() {
  searchMenuList(queryForm.value).then(res => {
    if (res.data.code === 200) {
      menuList.value = res.data.data.list;
      total.value = res.data.data.total;
      // 生成树形结构数据
      menuSelectData.value = _generateTree(res.data.data.list);
    }
  })
}
// 重置
function handleRest() {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = 10;
  queryForm.value.menuName = undefined;
  queryForm.value.perms = undefined;
  searchMenuList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      menuList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}
// 搜索
function handleQuery() {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = 10;
  searchMenuList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      menuList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}

function handleSizeChange(sizeNumber) {
  queryForm.value.pageSize = sizeNumber;
  searchMenuList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      menuList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}
// 点击下一页，上一页
function handleCurrentChange(pageNumber) {
  queryForm.value.pageNum = pageNumber;
  searchMenuList(queryForm.value).then(res => {
    if(res.data.code == 200) {
      // 获取数据
      menuList.value = res.data.data.list;
      total.value = res.data.data.total;
    }
  })
}
// 多选变化
function handleSelectionChange(selection) {
  selectMenuIds.value = selection.map(item => item.menuId);
  selectMenuNames.value = selection.map(item => item.menuName);
}
// 提交表单,根据form.menuId值判断是新增还是修改【有menuId值】
function handleSubmit() {
  // 做数据校验
  formRef.value.validate((valid) => {
    if (valid) {
      if(form.value.menuId) {
        // 修改
        updateMenu(form.value).then(res => {
          if(res.data.code == 200) {
            // 关闭窗口
            menuFormShow.value = false;
            // 刷新列表
            handleSearchMenuList();
            // 弹窗提示新增成功
            ElMessage({
              message: '修改菜单成功！',
              type: 'success',
            });
            handleClose();
          }
        })
      }else {
        // 新增，调用新增接口
        saveMenu(form.value).then(res => {
          if(res.data.code == 200) {
            // 关闭窗口
            menuFormShow.value = false;
            // 刷新列表
            handleSearchMenuList();
            // 弹窗提示新增成功
            ElMessage({
              message: '新增菜单成功！',
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
function handleAdd(parentId) {
  menuFormShow.value = true;
  menuTitle.value = parentId === 0 ? "新增菜单" : "新增子菜单";
  // 如果是新增子菜单，将当前菜单的id赋值给form.value.parentId
  if (parentId !== 0) {
    form.value.parentId = parentId; // 假设当前菜单的id存储在currentMenuId中
  }
}

// 修改按钮，根据menuId查询对应的数据，弹出表单，回显数据
function handleEdit(menuId) {
  // 先查询数据，再弹窗
  searchMenuById(menuId).then(res => {
    if(res.data.code == 200) {
      // 保障后端返回的字段名和前端字段名相同，可以一一赋值
      form.value = res.data.data;
      menuFormShow.value = true;
      menuTitle.value = "修改菜单";
    } else {
      ElMessage({
        message: '数据查询失败！',
        type: 'error',
      })
    }
  })

}
// 删除按钮，弹出是否要删除数据，确定就删除，取消就不删除
function handleRemove(menuId, menuName) {
  // 走删除接口
  let menuIds = undefined;
  let menuNames;
  if(menuId > 0) {
    menuIds = [menuId]
    menuNames = [menuName]
  }else {
    menuIds = selectMenuIds.value;
    menuNames = selectMenuNames.value;
  }
  // menuId其实是点击操作下的删除按钮时才会有数据
  ElMessageBox.confirm(
      `确定要删除【${menuNames}】数据吗?`,
      '删除菜单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error',
      }
  ).then(() => {
        removeMenu(menuIds).then(res => {
          if(res.data.code == 200) {
            ElMessage({
              message: '删除成功',
              type: 'success',
            })
            // 刷新列表
            handleSearchMenuList();
          } else {
            console.log("res.data: ",res.data)
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
  menuFormShow.value = false;
  // 清空表单验证状态和错误信息
  formRef.value?.resetFields();
}

// 选择图标
function handleSelect(name) {
  form.value.icon = name;
}

function _generateTree(menuList) {
  // 创建根节点
  let root = { label: '主目录', value: 'DIRECTORY', children: [] };

  // 递归函数，用于将菜单数据转换为树形结构
  function buildTree(parentNode, items) {
    for (const item of items) {
      // 创建当前节点
      let node = { label: item.menuName, value: item.menuId, children: [] };
      // 如果当前节点有子菜单，则递归构建子树
      if (item.children && item.children.length > 0) {
        buildTree(node, item.children);
      }
      // 将当前节点添加到父节点的子节点列表中
      parentNode.children.push(node);
    }
  }

  // 使用递归构建树
  buildTree(root, menuList);

  return [root];
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