import { $t } from "@/plugins/i18n";
import {error} from "@/router/enums";
const Layout = () => import("@/layout/index.vue");

export default [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/login/index.vue"),
    meta: {
      title: $t("menus.hslogin"),
      showLink: false,
      rank: 101
    }
  },
  {
    path: "/error/404",
    name: "404",
    component: () => import("@/views/error/404.vue"),
    meta: {
      title: $t("menus.hsfourZeroFour"),
      showLink: false,
      rank: 104
    }
  },
  {
    path: "/500",
    name: "500",
    component: () => import("@/views/error/500.vue"),
    meta: {
      title: $t("menus.hsFive"),
      showLink: false,
      rank: 105
    }
  },
  {
    path: "/redirect",
    component: Layout,
    meta: {
      title: $t("status.hsLoad"),
      showLink: false,
      rank: 102
    },
    children: [
      {
        path: "/redirect/:path(.*)",
        name: "Redirect",
        component: () => import("@/layout/redirect.vue")
      }
    ]
  },
  // 下面是一个无layout菜单的例子（一个全屏空白页面），因为这种情况极少发生，所以只需要在前端配置即可（配置路径：src/router/modules/remaining.ts）
  {
    path: "/empty",
    name: "Empty",
    component: () => import("@/views/404/index.vue"),
    meta: {
      title: $t("menus.hsempty"),
      showLink: false,
      rank: 103
    }
  }
] satisfies Array<RouteConfigsTable>;
