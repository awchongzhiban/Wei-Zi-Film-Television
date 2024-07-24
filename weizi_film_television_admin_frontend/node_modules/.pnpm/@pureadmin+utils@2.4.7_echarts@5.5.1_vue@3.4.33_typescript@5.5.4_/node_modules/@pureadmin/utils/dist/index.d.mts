import { WritableComputedRef, CSSProperties, Plugin, AppContext, Component, ShallowRef, Ref } from 'vue';
import { ECharts, EChartsOption } from 'echarts';
import * as echarts_types_dist_echarts from 'echarts/types/dist/echarts';

/**
 * Promise, or maybe not
 */
type Awaitable<T> = T | PromiseLike<T>;
/**
 * Null or whatever
 */
type Nullable<T> = T | null | undefined;
/**
 * Array, or not yet
 */
type Arrayable<T> = T | Array<T>;
/**
 * Function
 */
interface Fn<T = any, R = T> {
    (...arg: T[]): R;
}
/**
 * Constructor
 */
type Constructor<T = void> = new (...args: any[]) => T;
/**
 * Infers the element type of an array
 */
type ElementOf<T> = T extends (infer E)[] ? E : never;
/**
 * RefType
 */
type RefType<T> = T | null;
/**
 * Record
 */
type Recordable<T = any> = Record<string, T>;
/**
 * ReadonlyRecordable
 */
type ReadonlyRecordable<T> = Readonly<Record<string, T>>;
/**
 * ComponentElRef
 */
interface ComponentElRef<T extends HTMLElement = HTMLDivElement> {
    $el: T;
}
/**
 * ComponentRef
 */
type ComponentRef<T extends HTMLElement = HTMLDivElement> = ComponentElRef<T> | null;
/**
 * ElRef
 */
type ElRef<T extends HTMLElement = HTMLDivElement> = Nullable<T>;
interface ElementRef<T = any> {
    value: T;
}
interface RefValue<T = any> {
    value: T;
}
interface ComputedRefValue<T = any> extends WritableComputedRef<T> {
    readonly value: T;
}
/**
 * Defines an intersection type of all union items.
 *
 * @param U Union of any types that will be intersected.
 * @returns U items intersected
 * @see https://stackoverflow.com/a/50375286/9259330
 */
type UnionToIntersection<U> = (U extends unknown ? (k: U) => void : never) extends (k: infer I) => void ? I : never;
/**
 * Infers the arguments type of a function
 */
type ArgumentsType<T> = T extends (...args: infer A) => any ? A : never;
type MergeInsertions<T> = T extends object ? {
    [K in keyof T]: MergeInsertions<T[K]>;
} : T;
type DeepMerge<F, S> = MergeInsertions<{
    [K in keyof F | keyof S]: K extends keyof S & keyof F ? DeepMerge<F[K], S[K]> : K extends keyof S ? S[K] : K extends keyof F ? F[K] : never;
}>;
interface GlobalUtilsPropertiesApi {
    $echarts: ECharts;
}
/**
 * amount.ts
 */
interface AmountOpt {
    /** 保留几位小数，默认 `0` */
    digit?: number;
    /** 小数位是否四舍五入，默认 `false` 不进行四舍五入 */
    round?: boolean;
}
/**
 * array.ts
 */
type OrderType = 'asc' | 'desc' | 'random';
interface DivideOptions {
    /** 每一份的最小阀值。默认`0` */
    minPerPart?: number;
    /** 每一份的最大阀值。默认最大阀值不会超过总数 */
    maxPerPart?: number;
    /** 返回数组的排序方式：递增(`'asc'`)、递减(`'desc'`)、随机(`'random'`)。默认为`'random'` */
    order?: OrderType;
}
/**
 * color.ts
 */
type ColorType = "rgb" | "hex" | "hsl";
interface ColorOptions {
    /** 颜色格式 `rgb` 、 `hex` 、 `hsl` ，默认 `rgb` */
    type?: ColorType;
    /** 生成颜色的数量。指定数量，则返回生成颜色数组，数量为 `0` 或不指定，返回生成颜色字符串 */
    num?: number;
}
interface GradientOptions {
    /** 基础色相（`0`-`360`度），默认为一个随机色相 */
    baseHue?: number;
    /** 从基础色相偏移的度数，用于创建第二种和谐色相，默认为`30`度 */
    hueOffset?: number;
    /** 饱和度百分比（`50`-`100%`），颜色的鲜艳程度，默认为`70%` */
    saturation?: number;
    /** 亮度百分比（`40`-`70%`），颜色的明亮程度，默认为`60%` */
    lightness?: number;
    /** 渐变角度（`0`-`360`度），决定渐变的方向，默认为`135`度 */
    angle?: number;
    /** 是否随机生成色相，默认为`false` */
    randomizeHue?: boolean;
    /** 是否随机生成饱和度，默认为`false` */
    randomizeSaturation?: boolean;
    /** 是否随机生成亮度，默认为`false` */
    randomizeLightness?: boolean;
    /** 是否随机生成渐变角度，默认为`false` */
    randomizeAngle?: boolean;
}
/**
 * date.ts
 */
type dateType = 1 | 2 | 3;
interface currentDateType {
    /** 当前年月日 */
    ymd: string;
    /** 当前时分秒 */
    hms: string;
    /** 当前周几 */
    week: string;
}
interface currentDateOpt {
    /** 当前返回的年月日和时分秒的格式 1：汉字模式（默认） 2：- 连接符 3：/ 连接符  */
    type?: dateType;
    /** 自定义 `week` 返回格式前缀，默认 `星期` */
    prefix?: string;
}
interface hmsType {
    /** 小时 */
    h: number | string;
    /** 分钟 */
    m: number | string;
    /** 秒 */
    s: number | string;
}
/**
 * debounce.ts
 */
type TimeoutHandle = ReturnType<typeof setTimeout> | null;
type FunctionArgs<Args extends any[] = any[], Return = void> = (...args: Args) => Return;
/**
 * device.ts
 */
interface DeviceType {
    match: Fn;
}
interface BrowserType {
    /** 浏览器型号 */
    browser: string;
    /** 浏览器版本 */
    version: string;
}
/**
 * formData.ts
 */
interface HandleFileType {
    /** `File`或`Blob`文件 */
    file?: File | Blob;
    /** 传过来的文件字段键名 */
    key?: string;
    /** `new FormData()` 可以使用它的所有方法 */
    formData?: FormData;
}
interface FormDataOptions {
    /** 用于指定文件字段的键名，默认`file` */
    fileKey?: string;
    /** 自定义处理文件的函数 */
    handleFile?: (params: HandleFileType) => void;
    /** 定义需要过滤掉的值，它们不会出现在请求参数中 */
    filter?: any[];
}
/**
 * banMouseEvent.ts
 * @param "contextmenu"  右键
 * @param "selectstart"  选择
 * @param "copy"  拷贝
 */
type MouseEvent = "contextmenu" | "selectstart" | "copy";
/**
 * base64Conver.ts
 */
interface grayOpt {
    /** `RGB`颜色模型中的红色，默认`0.3` */
    red?: number;
    /** `RGB`颜色模型中的绿色，默认`0.59` */
    green?: number;
    /** `RGB`颜色模型中的蓝色，默认`0.11` */
    blue?: number;
    /** 使用`canvas`缩放图像比例，默认`1`不缩放保持原始比例，建议范围`0.2 < scale < 2` */
    scale?: number;
}
/**
 * link.ts
 * @param "_blank"     在新窗口中打开被链接文档（默认）
 * @param "_self"      在相同的框架中打开被链接文档
 * @param "_parent"    在父框架集中打开被链接文档
 * @param "_top"       在整个窗口中打开被链接文档
 * @param "framename"  在指定的框架中打开被链接文档
 */
type Target = "_blank" | "_self" | "_parent" | "_top" | "framename";
/**
 * packageSize.ts
 */
interface packageOpt {
    /** 文件夹名（默认：`dist`） */
    folder?: string;
    /** 是否返回已经转化好单位的包总大小（默认：`true`） */
    format?: boolean;
    /** 回调函数，返回包总大小（单位：字节） */
    callback: CallableFunction;
}
/**
 * @description 获取指定文件夹中所有文件的总大小
 */
declare const getPackageSize: (options: packageOpt) => void;
/**
 * performance.ts
 */
interface Performance {
    /** DNS查询耗时（单位：秒） */
    dns: number;
    /** TCP连接耗时（单位：秒） */
    tcp: number;
    /** Request请求耗时（单位：秒） */
    request: number;
    /** 解析Dom树耗时（单位：秒） */
    dom: number;
    /** 白屏时长（单位：秒） */
    whiteScreen: number;
}
/**
 * storage.ts
 */
interface ProxyStorage {
    setItem<T>(k: string, v: T): void;
    getItem<T>(k: string): T;
    removeItem(k: string): void;
    clear(): void;
}
/**
 * substring.ts
 */
interface HideTextIndex {
    /** 文字隐藏的开始位置 */
    start: number;
    /** 文字隐藏的结束位置 */
    end: number;
}
/**
 * useEcharts.ts
 */
type ToolTipType = "x" | "y" | true;
type Theme = "default" | "light" | "dark" | string;
declare type ElementEventType = "echarts" | "zrender";
declare type ElementEventName = "click" | "dblclick" | "mousewheel" | "mouseout" | "mouseover" | "mouseup" | "mousedown" | "mousemove" | "contextmenu" | "drag" | "dragstart" | "dragend" | "dragenter" | "dragleave" | "dragover" | "drop" | "globalout" | "selectchanged" | "highlight" | "downplay" | "legendselectchanged" | "legendselected" | "legendunselected" | "legendselectall" | "legendinverseselect" | "legendscroll" | "datazoom" | "datarangeselected" | "graphroam" | "georoam" | "treeroam" | "timelinechanged" | "timelineplaychanged" | "restore" | "dataviewchanged" | "magictypechanged" | "geoselectchanged" | "geoselected" | "geounselected" | "axisareaselected" | "brush" | "brushEnd" | "brushselected" | "globalcursortaken" | "rendered" | "finished";
interface EchartOptions {
    /** 主题色（可选`default`、`light`、`dark`，也可以 [自定义主题](https://echarts.apache.org/zh/theme-builder.html) ，默认`default`） */
    theme?: Theme | RefValue<Theme> | ComputedRefValue<Theme>;
    /** 给`x`、`y`轴添加`Tooltip`文字提示的元素id，默认`tooltipElement` */
    tooltipId?: string;
    /** 设备像素比，默认取浏览器的值`window.devicePixelRatio` */
    devicePixelRatio?: number;
    /** 渲染模式，支持`canvas`或者`svg`，默认`canvas` */
    renderer?: "canvas" | "svg";
    /** 是否使用服务端渲染，只有在`SVG`渲染模式有效。开启后不再会每帧自动渲染，必须要调用 [renderToSVGString](https://echarts.apache.org/zh/api.html#echartsInstance.renderToSVGString) 方法才能得到渲染后`SVG`字符串 */
    ssr?: boolean;
    /** 是否开启脏矩形渲染，只有在`Canvas`渲染模式有效，默认为`false` */
    useDirtyRect?: boolean;
    /** 是否扩大可点击元素的响应范围。`null`表示对移动设备开启；`true`表示总是开启；`false`表示总是不开启 */
    useCoarsePointer?: boolean;
    /** 扩大元素响应范围的像素大小，配合`opts.useCoarsePointer`使用 */
    pointerSize?: number;
    /** 可显式指定实例宽度，单位为像素。如果传入值为`null/undefined/'auto'`，则表示自动取`dom`（实例容器）的宽度 */
    width?: number | string;
    /** 可显式指定实例高度，单位为像素。如果传入值为`null/undefined/'auto'`，则表示自动取`dom`（实例容器）的高度 */
    height?: number | string;
    /** 使用的语言，内置`ZH`和`EN`两个语言，也可以使用 [echarts.registerLocale](https://echarts.apache.org/zh/api.html#echarts.registerLocale) 方法注册新的语言包。目前支持的语言见 [src/i18n](https://github.com/apache/echarts/tree/release/src/i18n) */
    locale?: string;
}
interface OptionsParams {
    /** 事件类型名称 `必传` */
    name: ElementEventName;
    /** 回调函数，返回 [params](https://echarts.apache.org/zh/api.html#events.%E9%BC%A0%E6%A0%87%E4%BA%8B%E4%BB%B6) 参数 `必传` */
    callback: Fn;
    /** `echarts`事件（默认）或 [zrender](https://echarts.apache.org/handbook/zh/concepts/event/#%E7%9B%91%E5%90%AC%E2%80%9C%E7%A9%BA%E7%99%BD%E5%A4%84%E2%80%9D%E7%9A%84%E4%BA%8B%E4%BB%B6) 事件 */
    type?: ElementEventType;
    /** `query`属性，点击 [此处](https://echarts.apache.org/handbook/zh/concepts/event/#%E9%BC%A0%E6%A0%87%E4%BA%8B%E4%BB%B6%E7%9A%84%E5%A4%84%E7%90%86) 搜索`query`进行了解 可选 */
    query?: string | object;
}
interface LoadOpts {
    type?: string;
    opts?: {
        text?: string;
        color?: string;
        textColor?: string;
        maskColor?: string;
        zlevel?: number;
        /** 字体大小。从 `v4.8.0` 开始支持 */
        fontSize?: number;
        /** 是否显示旋转动画（spinner）。从 `v4.8.0` 开始支持 */
        showSpinner?: boolean;
        /** 旋转动画（spinner）的半径。从 `v4.8.0` 开始支持 */
        spinnerRadius?: number;
        /** 旋转动画（spinner）的线宽。从 `v4.8.0` 开始支持 */
        lineWidth?: number;
        /** 字体粗细。从 `v5.0.1` 开始支持 */
        fontWeight?: string;
        /** 字体风格。从 `v5.0.1` 开始支持 */
        fontStyle?: string;
        /** 字体系列。从 `v5.0.1` 开始支持 */
        fontFamily?: string;
    };
}
interface AppendDataOpts {
    /** 要增加数据的系列序号 */
    seriesIndex?: string | number;
    /** 增加的数据 */
    data?: Array<any>;
}
interface DataURL {
    /** 导出的格式，可选 png, jpg, svg（注意：png, jpg 只有在 canvas 渲染器的时候可使用，svg 只有在使用 svg 渲染器的时候可用） */
    type?: string;
    /** 导出的图片分辨率比例，默认为 1 */
    pixelRatio?: number;
    /** 导出的图片背景色，默认使用 option 里的 backgroundColor */
    backgroundColor?: string;
    /** 忽略组件的列表，例如要忽略 toolbox 就是 ['toolbox'] */
    excludeComponents?: Array<string>;
}
interface EventParams {
    componentIndex?: number;
    /** 当前点击的图形元素所属的组件名称，其值如 'series'、'markLine'、'markPoint'、'timeLine' 等 */
    componentType: string;
    /** 系列类型。值可能为：'line'、'bar'、'pie' 等。当 componentType 为 'series' 时有意义 */
    seriesType: string;
    /** 系列在传入的 option.series 中的 index。当 componentType 为 'series' 时有意义 */
    seriesIndex: number;
    /** 系列名称。当 componentType 为 'series' 时有意义 */
    seriesName: string;
    /** 数据名，类目名 */
    name: string;
    /** 数据在传入的 data 数组中的 index */
    dataIndex: number;
    /** 传入的原始数据项 */
    data: object;
    /** sankey、graph 等图表同时含有 nodeData 和 edgeData 两种 data，dataType 的值会是 'node' 或者 'edge'，表示当前点击在 node 还是 edge 上，其他大部分图表中只有一种 data，dataType 无意义 */
    dataType: string;
    event?: any;
    type?: string;
    targetType?: string;
    /** 传入的数据值 */
    value: string | number | Array<string | number>;
    /** 数据图形的颜色。当 componentType 为 'series' 时有意义 */
    color: string;
}
interface UtilsEChartsOption extends EChartsOption {
    /** 是否清空当前实例，会移除实例中的图表，一般用于动态渲染，默认：`true` */
    clear?: boolean;
    /** 给`x`、`y`轴添加`Tooltip`文字提示，一般用于文字太长，`x`代表`x轴`   `y`代表`y轴`   `true`代表`x、y轴`（该属性生效的前提是将 `xAxis` 或者 `yAxis` 添加`triggerEvent: true`属性，用到哪个添加哪个） */
    addTooltip?: ToolTipType;
    /**
     * 第一种含义（默认）：`window.onresize` 时改变图表尺寸的延时时间，单位毫秒，默认 `300` 毫秒。当 `echarts` 在拖拉窗口时不会自适应（一般不会出现这种情况），可以调整 `delay` 数值到自适应为止
     * 第二种含义：如果设置了`container`，那么`delay`就代表指定容器元素尺寸变化的防抖时长，单位毫秒，默认`40`毫秒
     * */
    delay?: number;
    /** 是否监听页面`resize`事件并在页面`resize`时改变图表尺寸以适应当前容器，监听及改变，`true`(默认)代表监听 `false`代表不监听 */
    resize?: boolean;
    /**
     * 监听指定容器元素尺寸的变化，从而实现图表尺寸自适应当前容器
     * `container`可以是类名（`'.class'`）也可以是`ID`（`'.id'`），内部用的是`document.querySelectorAll`。当然它也可以是 [ref](https://cn.vuejs.org/guide/essentials/template-refs.html#template-refs)
     * 使用场景：常用于在容器宽高尺寸非固定值时，设置`container`实现图表尺寸自适应
     * 注意：设置`container`后，`resize`属性就无效了。如果既想监听指定容器元素，又想监听`body`或更多元素，可以传字符串数组，如`['.class','#id','body',...]`
     * */
    container?: ElementRef<HTMLDivElement> | string | (ElementRef<HTMLDivElement> | string)[];
}
interface EchartGlobalProperties {
    /** `globalProperties`属性名 */
    name?: string;
    /** `globalProperties`属性值 */
    value?: ECharts;
}
/**
 * useDraggable.ts
 */
interface ArgsDraggable {
    /** 添加额外样式到`dragRef`上，默认添加`{ cursor: "move", userSelect: "none" }` */
    dragRefStyle?: CSSProperties;
    /** 当页面`resize`时，如果拖动的元素超出新的视口边界，是否将其恢复到初始位置。默认`true`，代表恢复到初始位置。可选`false`，代表不会恢复到初始位置。当然也可以填写`resize`时的防抖延迟时间，默认`60`毫秒 */
    resize?: boolean | number;
}
/**
 * useLoader.ts
 */
declare type documentElement = "head" | "body";
interface OptionsScript {
    /** 要加载的资源链接地址 */
    src: string | string[];
    /** 标签插入到哪个元素里。默认`CSS`（`link`标签）插入到`head`，`JavaScript`（`script`标签）插入到`body` */
    element?: documentElement | HTMLElement;
}
interface LoadResult {
    /** 加载的资源链接地址 */
    src?: string;
    /** 加载状态（加载成功、加载失败） */
    message?: string;
}
/**
 * useWatermark.ts
 */
interface gradientType {
    /** 范围 `0.0` 到 `1.0` https://www.runoob.com/tags/canvas-addcolorstop.html */
    value: number;
    /** 对应 `value` 范围的文本颜色 */
    color: string;
}
interface attr {
    /** 字体，默认 `normal 16px Arial, 'Courier New', 'Droid Sans', sans-serif`，`Arial`和`Courier New`正常情况下在`Windows`、`macOS`、`iOS`默认存在，`Droid Sans`正常情况下在`Linux`和`Android`默认存在，`sans-serif`作为候补是一个通用的字体族关键词，它会使用浏览器使用默认的无衬线字体。`font`的字体遵循从左往右原则 */
    font?: string;
    /** 字体颜色，默认 `rgba(128, 128, 128, 0.3)` */
    color?: string;
    /** 宽度，默认 `250` */
    width?: number;
    /** 高度，默认 `100` */
    height?: number;
    /** 水印整体的旋转，默认 `-10` */
    rotate?: number;
    /** 水印的 `z-index`，默认 `100000` */
    zIndex?: string;
    /** 字体渐变色，优先级高于 `color` */
    gradient?: Array<gradientType>;
    /** 创建阴影（四个参数，如下）
     *  第一个填 `shadowBlur`，`必填`，具体设置看：https://www.runoob.com/jsref/prop-canvas-shadowblur.html
     *  第二个填 `shadowColor`，`可选，默认#000000`，具体设置看：https://www.runoob.com/jsref/prop-canvas-shadowcolor.html
     *  第三个填 `shadowOffsetX`，`可选，默认0`，具体设置看：https://www.runoob.com/jsref/prop-canvas-shadowoffsetx.html
     *  第四个填 `shadowOffsetY`，`可选，默认0`，具体设置看：https://www.runoob.com/jsref/prop-canvas-shadowoffsety.html
     */
    shadowConfig?: Array<any>;
    /** 透明度，范围 `0.0`（完全透明） 到 `1.0` */
    globalAlpha?: number;
    /** 多行水印文本的行高，默认 `20` */
    lineHeight?: number;
    /** 多行水印文本的换行标识符，默认遇到`、`就换行 */
    wrap?: string;
    /** 文本对齐方向。当多行文本时默认`center`，反之默认`left`。具体设置看：https://developer.mozilla.org/zh-CN/docs/Web/API/CanvasRenderingContext2D/textAlign */
    textAlign?: CanvasTextAlign;
    /** 图片路径 */
    image?: string;
    /** 图片宽度 */
    imageWidth?: number;
    /** 图片高度 */
    imageHeight?: number;
    /** 是否让水印无法删除，默认`false`，开启后在控制台操作对应的 `Elements` 也无法删除 */
    forever?: boolean;
}
/**
 * useAttrs.ts
 */
interface AttrParams {
    excludeListeners?: boolean;
    excludeKeys?: string[];
}
/**
 * is.ts
 */
interface isParams {
    /** 自定义 `unicode`，不会覆盖默认的 `unicode` */
    unicode?: string;
    /** 自定义 `unicode`，会覆盖默认的 `unicode` */
    replaceUnicode?: string;
    /** 是否全部是中文，默认 `false` */
    all?: boolean;
    /** 是否删除全部空格，默认 `false` */
    pure?: boolean;
}
/**
 * useDark.ts
 */
interface DarkOptions {
    /** 自定义选择器，默认`html`，可选`body` */
    selector?: "html" | "body";
    /** 检测某个类名是否在`html`或`body`上存在，默认`dark` */
    className?: string;
}
/**
 * useScrollTo.ts
 */
interface EaseInOutQuadOptions {
    /** 动画开始的指定时间 */
    timeElapsed: number;
    /** 对象在`x`轴上的指定起始位置 */
    startValue: number;
    /** 对象的指定值更改 */
    byValue: number;
    /** 整个过程的指定持续时间，单位毫秒 */
    duration: number;
}
interface ScrollOptions {
    /** 滚动对象的实例 */
    el: HTMLElement | any;
    /** 滚动到某个位置 */
    to: number;
    /** 滚动方向 */
    directions: "scrollTop" | "scrollLeft";
    /** 滚动时长，单位毫秒。默认`0`，没有缓动效果 */
    duration?: number;
    /** 滚动完毕的回调，返回一个参数`msg`。`msg`等于`滚动完毕`，代表滚动完成；`msg`等于`无需滚动`，代表当前已处于滚动目标位置，无需滚动 */
    callback?: (/** `msg`等于`滚动完毕`，代表滚动完成；`msg`等于`无需滚动`，代表当前已处于滚动目标位置，无需滚动 */ msg?: string) => void;
}
/**
 * useResizeObserver.ts
 */
interface UseResizeObserverOptions {
    /** 防抖延迟时间，默认`40`毫秒 */
    time?: number;
    /** 初始化时是否立刻触发回调，默认`true` */
    immediate?: boolean;
    /** `content-box`（默认）指的是元素的内容区域尺寸，不包括边框和内边距；`border-box`指的是元素的边框盒尺寸，包括内容、内边距和边框，但不包括外边距；`device-pixel-content-box`指的是设备像素级别观察尺寸变化 */
    box?: "content-box" | "border-box" | "device-pixel-content-box";
}

/**
 * @description 在数值后加 `.00`
 * @param val
 * @returns 加完 `.00` 后的值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/amount/amount#addzero
 */
declare const addZero: (val: any) => string | boolean;
/**
 * @description 分转元
 * @param val 分
 * @param format 转元后像 `10` 、`20`、 `100` 、`1000` 这种整金额默认会在末尾加 `.00` ，如果不想要设置成 `fasle` 即可
 * @returns 元
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/amount/amount#centstodollars
 */
declare const centsToDollars: (val: any, format?: boolean) => any;
/**
 * @description 元转分
 * @param val 元
 * @param digit 转换倍数，默认 `100`
 * @returns 分
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/amount/amount#dollarstocents
 */
declare const dollarsToCents: (val: number, digit?: number) => number;
/**
 * @description 获取数值的小数位数
 * @param val 数值
 * @returns 小数位数
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/amount/amount#getdecimalplaces
 */
declare const getDecimalPlaces: (val: string | number) => number;
/**
 * @description 金额转大写汉字（支持小数位）
 * @param val 金额
 * @param format 整 （如果是整数金额最后面会默认加个 `整`，不想要的话给空字符串 `""`）
 * @returns 大写汉字金额
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/amount/amount#priceuppercase
 */
declare const priceUppercase: (val: any, format?: string) => string;
/**
 * @description 格式化金额，三位加一个逗号
 * @param amount 金额
 * @param options `AmountOpt`
 * @returns 格式化后的金额
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/amount/amount#pricetothousands
 */
declare const priceToThousands: (amount: number, options?: AmountOpt) => string;

/**
 * @description 禁止指定的鼠标事件
 * @param eventList 鼠标事件（ `contextmenu` ：右键、 `selectstart` ：选择、 `copy` ：拷贝）
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/mouseEvent/mouseEvent#banmouseevent
 */
declare const banMouseEvent: (eventList: Array<MouseEvent>) => void;
/**
 * @description 允许指定的鼠标事件
 * @param eventList 鼠标事件（ `contextmenu` ：右键、 `selectstart` ：选择、 `copy` ：拷贝）
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/mouseEvent/mouseEvent#allowmouseevent
 */
declare const allowMouseEvent: (eventList: Array<MouseEvent>) => void;

/**
 * @description `base64` 转 `blob`
 * @param base64Buf `base64`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/base64Conver/base64Conver#dataurltoblob
 */
declare function dataURLtoBlob(base64Buf: string): Blob;
/**
 * @description 图片 `url` 转 `base64`
 * @param url 图片 `url`
 * @param mineType 图片格式，默认为 `image/png`
 * @param encoderOptions `0` 到 `1` 之间的取值，主要用来选定图片的质量，默认值是 `0.92`，超出范围也会选择默认值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/base64Conver/base64Conver#urltobase64
 */
declare function urlToBase64(url: string, mineType?: string, encoderOptions?: number): Promise<string>;
/**
 * @description 彩色图片转灰色图片
 * @param url 彩色图片 `url`
 * @param options 转灰色图片的相关配置
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/base64Conver/base64Conver#convertimagetogray
 */
declare function convertImageToGray(url: string, options?: grayOpt): Promise<string>;

/**
 * @description 判断元素是否存在指定类名
 * @param element 当前类名的元素
 * @param name 类名
 * @returns `boolean`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/class/class#hasclass
 */
declare const hasClass: (element: HTMLElement | Element, name: string) => boolean;
/**
 * @description 向当前元素添加指定类名
 * @param element 当前元素
 * @param name 类名
 * @param extraName 额外类名（可选）
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/class/class#addclass
 */
declare const addClass: (element: HTMLElement | Element, name: string, extraName?: string) => void;
/**
 * @description 删除当前元素的指定类名
 * @param element 当前元素
 * @param name 类名
 * @param extraName 额外类名（可选）
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/class/class#removeclass
 */
declare const removeClass: (element: HTMLElement | Element, name: string, extraName?: string) => void;
/**
 * @description 是否向当前元素添加指定类名
 * @param bool `boolean`
 * @param name 类名
 * @param element 当前元素（可选，如果不填，默认 `document.body` ）
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/class/class#toggleclass
 */
declare const toggleClass: (bool: boolean, name: string, element?: HTMLElement | Element) => void;
/**
 * @description 获取当前元素的所有类名
 * @param element 当前元素
 * @returns `string`、`string[]` 当前元素的所有类名，如果只有一个类名，返回字符串，如果有多个，返回字符串数组
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/class/class#getclass
 */
declare const getClass: (element: HTMLElement | Element) => string | string[];

/**
 * @description 判断对象自身属性中是否具有指定的属性
 * @param  obj 要判断的对象
 * @param  key 指定的属性
 * @return boolean
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/clone/clone#hasownprop
 */
declare function hasOwnProp(obj: object, key: string | number): boolean;
/**
 * @description 浅拷贝/深拷贝
 * @param val 需要拷贝的值
 * @param deep 是否深拷贝（默认 `false` 浅拷贝）
 * @return 拷贝后的值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/clone/clone#clone
 */
declare function clone(val: any, deep?: boolean): any;
/**
 * @description 深拷贝
 * @param val 需要拷贝的值
 * @return 拷贝后的值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/clone/clone#clonedeep
 */
declare function cloneDeep(val: any): any;

/**
 * @description 随机生成颜色
 * @param type 颜色格式 `rgb` 、 `hex` 、 `hsl` ，默认 `rgb`
 * @param num 生成颜色的数量。指定数量，则返回生成颜色数组，数量为 `0` 或不指定，返回生成颜色字符串
 * @returns 生成的颜色
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/color/color#randomcolor
 */
declare const randomColor: (options?: ColorOptions) => string | Array<string>;
/**
 * @description 随机生成渐变色
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/color/color#randomgradient
 */
declare const randomGradient: (options?: GradientOptions) => string;
/**
 * @description `hex` 转 `rgb`
 * @param str `hex`
 * @returns `rgb`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/color/color#hextorgb
 */
declare const hexToRgb: (str: string) => Array<number>;
/**
 * @description `rgb` 转 `hex`
 * @param r  红(R)
 * @param g  绿(G)
 * @param b  蓝(B)
 * @returns `hex`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/color/color#rgbtohex
 */
declare const rgbToHex: (r: number, g: number, b: number) => string;
/**
 * @description 颜色值加深
 * @param color `hex` 格式
 * @param level 色值度
 * @returns 加深后的颜色值，`hex` 格式
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/color/color#darken
 */
declare const darken: (color: string, level: number) => string;
/**
 * @description 颜色值变浅
 * @param color `hex` 格式
 * @param level 色值度
 * @returns 变浅后的颜色值，`hex` 格式
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/color/color#lighten
 */
declare const lighten: (color: string, level: number) => string;

/**
 * @description 将 `Windows` 反斜杠路径转换为斜杠路径
 * @param path 路径地址
 * @returns 斜杠路径
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/convertPath/convertPath#convertpath
 */
declare function convertPath(path: string): string;

/**
 * @description 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02) 的转换 （即 百度 转 谷歌、高德）
 * @param lng 经度
 * @param lat 纬度
 * @returns 经、纬度组成的数组
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/coordtransform/coordtransform#bd09togcj02
 */
declare function bd09togcj02(lng: number, lat: number): Array<number>;
/**
 * @description 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换 （即 谷歌、高德 转 百度）
 * @param lng 经度
 * @param lat 纬度
 * @returns 经、纬度组成的数组
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/coordtransform/coordtransform#gcj02tobd09
 */
declare function gcj02tobd09(lng: number, lat: number): Array<number>;
/**
 * @description WGS-84 转 GCJ-02
 * @param lng 经度
 * @param lat 纬度
 * @returns 经、纬度组成的数组
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/coordtransform/coordtransform#wgs84togcj02
 */
declare function wgs84togcj02(lng: number, lat: number): Array<number>;
/**
 * @description GCJ-02 转换为 WGS-84
 * @param lng 经度
 * @param lat 纬度
 * @returns 经、纬度组成的数组
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/coordtransform/coordtransform#gcj02towgs84
 */
declare function gcj02towgs84(lng: number, lat: number): Array<number>;
/**
 * @description 判断是否是国外（非中国）坐标
 * @see 中国的经纬度范围大约为：经度 `73.66` ~ `135.05`、纬度 `3.86` ~ `53.55` {@link https://zhidao.baidu.com/question/1244677296430089899.html}
 * @param lng 经度
 * @param lat 纬度
 * @returns `boolean`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/coordtransform/coordtransform#out-of-china
 */
declare function out_of_china(lng: number, lat: number): boolean;

/**
 * @description 获取格式化后的当前日期
 * @param format 日期格式化
 * @returns 格式化后的当前日期
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/date/date#dateformat
 */
declare function dateFormat(format: string): string;
/**
 * @description 获取当前是星期几
 * @param prefix 自定义前缀（默认显示`星期`）
 * @returns 星期几
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/date/date#getcurrentweek
 */
declare function getCurrentWeek(prefix?: string): string;
/**
 * @description 获取指定日期月份的总天数
 * @param time 指定日期月份
 * @returns 总天数
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/date/date#monthdays
 */
declare function monthDays(time: Date | string): number;
/**
 * @description 获取从当前年份到开始年份的数组
 * @param start 开始年份
 * @returns 年份数组
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/date/date#createyear
 */
declare function createYear(start: number): Array<number>;
/**
 * @description 获取当前的日期
 * @param options `type` 当前返回的年月日和时分秒的格式 `1`：`年月日`模式（默认） `2`：`-` 连接符 `3`：`/` 连接符
 * @returns 当前的日期对象
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/date/date#getcurrentdate
 */
declare function getCurrentDate(options?: currentDateOpt): currentDateType;
/**
 * @description 将秒转换为时、分、秒
 * @param seconds 要转换的秒数（可以是小数，会向下取整）
 * @param bool 是否补`0`，`true` 为补`0`，`false` 为不补`0`，默认`true`。当时、分、秒小于`10`时，会向前补`0`，如`01:02:09`
 * @returns 时、分、秒
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/date/date#gettime
 */
declare function getTime(seconds: number, bool?: boolean): hmsType;

/**
 * @description 延迟函数
 * @param timeout 延迟时间（毫秒），默认 `20`
 * @returns Promise
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/debounce/debounce#delay
 */
declare const delay: (timeout?: number) => Promise<unknown>;
/**
 * @description 防抖函数
 * @param fn 函数
 * @param timeout 延迟时间（毫秒），默认 `200`
 * @param immediate 是否立即执行，默认 `false`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/debounce/debounce#debounce
 */
declare const debounce: <T extends FunctionArgs>(fn: T, timeout?: number, immediate?: boolean) => () => void;
/**
 * @description 节流函数
 * @param fn 函数
 * @param timeout 延迟时间（毫秒），默认 `1000`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/debounce/debounce#throttle
 */
declare const throttle: <T extends FunctionArgs>(fn: T, timeout?: number) => () => void;

/** 比较两个 Map 对象 */
declare function mapsEqual(a: Map<any, any>, b: Map<any, any>, equalFn: (a: any, b: any, seen?: WeakMap<any, any>) => boolean): boolean;
/** 比较两个 Set 对象 */
declare function setsEqual(a: Set<any>, b: Set<any>): boolean;
/** 深度比较函数，使用 WeakMap 来处理循环引用 */
declare function deepEqual(a: any, b: any, seen?: WeakMap<any, any>): boolean;

/**
 * @description 检测设备类型（`mobile` 返回 `true` ，反之）
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/device/device#devicedetection
 */
declare const deviceDetection: () => boolean;
/**
 * @description 获取浏览器 `型号` 以及 `版本`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/device/device#getbrowserinfo
 */
declare const getBrowserInfo: () => BrowserType;

/**
 * @description 下载在线图片
 * @param url 需要下载的 `url`
 * @param filename 文件名
 * @param mime 类型
 * @param bom `BlobPart`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/download/download#downloadbyonlineurl
 */
declare function downloadByOnlineUrl(url: string, filename: string, mime?: string, bom?: BlobPart): void;
/**
 * @description 基于 `base64` 下载图片
 * @param buf `base64`
 * @param filename 文件名
 * @param mime 类型
 * @param bom `BlobPart`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/download/download#downloadbybase64
 */
declare function downloadByBase64(buf: string, filename: string, mime?: string, bom?: BlobPart): void;
/**
 * @description 根据后台接口文件流下载
 * @param data 文件流
 * @param filename 文件名
 * @param mime 类型
 * @param bom `BlobPart`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/download/download#downloadbydata
 */
declare function downloadByData(data: BlobPart, filename: string, mime?: string, bom?: BlobPart): void;
/**
 * @description 根据文件地址下载文件
 * @param url 文件地址
 * @param fileName 文件名
 * @param target `Target`，默认 `_self`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/download/download#downloadbyurl
 */
declare function downloadByUrl(url: string, fileName: string, target?: string): boolean | undefined;

/**
 * @description 判断两个对象是否相等（深度对比）
 * @param obj 前一个对象
 * @param other 后一个对象
 * @returns `boolean`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/equal/equal#isequalobject
 */
declare function isEqualObject(obj: Record<string, unknown> | undefined, other: Record<string, unknown> | undefined): boolean;
/**
 * @description 判断两个数组是否相等
 * @param arr 前一个数组
 * @param other 后一个数组
 * @returns `boolean`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/equal/equal#isequalarray
 */
declare function isEqualArray(arr: unknown[] | undefined, other: unknown[] | undefined): boolean;
/**
 * @description 判断两者是否相等
 * @param a 前者
 * @param b 后者
 * @returns `boolean`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/equal/equal#isequal
 */
declare function isEqual(a: unknown, b: unknown): boolean;

/**
 * @description 处理 `FormData` 传参，常用于发送表单数据或进行文件上传
 * @param obj `FormData` 参数对象。如：`{ username: 'boy', age: 18, ... }`
 * @returns `FormData`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/formData/formData#formdatahander
 */
declare function formDataHander(obj: any): FormData;
/**
 * @description 处理 `FormData` 传参，比 `formDataHander` 更灵活强大
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/formData/formData#createformdata
 */
declare function createFormData(obj: Record<string, any>, options?: FormDataOptions): FormData;

type SFCWithInstall<T> = T & Plugin;
type SFCInstallWithContext<T> = SFCWithInstall<T> & {
    _context: AppContext | null;
};
/**
 * @description 向组件中添加`install`方法，使其既可以使用`app.component`注册又可以使用`app.use`安装，且无需考虑`TypeScript`类型
 * @param main 主组件（第一个被注册的组件）
 * @param extra 额外组件，对象格式（会按照传入的先后顺序注册）
 * @notice 每个使用`withInstall`的组件都应该有个唯一`name`，以便兼容各种场景。如果要在全局中使用，组件名需传`name`值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/install/install#withinstall
 */
declare const withInstall: <T, E extends Record<string, any>>(main: T, extra?: E | undefined) => SFCWithInstall<T> & E;
/**
 * @description 向组件中添加空的`install`方法。使用场景：不希望组件自动注册，或者想要在控制注册时机和方式时提供灵活性。例如，想要在某些条件满足时才注册组件，或者已经有了注册逻辑的其他实现方式
 * @param component 组件
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/install/install#withnoopinstall
 */
declare const withNoopInstall: <T>(component: T) => SFCWithInstall<T>;
/**
 * @description 向`Vue3`实例的全局属性中添加函数
 * @param fn 函数
 * @param name 函数全局属性名
 */
declare const withInstallFunction: <T>(fn: T, name: string) => SFCInstallWithContext<T>;

/**
 * @description 判断某值是某种类型
 * @param val 需要判断的值
 * @param type 需要判断值的类型
 * @returns boolean
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#is
 */
declare function is(val: unknown, type: string): boolean;
/**
 * @description 是否是对象 `object`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isobject
 */
declare function isObject(val: any): val is Record<any, any>;
/**
 * @description 是否是普通对象。该对象由 `Object` 构造函数创建，或者 `[[Prototype]]` 为 `null`
 * @param val 要检查的值
 * @returns boolean 如果 `val` 为一个普通对象，那么返回 `true` 否则返回 `false`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isplainobject
 */
declare function isPlainObject(val: any): val is Record<any, any>;
/**
 * @description 是否非 `undefined`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isdef
 */
declare function isDef<T = unknown>(val?: T): val is T;
/**
 * @description 是否是 `undefined`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isundef
 */
declare function isUnDef<T = unknown>(val?: T): val is T;
/**
 * @description 是否是 `null`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isnull
 */
declare function isNull(val: unknown): val is null;
/**
 * @description 是否是 `null` 并且是 `undefined`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isnullandundef
 */
declare function isNullAndUnDef(val: unknown): val is null | undefined;
/**
 * @description 是否是 `null` 或者 `undefined`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isnullorundef
 */
declare function isNullOrUnDef(val: unknown): val is null | undefined;
/**
 * @description 是否为空，针对 `数组`、`对象`、`字符串`、`new Map()`、`new Set()` 进行判断
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isempty
 */
declare function isEmpty<T = unknown>(val: T): val is T;
/**
 * @description 是否为空，针对 `数组`、`对象`、`字符串`、`new Map()`、`new Set()`、`null`、`undefined` 进行判断，`null`、`undefined` 直接返回 `true`，也就是直接等于空
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isallempty
 */
declare function isAllEmpty<T = unknown>(val: T): val is T;
/**
 * @description 是否是 `Date` 日期类型
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isdate
 */
declare function isDate(val: unknown): val is Date;
/**
 * @description 是否是闰年
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isleapyear
 */
declare function isLeapYear(val: number): boolean;
/**
 * @description 是否是 `number`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isnumber
 */
declare function isNumber(val: unknown): val is number;
/**
 * @description 是否是 `Promise`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#ispromise
 */
declare function isPromise<T = any>(val: unknown): val is Promise<T>;
/**
 * @description 是否是 `string`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isstring
 */
declare function isString(val: unknown): val is string;
/**
 * @description 是否是 `Function`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isfunction
 */
declare function isFunction(val: unknown): val is Function;
/**
 * @description 是否是 `Boolean`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isboolean
 */
declare function isBoolean(val: unknown): val is boolean;
/**
 * @description 是否是 `RegExp`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isregexp
 */
declare function isRegExp(val: unknown): val is RegExp;
/**
 * @description 是否是 `Array`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isarray
 */
declare function isArray(val: any): val is Array<any>;
/**
 * @description 是否是标准的 `JSON` 格式
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isjson
 */
declare function isJSON(val: any): boolean;
/**
 * @description 是否是 `Window`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#iswindow
 */
declare function isWindow(val: any): val is Window;
/**
 * @description 是否是 `Element`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#iselement
 */
declare function isElement(val: unknown): val is Element;
/**
 * @description 是否是 `Base64`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isbase64
 */
declare const isBase64: (val: string) => boolean;
/**
 * @description 是否是 `hex`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#ishex
 */
declare const isHex: (color: string) => boolean;
/**
 * @description 是否是 `rgb`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isrgb
 */
declare const isRgb: (color: string) => boolean;
/**
 * @description 是否是 `rgba`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isrgba
 */
declare const isRgba: (color: string) => boolean;
/**
 * @description 是否处于服务端，非浏览器环境（根据是否存在`window`来判断）
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isserver
 */
declare const isServer: boolean;
/**
 * @description 是否处于浏览器环境（根据是否存在`window`来判断）
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isclient
 */
declare const isClient: boolean;
/**
 * @description 是否处于浏览器环境（根据是否存在`document`来判断）
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isbrowser
 */
declare const isBrowser: boolean;
/**
 * @description `url` 链接正则
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isurl
 */
declare function isUrl(value: string): boolean;
/**
 * @description 手机号码正则
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isphone
 */
declare function isPhone(value: any): boolean;
/**
 * @description 邮箱正则
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isemail
 */
declare function isEmail(value: string): boolean;
/**
 * @description `QQ` 正则
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isqq
 */
declare function isQQ(value: number): boolean;
/**
 * @description 是否是中国大陆邮政编码（共6位，且不能以0开头）
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#ispostcode
 */
declare function isPostCode(value: number): boolean;
/**
 * @description 1. 校验是否包含中文或指定的 `unicode` 字符；2. 校验是否全是中文或指定的 `unicode` 字符（包括常用中文标点符号，更多使用方法请看下方链接)
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#hascnchars
 */
declare function hasCNChars(value: any, options?: isParams): boolean;
/**
 * @description 是否是小写字母
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#islowercase
 */
declare function isLowerCase(value: string): boolean;
/**
 * @description 是否是大写字母
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isuppercase
 */
declare function isUpperCase(value: string): boolean;
/**
 * @description 是否是大小写字母
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isalphabets
 */
declare function isAlphabets(value: string): boolean;
/**
 * @description 检测字符串是否有空格。true：有空格，false：无空格
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#isexistspace
 */
declare function isExistSpace(value: string): boolean;
/**
 * @description 是否是 `HTML`，通过判断传入字符是否包含类似`HTML`标签的结构，适用于基本场景
 * @see 如需判断传入字符是否符合 `W3C HTML` 规范可以用 https://www.npmjs.com/package/html-validate
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/is/is#ishtml
 */
declare function isHtml(value: string): boolean;

/**
 * @description 创建超链接
 * @param href 超链接地址
 * @param target `Target`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/link/link#openlink
 */
declare const openLink: (href: string, target?: Target) => void;

/**
 * @description 求数字类型组成数组中的最大值
 * @param list 数字类型组成数组
 * @returns 最大值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/math/math#max
 */
declare const max: (list: Array<number>) => number;
/**
 * @description 求数字类型组成数组中的最小值
 * @param list 数字类型组成数组
 * @returns 最小值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/math/math#min
 */
declare const min: (list: Array<number>) => number;
/**
 * @description 求数字类型组成数组中的和
 * @param list 数字类型组成数组
 * @returns 求和值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/math/math#sum
 */
declare const sum: (list: Array<number>) => number;
/**
 * @description 求数字类型组成数组中的平均值
 * @param list 数字类型组成数组
 * @returns 平均值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/math/math#average
 */
declare const average: (list: Array<number>) => number;
/**
 * @description 将阿拉伯数字翻译成中文数字
 * @param num 阿拉伯数字
 * @returns 中文数字
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/math/math#numbertochinese
 */
declare const numberToChinese: (num: number | string) => string;
/**
 * @description 判断数值是否超过 `js` 最大值
 * @param num 需要判断的数值
 * @returns boolean
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/math/math#exceedmathmax
 */
declare function exceedMathMax(num: number): boolean;
/**
 * @description 两个数值的加法运算
 * @param num1
 * @param num2
 * @param decimal 保留的小数位数，为 `0` 时不进行任何处理
 * @returns 加法运算后的值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/math/math#addition
 */
declare function addition(num1: number, num2: number, decimal?: number): number;
/**
 * @description 两个数值的减法运算
 * @param num1
 * @param num2
 * @param decimal 保留的小数位数，为 `0` 时不进行任何处理
 * @returns 减法运算后的值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/math/math#subtraction
 */
declare function subtraction(num1: number, num2: number, decimal?: number): number;
/**
 * @description 两个数值的乘法运算
 * @param num1
 * @param num2
 * @param decimal 保留的小数位数，为 `0` 时不进行任何处理
 * @returns 乘法运算后的值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/math/math#multiplication
 */
declare function multiplication(num1: number, num2: number, decimal?: number): number;
/**
 * @description 两个数值的除法运算
 * @param num1
 * @param num2
 * @param decimal 保留的小数位数，为 `0` 时不进行任何处理
 * @returns 除法运算后的值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/math/math#divisionoperation
 */
declare function divisionOperation(num1: number, num2: number, decimal?: number): number;
/**
 * @description 将字节单位智能转化成 `Bytes` 、 `KB` 、 `MB` 、 `GB` 、 `TB` 、 `PB` 、 `EB` 、 `ZB` 、 `YB` 其中的一种
 * @param byte 字节
 * @param digits 四舍五入保留几位小数（默认四舍五入保留两位小数）
 * @returns 智能转化字节单位后的值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/math/math#formatbytes
 */
declare const formatBytes: (byte: number, digits?: number) => string;

/**
 * @description 横线转驼峰命名
 * @param str 字符串
 * @returns 横线转驼峰命名后的字符串
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/nameTransform/nameTransform#namecamelize
 */
declare const nameCamelize: (str: string) => string;
/**
 * @description 驼峰命名转横线命名：拆分字符串，使用 `-` 相连，并且全部转换为小写
 * @param str 字符串
 * @returns 驼峰命名转横线命名后的字符串
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/nameTransform/nameTransform#namehyphenate
 */
declare const nameHyphenate: (str: string) => string;

/**
 * @description 从对象中删除指定的属性
 * @param obj 需要删除属性的对象
 * @param props 指定要删除的属性，可以是单个属性名(字符串)或一个属性名字符串数组
 * @returns 返回修改后的新对象，不会修改原始对象
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/object/object#delObjectProperty
 */
declare const delObjectProperty: <T extends Record<string, any>, K extends keyof T>(obj: T, props: K | K[]) => Omit<T, K>;

/**
 * @description 获取当前页面在加载和使用期间发生各种事件的性能计时信息
 * @returns `Performance`（单位：秒）
 * @see https://developer.mozilla.org/zh-CN/docs/Web/API/PerformanceTiming
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/performance/performance#getperformance
 */
declare function getPerformance(): Promise<Performance>;

/**
 * @description 去掉字符串左边空格
 * @param str 字符串
 * @returns 去掉左边空格后的字符串
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/space/space#removeleftspace
 */
declare const removeLeftSpace: (str: string) => string;
/**
 * @description 去掉字符串右边空格
 * @param str 字符串
 * @returns 去掉右边空格后的字符串
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/space/space#removerightspace
 */
declare const removeRightSpace: (str: string) => string;
/**
 * @description 去掉字符串左右两边空格
 * @param str 字符串
 * @returns 去掉左右两边空格后的字符串
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/space/space#removebothsidesspace
 */
declare const removeBothSidesSpace: (str: string) => string;
/**
 * @description 去掉字符串全部空格
 * @param str 字符串
 * @returns 去掉全部空格后的字符串
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/space/space#removeallspace
 */
declare const removeAllSpace: (str: string) => string;

declare class sessionStorageProxy implements ProxyStorage {
    protected storage: Storage;
    constructor(storageModel: any);
    /**
     * @description 储存对应键名的 `Storage` 对象
     * @param k 键名
     * @param v 键值
     */
    setItem<T>(k: string, v: T): void;
    /**
     * @description 获取对应键名的 `Storage` 对象
     * @param k 键名
     * @returns 对应键名的 `Storage` 对象
     */
    getItem<T>(k: string): T;
    /**
     * @description 删除对应键名的 `Storage` 对象
     * @param k 键名
     */
    removeItem(k: string): void;
    /**
     * @description 删除此域的所有 `Storage` 对象
     */
    clear(): void;
}
declare class localStorageProxy extends sessionStorageProxy implements ProxyStorage {
    constructor(localStorage: any);
}
/**
 * @description 操作本地 `localStorage`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/storage/storage#storagelocal
 */
declare const storageLocal: () => localStorageProxy;
/**
 * @description 操作本地 `sessionStorage`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/storage/storage#storagesession
 */
declare const storageSession: () => sessionStorageProxy;

/**
 * @description 截取指定字符前面的值
 * @param val 要截取的值
 * @param character 指定字符
 * @returns 截取后的值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/substring/substring#subbefore
 */
declare function subBefore(val: string, character: string): string;
/**
 * @description 截取指定字符后面的值
 * @param val 要截取的值
 * @param character 指定字符
 * @returns 截取后的值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/substring/substring#subafter
 */
declare function subAfter(val: string, character: string): string;
/**
 * @description 截取指定字符两边的值
 * @param val 要截取的值
 * @param character 指定字符
 * @returns 截取后的值，数组格式，左边返回指定字符前面的值，后边返回指定字符后面的值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/substring/substring#subbothsides
 */
declare function subBothSides(val: string, character: string): Array<string>;
/**
 * @description 截取指定两个字符之间的值
 * @param val 要截取的值
 * @param before 前一个指定字符
 * @param after 后一个指定字符
 * @returns 截取后的值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/substring/substring#subbetween
 */
declare function subBetween(val: string, before: string, after: string): string;
/**
 * @description 截取字符并追加省略号（常用场景：`echarts`）
 * @param str 要截取的值
 * @param len 要保留的位数（默认三位）
 * @returns 截取字符并追加省略号后的值
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/substring/substring#subtextaddellipsis
 */
declare function subTextAddEllipsis(str: string | number, len?: number): string;
/**
 * @description 将数字拆分为单个数字组成的数组
 * @param number 要拆分的数字
 * @returns 拆分的单个数字集合
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/substring/substring#splitnum
 */
declare function splitNum(number: number): Array<number> | string;
/**
 * @description 使用指定符号对指定的文字进行隐藏，默认使用 `*` 符号
 * @param text 文字
 * @param index 指定的文字索引或索引区间
 * @param symbol 指定的符号
 * @returns 隐藏后的文字
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/substring/substring#hidetextatindex
 */
declare function hideTextAtIndex(text: string | number, index: number | Array<number | unknown> | HideTextIndex | Array<HideTextIndex>, symbol?: string): string;

/**
 * @description 提取菜单树中的每一项 `uniqueId`
 * @param tree 树
 * @returns 每一项 `uniqueId` 组成的数组
 */
declare const extractPathList: (tree: any[]) => any;
/**
 * @description 如果父级下 `children` 的 `length` 为 `1`，删除 `children` 并自动组建唯一 `uniqueId`
 * @param tree 树
 * @param pathList 每一项的 `id` 组成的数组
 * @returns 组件唯一 `uniqueId` 后的树
 */
declare const deleteChildren: (tree: any[], pathList?: never[]) => any;
/**
 * @description 创建层级关系
 * @param tree 树
 * @param pathList 每一项的 `id` 组成的数组
 * @returns 创建层级关系后的树
 */
declare const buildHierarchyTree: (tree: any[], pathList?: never[]) => any;
/**
 * @description 广度优先遍历，根据唯一 `uniqueId` 找当前节点信息
 * @param tree 树
 * @param uniqueId 唯一 `uniqueId`
 * @returns 当前节点信息
 */
declare const getNodeByUniqueId: (tree: any[], uniqueId: number | string) => any;
/**
 * @description 向当前唯一 `uniqueId` 节点中追加字段
 * @param tree 树
 * @param uniqueId 唯一 `uniqueId`
 * @param fields 需要追加的字段
 * @returns 追加字段后的树
 */
declare const appendFieldByUniqueId: (tree: any[], uniqueId: number | string, fields: object) => any;
/**
 * @description 构造树型结构数据
 * @param data 数据源
 * @param id `id` 字段 默认 `id`
 * @param parentId 父节点字段，默认 `parentId`
 * @param children 子节点字段，默认 `children`
 * @returns 追加字段后的树
 */
declare const handleTree: (data: any[], id?: string, parentId?: string, children?: string) => any;

/**
 * @description 获取浏览器当前的 `location` 信息
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/url/url#getlocation
 */
declare function getLocation(): Location;
/**
 * @description 提取浏览器 `url` 中所有参数
 * @param url 超链接地址
 * @returns 所有参数组成的对象
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/url/url#getquerymap
 */
declare function getQueryMap(url: string): object;

/**
 * @description 生成 `32` 位 `uuid`
 * @returns `32` 位 `uuid`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/uuid/uuid#builduuid
 */
declare const buildUUID: () => string;
/**
 * @description 生成自定义前缀的 `uuid`
 * @param prefix 自定义前缀
 * @returns 自定义前缀的 `uuid`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/uuid/uuid#buildprefixuuid
 */
declare const buildPrefixUUID: (prefix?: string) => string;
/**
 * @description 生成指定长度和基数的 `uuid`
 * @param len 指定长度
 * @param radix 指定基数
 * @param prefix 自定义前缀
 * @returns 指定长度和基数的 `uuid`
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/uuid/uuid#uuid
 */
declare const uuid: (len?: number, radix?: number, prefix?: string) => string;

/**
 * @description 判断一个数组（这里简称为母体）中是否包含了另一个由基本数据类型组成的数组（这里简称为子体）中的全部元素
 * @param c 子体
 * @param m 母体
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/array/array#isincludeallchildren
 */
declare function isIncludeAllChildren(c: Array<string | number | unknown>, m: Array<unknown>): boolean;
/**
 * @description 获取由基本数据类型组成的数组交集
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/array/array#intersection
 */
declare const intersection: (...rest: any[]) => any[];
/**
 * @description 数组中两个元素互换顺序（内部使用 `splice` 会改变原数组）
 * @param arr 数组
 * @param fIndex 要换的元素索引
 * @param sIndex 被换的元素索引
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/array/array#swaporder
 */
declare function swapOrder(arr: any[], fIndex: number, sIndex: number): any[];
/**
 * @description 从数组中获取指定 `key` 组成的新数组，会去重也会去除不存在的值
 * @param arr 数组
 * @param key 指定的 `key`
 * @param duplicates 是否去重，默认 `true` 去重
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/array/array#getkeylist
 */
declare function getKeyList(arr: any, key: string, duplicates?: boolean): any[];
/**
 * @description 将一个总数随机分配到指定份数的数组中，并按指定顺序返回数组（内部使用`Fisher-Yates`（也称为`Knuth`）洗牌算法）
 * @param total 需要被分配的总数
 * @param parts 将总数分成多少份
 * @param options 额外参数对象
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/array/array#randomdivide
 */
declare function randomDivide(total: number, parts: number, options?: DivideOptions): number[];
/**
 * @description 检测一个数组是否包含另一个数组中所有的值（内部使用`new Set`性能更好。该方法只针对基本数据类型，需要更复杂的场景可以用`arrayAllExistDeep`）
 * @param array 初始数组
 * @param checkArray 与初始数组做对比的数组
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/array/array#arrayallexist
 */
declare const arrayAllExist: (array: Array<any>, checkArray: Array<any>) => boolean;
/**
 * @description 检测一个数组是否包含另一个数组中所有的值（深度对比）
 * @param array 初始数组
 * @param checkArray 与初始数组做对比的数组
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/array/array#arrayallexistdeep
 */
declare const arrayAllExistDeep: (array: Array<any>, checkArray: Array<any>) => boolean;
/**
 * @description 检测一个数组是否包含另一个数组中任意一个值（内部使用`new Set`性能更好。该方法只针对基本数据类型，需要更复杂的场景可以用`arrayAnyExistDeep`）
 * @param array 初始数组
 * @param checkArray 与初始数组做对比的数组
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/array/array#arrayanyexist
 */
declare const arrayAnyExist: (array: Array<any>, checkArray: Array<any>) => boolean;
/**
 * @description 检测一个数组是否包含另一个数组中任意一个值（深度对比）
 * @param array 初始数组
 * @param checkArray 与初始数组做对比的数组
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/array/array#arrayanyexistdeep
 */
declare function arrayAnyExistDeep(array: Array<any>, checkArray: Array<any>): boolean;
/**
 * @description 提取对象数组中的任意字段，返回一个新的数组
 * @see 详细文档 https://pure-admin-utils.netlify.app/utils/array/array#extractfields
 */
declare function extractFields<T, K extends keyof T>(array: T[], ...keys: K[]): Pick<T, K>[];

declare function entries<T>(obj: Recordable<T>): [string, T][];
declare function useAttrs(params?: AttrParams): RefValue<Recordable> | {};

/**
 * @description `tsx` 中加载动态组件
 * @returns 动态组件
 */
declare const useDynamicComponent: (component: string) => Component;

/** 将文本复制到剪贴板 */
declare function copyTextToClipboard(input: string, { target }?: {
    target?: HTMLElement;
}): boolean;
/**
 * @description 拷贝文本
 * @param defaultValue 当要拷贝的文本值为空时，拷贝的值，默认不设置
 * @see 详细文档 https://pure-admin-utils.netlify.app/hooks/useCopyToClipboard/useCopyToClipboard
 */
declare const useCopyToClipboard: (defaultValue?: string) => {
    /** 拷贝后的文本值 */
    clipboardValue: ShallowRef<string>;
    /** 是否拷贝成功。`true`代表拷贝成功，`false`代表拷贝失败 */
    copied: ShallowRef<boolean>;
    /** 更新要拷贝的文本值 */
    update: (value: string | RefValue<string>) => void;
};

/**
 * @description 检测网页整体是否处于暗色（`dark`）主题，它是响应式的
 * @see 详细文档 https://pure-admin-utils.netlify.app/hooks/useDark/useDark
 */
declare const useDark: (options?: DarkOptions) => {
    /** 当前整个网页是否处于暗色(`dark`)主题 */
    isDark: ShallowRef<boolean>;
    /** `dark`和非`dark`主题间相互切换 */
    toggleDark: () => void;
};

/**
 * @description 使元素可拖动
 * @param targetRef 要拖动的元素
 * @param dragRef 拖动的地方
 * @param args 额外的可选参数
 * @see 详细文档 https://pure-admin-utils.netlify.app/hooks/useDraggable/useDraggable
 */
declare const useDraggable: (targetRef: ElementRef<HTMLElement | undefined> | string, dragRef: ElementRef<HTMLElement | undefined> | string, args?: ArgsDraggable) => {
    /** 是否已经开启拖动功能（响应式）。`true`代表已开启拖动功能，`false`代表关闭了拖动功能 */
    draggable: Ref<boolean>;
    /** 是否正在拖动中（响应式）。`true`代表正在拖动，`false`代表没有拖动 */
    dragging: Ref<boolean>;
    /** 响应式对象`transform`：包含当前被拖动元素的`offsetX`、`offsetY`信息 */
    transform: {
        offsetX: number;
        offsetY: number;
    };
    /** 初始化开启拖动功能 */
    init: () => void;
    /** 开启拖动功能。常用于当关闭拖动时，想再次开启拖动并在当前所处位置继续拖动 */
    open: () => void;
    /** 关闭拖动功能 */
    close: () => void;
    /** 将拖动元素恢复到初始位置 */
    reset: () => void;
};

/**
 * @description 渲染 `echarts`
 * @param elRef `ref`
 * @param options `EchartOptions`
 * @see 详细文档 https://pure-admin-utils.netlify.app/hooks/useECharts/useECharts
 */
declare const useECharts: (elRef: ElementRef<HTMLDivElement>, options?: EchartOptions) => {
    /** 返回的`echarts`，功能同 [echarts](https://echarts.apache.org/zh/api.html#echarts) */
    echarts: echarts_types_dist_echarts.ECharts;
    /** 根据配置项渲染`ECharts`，功能同 [setOption](https://echarts.apache.org/zh/option.html) */
    setOptions: (options: UtilsEChartsOption, ...params: OptionsParams[]) => void;
    /** 获取通过 [echarts.init](https://echarts.apache.org/zh/api.html#echarts.init) 创建的实例，功能同 [echartsInstance](https://echarts.apache.org/zh/api.html#echartsInstance) */
    getInstance: () => echarts_types_dist_echarts.ECharts | null;
    /** 显示加载动画 */
    showLoading: (params: LoadOpts) => void;
    /** 隐藏加载动画 */
    hideLoading: () => void;
    /** 清空当前`ECharts`实例，会移除实例中所有的组件和图表 */
    clear: () => void;
    /** 改变`ECharts`图表尺寸，使其适应容器 */
    resize: () => void;
    /** 获取绑定到`echarts`的 [globalProperties](https://cn.vuejs.org/api/application.html#app-config-globalproperties) */
    getGlobalProperties: () => EchartGlobalProperties;
    /** 获取`ECharts`实例容器的`Dom`节点 */
    getDom: () => HTMLCanvasElement | HTMLDivElement;
    /** 获取`ECharts`实例容器的宽度 */
    getWidth: () => number;
    /** 获取`ECharts`实例容器的高度 */
    getHeight: () => number;
    /** 获取当前`ECharts`实例中维护的`option`对象 */
    getOption: () => echarts_types_dist_echarts.EChartsCoreOption;
    /** 此接口用于，在大数据量（百万以上）的渲染场景，分片加载数据和增量渲染 */
    appendData: (opts: AppendDataOpts) => void;
    /** 导出图表图片，返回一个`base64`的`URL`，可以设置为`Image`的`src` */
    getDataURL: (opts: DataURL) => string;
    /** 导出联动的图表图片，返回一个`base64`的`url`，可以设置为`Image`的`src`。导出图片中每个图表的相对位置跟容器的相对位置有关 */
    getConnectedDataURL: (opts: DataURL) => string;
    /**
     * @description 给`x`、`y`轴添加`Tooltip`文字提示
     * @param type `x`代表`x轴`   `y`代表`y轴`   `true`(默认)代表`x、y轴` （该属性生效的前提是将 `xAxis` 或者 `yAxis` 添加`triggerEvent: true`属性，用到哪个添加哪个）
     */
    addTooltip: (type: ToolTipType) => void;
};

/**
 * @description 获取已经注册的全局属性对象 [globalProperties](https://cn.vuejs.org/api/application.html#app-config-globalproperties)
 * @see 详细文档 https://pure-admin-utils.netlify.app/hooks/useGlobal/useGlobal
 */
declare function useGlobal<T>(): T;

/**
 * @description 动态地将静态资源（`CSS`样式表或`JavaScript`脚本）加载到网页中
 * @param destroy 是否在生命周期结束后移除标签，默认 `true`
 * @see 详细文档 https://pure-admin-utils.netlify.app/hooks/useLoader/useLoader
 */
declare function useLoader(destroy?: boolean): {
    /** 动态加载`CSS`样式表 */
    loadCss: (opts: OptionsScript | OptionsScript[]) => Promise<LoadResult[]> | undefined;
    /** 动态加载`JavaScript`脚本 */
    loadScript: (opts: OptionsScript | OptionsScript[]) => Promise<LoadResult[]> | undefined;
};

/**
 * @description 监听元素尺寸的变化，内置自定义防抖时长
 * @see 详细文档 https://pure-admin-utils.netlify.app/hooks/useResizeObserver/useResizeObserver
 */
declare function useResizeObserver(target: ElementRef<HTMLDivElement> | string | (ElementRef<HTMLDivElement> | string)[], callback: ResizeObserverCallback, options?: UseResizeObserverOptions): {
    /** 停止监听 */
    stop: () => void;
    /** 重新启用监听，常用于停止监听后重新启用监听 */
    restart: () => void;
};

/** 二次方缓动（缓入、缓出）*/
declare const easeInOutQuad: ({ timeElapsed, startValue, byValue, duration }: EaseInOutQuadOptions) => number;
/**
 * @description 使元素可自定义缓动
 * @optionsParam `el`滚动对象的实例，必填
 * @optionsParam `to`滚动到某个位置，必填
 * @optionsParam `directions`滚动方向，必填
 * @optionsParam `duration`滚动时长，单位毫秒。默认`0`，没有缓动效果，可选
 * @optionsParam `callback`滚动完毕的回调，可选
 * @see 详细文档 https://pure-admin-utils.netlify.app/hooks/useScrollTo/useScrollTo
 */
declare const useScrollTo: (options: ScrollOptions) => {
    /** 开始滚动 */
    start: () => void;
    /** 暂停滚动 */
    stop: () => void;
};

/**
 * @description 创建水印
 * @see 详细文档 https://pure-admin-utils.netlify.app/hooks/useWatermark/useWatermark
 */
declare const useWatermark: (appendEl?: ElementRef<HTMLElement | null>) => {
    /** 销毁水印 */
    clear: () => void;
    /** 根据自定义配置项添加水印 */
    setWatermark: (str: string, attr?: attr | undefined) => void;
};

/**
 * @description `Fisher-Yates`（也称为`Knuth`）洗牌算法。以均匀的分布方式，随机打乱数组的顺序。它的效果在数学上是无偏的，确保了每个元素有相同的概率出现在数组的任何位置
 */
declare function shuffleArray(array: number[]): number[];

export { AmountOpt, AppendDataOpts, ArgsDraggable, ArgumentsType, Arrayable, AttrParams, Awaitable, BrowserType, ColorOptions, ColorType, ComponentElRef, ComponentRef, ComputedRefValue, Constructor, DarkOptions, DataURL, DeepMerge, DeviceType, DivideOptions, EaseInOutQuadOptions, EchartGlobalProperties, EchartOptions, ElRef, ElementOf, ElementRef, EventParams, Fn, FormDataOptions, FunctionArgs, GlobalUtilsPropertiesApi, GradientOptions, HandleFileType, HideTextIndex, LoadOpts, LoadResult, MergeInsertions, MouseEvent, Nullable, OptionsParams, OptionsScript, OrderType, Performance, ProxyStorage, ReadonlyRecordable, Recordable, RefType, RefValue, SFCInstallWithContext, SFCWithInstall, ScrollOptions, Target, Theme, TimeoutHandle, ToolTipType, UnionToIntersection, UseResizeObserverOptions, UtilsEChartsOption, addClass, addZero, addition, allowMouseEvent, appendFieldByUniqueId, arrayAllExist, arrayAllExistDeep, arrayAnyExist, arrayAnyExistDeep, attr, average, banMouseEvent, bd09togcj02, buildHierarchyTree, buildPrefixUUID, buildUUID, centsToDollars, clone, cloneDeep, convertImageToGray, convertPath, copyTextToClipboard, createFormData, createYear, currentDateOpt, currentDateType, darken, dataURLtoBlob, dateFormat, dateType, debounce, deepEqual, delObjectProperty, delay, deleteChildren, deviceDetection, divisionOperation, dollarsToCents, downloadByBase64, downloadByData, downloadByOnlineUrl, downloadByUrl, easeInOutQuad, entries, exceedMathMax, extractFields, extractPathList, formDataHander, formatBytes, gcj02tobd09, gcj02towgs84, getBrowserInfo, getClass, getCurrentDate, getCurrentWeek, getDecimalPlaces, getKeyList, getLocation, getNodeByUniqueId, getPackageSize, getPerformance, getQueryMap, getTime, gradientType, grayOpt, handleTree, hasCNChars, hasClass, hasOwnProp, hexToRgb, hideTextAtIndex, hmsType, intersection, is, isAllEmpty, isAlphabets, isArray, isBase64, isBoolean, isBrowser, isClient, isDate, isDef, isElement, isEmail, isEmpty, isEqual, isEqualArray, isEqualObject, isExistSpace, isFunction, isHex, isHtml, isIncludeAllChildren, isJSON, isLeapYear, isLowerCase, isNull, isNullAndUnDef, isNullOrUnDef, isNumber, isObject, isParams, isPhone, isPlainObject, isPostCode, isPromise, isQQ, isRegExp, isRgb, isRgba, isServer, isString, isUnDef, isUpperCase, isUrl, isWindow, lighten, mapsEqual, max, min, monthDays, multiplication, nameCamelize, nameHyphenate, numberToChinese, openLink, out_of_china, packageOpt, priceToThousands, priceUppercase, randomColor, randomDivide, randomGradient, removeAllSpace, removeBothSidesSpace, removeClass, removeLeftSpace, removeRightSpace, rgbToHex, setsEqual, shuffleArray, splitNum, storageLocal, storageSession, subAfter, subBefore, subBetween, subBothSides, subTextAddEllipsis, subtraction, sum, swapOrder, throttle, toggleClass, urlToBase64, useAttrs, useCopyToClipboard, useDark, useDraggable, useDynamicComponent, useECharts, useGlobal, useLoader, useResizeObserver, useScrollTo, useWatermark, uuid, wgs84togcj02, withInstall, withInstallFunction, withNoopInstall };
