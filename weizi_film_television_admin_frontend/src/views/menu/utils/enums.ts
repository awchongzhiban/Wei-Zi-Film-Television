import type { OptionsType } from "@/components/ReSegmented";
import {transformI18n} from "@/plugins/i18n";

const menuTypeOptions: Array<OptionsType> = [
  {
    label: transformI18n("type.menu.directory"),
    value: 0
  },
  {
    label: transformI18n("type.menu.menu"),
    value: 1
  },
  {
    label: transformI18n("type.menu.button"),
    value: 2
  }
];

const statusOptions: Array<OptionsType> = [
  {
    label: transformI18n("type.status.enable"),
    tip: "显示在菜单或可使用",
    value: false
  },
  {
    label: transformI18n("type.status.disable"),
    tip: "不显示在菜单或不可使用",
    value: true
  }
];

export {
  menuTypeOptions,
  statusOptions
};
