import { h, Component } from 'preact';
import GraphModel from '../../model/GraphModel';
import BaseNodeModel from '../../model/node/BaseNodeModel';
import { StepDrag } from '../../util/drag';
import { IReactionDisposer } from '../../util/mobx';
declare type IProps = {
    model: BaseNodeModel;
    graphModel: GraphModel;
};
declare type IState = {
    isDragging?: boolean;
};
export default abstract class BaseNode extends Component<IProps, IState> {
    t: any;
    moveOffset: {
        x: number;
        y: number;
    };
    static getModel(defaultModel: any): any;
    stepDrag: StepDrag;
    contextMenuTime: number;
    mouseUpDrag: boolean;
    startTime: number;
    clickTimer: number;
    modelDisposer: IReactionDisposer;
    constructor(props: any);
    componentWillUnmount(): void;
    abstract getShape(): any;
    getAnchorShape(anchorData: any): h.JSX.Element;
    getAnchors(): h.JSX.Element[];
    getRotateControl(): h.JSX.Element;
    getText(): "" | h.JSX.Element;
    getStateClassName(): string;
    onDragStart: ({ event: { clientX, clientY } }: {
        event: {
            clientX: any;
            clientY: any;
        };
    }) => void;
    onDragging: ({ event }: {
        event: any;
    }) => void;
    onDragEnd: () => void;
    handleMouseUp: () => void;
    handleClick: (e: MouseEvent) => void;
    handleContextMenu: (ev: MouseEvent) => void;
    handleMouseDown: (ev: MouseEvent) => void;
    setHoverON: (ev: any) => void;
    setHoverOFF: (ev: any) => void;
    onMouseOut: (ev: any) => void;
    /**
     *  @overridable 支持重写, 节点置顶，可以被某些不需要置顶的节点重写，如group节点。
     */
    toFront(): void;
    render(): any;
}
export {};
