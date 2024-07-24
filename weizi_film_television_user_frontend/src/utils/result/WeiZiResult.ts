export type  WeiZiResult = {
  data: {
    code: number;
    data?: any[]; // 或者更具体的类型，比如 Channel[]
    msg?: string; // msg字段现在是可选的
  };
}
