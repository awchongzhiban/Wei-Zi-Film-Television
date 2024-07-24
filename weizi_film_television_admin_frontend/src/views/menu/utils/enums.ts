import type { OptionsType } from "@/components/ReSegmented";

const menuTypeOptions: Array<OptionsType> = [
  {
    label: "目录",
    value: 0
  },
  {
    label: "菜单",
    value: 1
  },
  {
    label: "按钮",
    value: 2
  }
];

const statusOptions: Array<OptionsType> = [
  {
    label: "可用",
    tip: "显示在菜单或可使用",
    value: false
  },
  {
    label: "不可用",
    tip: "不显示在菜单或不可使用",
    value: true
  }
];

export {
  menuTypeOptions,
  statusOptions
};
