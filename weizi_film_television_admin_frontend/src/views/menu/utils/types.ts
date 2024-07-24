interface FormItemProps {
  higherMenuOptions: Record<string, unknown>[];
  menuId: number;
  parentId: number;
  menuName: string;
  sort: number;
  path: string;
  componentPath: string;
  icon: string;
  /** 菜单类型（DIRECTORY代表目录、MENU代表菜单、BUTTON代表按钮）*/
  menuType: number | string;
  perms: string;
  status: boolean;
}
interface FormProps {
  formInline: FormItemProps;
}

export type { FormItemProps, FormProps };
