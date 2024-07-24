import editForm from "../form.vue";
import { handleTree } from "@/utils/tree";
import { message } from "@/utils/message";
import {getMenuList, saveMenu, updateMenu, deleteMenu} from "@/api/menu";
import { transformI18n } from "@/plugins/i18n";
import { addDialog } from "@/components/ReDialog";
import { reactive, ref, onMounted, h } from "vue";
import type { FormItemProps } from "../utils/types";
import {cloneDeep, isAllEmpty} from "@pureadmin/utils";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";

export function useMenu() {
  const form = reactive({
    menuName: ""
  });

  const formRef = ref();
  const dataList = ref([]);
  const loading = ref(true);

  const getMenuType = (type, text = false) => {
    switch (type) {
      case "DIRECTORY":
        return text ? "目录" : "warning";
      case "MENU":
        return text ? "菜单" : "primary";
      case "BUTTON":
        return text ? "按钮" : "info";
    }
  };

  const getMenuTypeNumberOptions = (type: number|string) => {
    switch (type) {
      case "MENU":
        return 1;
      case "BUTTON":
        return 2;
      case "DIRECTORY":
      default:
        return 0;
    }
  };

  const getMenuTypeStringOptions = (type: number|string) => {
    switch (type) {
      case 1:
        return "MENU";
      case 2:
        return "BUTTON";
      case 0:
      default:
        return "DIRECTORY";
    }
  };


  const getStatusType = (isAvailable: boolean, text = false) => {
    if (isAvailable) {
      return text ? "禁用" : "danger";
    } else {
      return text ? "启用" : "success";
    }
  };

  const columns: TableColumnList = [
    {
      label: "菜单名称",
      prop: "menuName",
      align: "left",
      cellRenderer: ({ row }) => (
        <>
          <span class="inline-block mr-1">
            {h(useRenderIcon(row.icon), {
              style: { paddingTop: "1px" }
            })}
          </span>
          <span>{transformI18n(row.menuName)}</span>
        </>
      )
    },
    {
      label: "菜单类型",
      prop: "menuType",
      width: 100,
      cellRenderer: ({ row, props }) => (
        <el-tag
          size={props.size}
          type={getMenuType(row.menuType)}
          effect="plain"
        >
          {getMenuType(row.menuType, true)}
        </el-tag>
      )
    },
    {
      label: "路由路径",
      prop: "path"
    },
    {
      label: "组件路径",
      prop: "componentPath",
    },
    {
      label: "权限标识",
      prop: "perms"
    },
    {
      label: "状态",
      prop: "status",
      width: 100,
      cellRenderer: ({ row, props }) => (
        <el-tag
          size={props.size}
          type={getStatusType(row.status)}
          effect="plain"
        >
          {getStatusType(row.status, true)}
        </el-tag>
      )
    },
    {
      label: "排序",
      prop: "sort",
      width: 100
    },
    {
      label: "操作",
      fixed: "right",
      width: 210,
      slot: "operation"
    }
  ];

  function handleSelectionChange(val) {
    console.log("handleSelectionChange", val);
  }

  function resetForm(formEl) {
    if (!formEl) return;
    formEl.resetFields();
    onSearch();
  }

  async function onSearch() {
    loading.value = true;
    const { data } = await getMenuList(); // 这里是返回一维数组结构，前端自行处理成树结构，返回格式要求：唯一id加父节点parentId，parentId取父节点id
    let newData = data?.list;
    if (!isAllEmpty(form.menuName)) {
      // 前端搜索菜单名称
      newData = newData.filter(item =>
        transformI18n(item.menuName).includes(form.menuName)
      );
    }
    dataList.value = handleTree(newData); // 处理成树结构
    setTimeout(() => {
      loading.value = false;
    }, 500);
  }

  function formatHigherMenuOptions(treeList) {
    if (!treeList || !treeList.length) return;
    const newTreeList = [];
    for (let i = 0; i < treeList.length; i++) {
      treeList[i].menuName = transformI18n(treeList[i].menuName);
      formatHigherMenuOptions(treeList[i].children);
      newTreeList.push(treeList[i]);
    }
    return newTreeList;
  }

  function openDialog(title = "新增", row?: FormItemProps) {
    console.log("row: ",row)
    addDialog({
      title: `${title}菜单`,
      props: {
        formInline: {
          higherMenuOptions: formatHigherMenuOptions(cloneDeep(dataList.value)),
          menuId: row?.menuId,
          parentId: row?.parentId,
          menuName: row?.menuName,
          path: row?.path,
          componentPath: row?.componentPath,
          icon: row?.icon,
          menuType: getMenuTypeNumberOptions(row?.menuType),
          perms: row?.perms,
          status: row?.status ?? false,
          sort: row?.sort ?? 0,
        }
      },
      width: "45%",
      draggable: true,
      fullscreenIcon: true,
      closeOnClickModal: false,
      contentRenderer: () => h(editForm, { ref: formRef }),
      beforeSure: (done, { options }) => {
        const FormRef = formRef.value.getRef();
        const curData = options.props.formInline as FormItemProps;
        function chores() {
          message(
            `您${title}了菜单名称为${transformI18n(curData.menuName)}的这条数据`,
            {
              type: "success"
            }
          );
          done(); // 关闭弹框
          onSearch(); // 刷新表格数据
        }
        FormRef.validate(valid => {
          if (valid) {
            // 创建一个新对象来处理
            const newData = { ...curData }; // 使用扩展运算符复制对象
            newData.menuType = getMenuTypeStringOptions(newData.menuType);
            // 表单规则校验通过
            if (title === "新增") {
              saveMenu(newData).then(res => {
                if (res.code === 200 || res.code === 403) {
                  chores();
                } else {
                  message(res.msg, { type: "error" });
                }
              });
            } else {
              updateMenu(newData).then(res => {
                if (res.code === 200 || res.code === 403) {
                  chores();
                } else {
                  message(res.msg, { type: "error" });
                }
              });
            }
          }
        });
      }
    });
  }

  function handleDelete(row) {
    deleteMenu(row.menuId).then(res => {
      if (res.code === 200) {
        message(`您删除了菜单名称为${transformI18n(row.menuName)}的这条数据`, {
          type: "success"
        });
      } else {
        message(res.msg, { type: "error" });
      }
    }).finally(() => {
      onSearch();
    });
  }

  onMounted(() => {
    onSearch();
  });

  return {
    form,
    loading,
    columns,
    dataList,
    /** 搜索 */
    onSearch,
    /** 重置 */
    resetForm,
    /** 新增、修改菜单 */
    openDialog,
    /** 删除菜单 */
    handleDelete,
    handleSelectionChange
  };
}
