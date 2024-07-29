import editForm from "../form.vue";
import {handleTree} from "@/utils/tree";
import {message} from "@/utils/message";
import {deleteMenu, getMenuList, saveMenu, updateMenu} from "@/api/menu";
import {$t, transformI18n} from "@/plugins/i18n";
import {addDialog} from "@/components/ReDialog";
import {h, onMounted, reactive, ref} from "vue";
import type {FormItemProps} from "../utils/types";
import {cloneDeep} from "@pureadmin/utils";
import {useRenderIcon} from "@/components/ReIcon/src/hooks";
import OperationType from "@/utils/enum";

export function useMenu() {
  const form = reactive({
    menuName: "",
    perms: ""
  });

  const formRef = ref();
  const dataList = ref([]);
  const loading = ref(true);

  const getMenuType = (type, text = false) => {
    switch (type) {
      case "DIRECTORY":
        return text ? transformI18n("type.menu.directory") : "warning";
      case "MENU":
        return text ? transformI18n("type.menu.menu") : "primary";
      case "BUTTON":
        return text ? transformI18n("type.menu.button") : "info";
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
      return text ? transformI18n("type.status.disable") : "danger";
    } else {
      return text ? transformI18n("type.status.enable") : "success";
    }
  };

  const columns: TableColumnList = [
    {
      label: "module.menuManagement.menuName",
      prop: "menuName",
      align: "left",
      cellRenderer: ({ row }) => (
        <>
          <span class="inline-block mr-1">
            {h(useRenderIcon(row.icon), {
              style: {paddingTop: "1px"}
            })}
          </span>
          <span>{transformI18n(row.menuName)}</span>
          {
            row.menuType !== 'BUTTON' ? (
              <>
                <br />
                <span style={{ paddingLeft: row.icon ? '40px' : '25px' }}>{row.menuName}</span>
              </>
            ) : null
          }
        </>
      )
    },
    {
      label: $t("module.menuManagement.menuType"),
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
      label: $t("module.menuManagement.path"),
      prop: "path"
    },
    {
      label: $t("module.menuManagement.componentPath"),
      prop: "componentPath",
    },
    {
      label: $t("module.menuManagement.perms"),
      prop: "perms"
    },
    {
      label: $t("module.menuManagement.status"),
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
      label: $t("module.menuManagement.sort"),
      prop: "sort",
      width: 100
    },
    {
      label: $t("buttons.hsoperation"),
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
    const { data } = await getMenuList(form); // 这里是返回一维数组结构，前端自行处理成树结构，返回格式要求：唯一id加父节点parentId，parentId取父节点id
    dataList.value = handleTree(data); // 处理成树结构
    setTimeout(() => {
      loading.value = false;
    }, 500);
  }

  function formatHigherMenuOptions(treeList) {
    if (!treeList || !treeList.length) return [];

    const newTreeList = [];
    for (let i = 0; i < treeList.length; i++) {
        const item = treeList[i];
        // 只有当 menuType 不是 "BUTTON" 时，才处理并添加到新列表中
        if (item.menuType !== "BUTTON") {
            item.menuName = transformI18n(item.menuName);
            // 递归处理子菜单，确保子菜单也经过相同逻辑的过滤
            item.children = formatHigherMenuOptions(item.children);
            newTreeList.push(item);
        }
    }
    return newTreeList;
}


  function openDialog(addOrEdit = OperationType.ADD, row?: FormItemProps) {
    let title = addOrEdit === OperationType.ADD ? "module.menuManagement.dialogAddTitle" : "module.menuManagement.dialogEditTitle";
    addDialog({
      title: `${transformI18n(title)}`,
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
          let messageContent = $t('module.menuManagement.saveSuccessMessage', {
            addOrEdit: transformI18n(title),
            menuName: transformI18n(curData.menuName)
          });
          message(
            messageContent,
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
            if (addOrEdit === OperationType.ADD) {
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
