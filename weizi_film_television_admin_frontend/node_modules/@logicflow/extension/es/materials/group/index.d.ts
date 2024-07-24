import LogicFlow, { BaseEdgeModel, BaseNodeModel, EdgeConfig, NodeData } from '@logicflow/core';
import GroupNode from './GroupNode';
declare type BaseNodeId = string;
declare type GroupId = string;
declare type Bounds = {
    x1: number;
    y1: number;
    x2: number;
    y2: number;
};
declare class Group {
    static pluginName: string;
    lf: LogicFlow;
    topGroupZIndex: number;
    activeGroup: any;
    nodeGroupMap: Map<BaseNodeId, GroupId>;
    constructor({ lf }: {
        lf: any;
    });
    /**
     * 创建一个Group类型节点内部的所有子节点的副本
     * 并且在遍历所有nodes的过程中顺便拿到所有edges（只在Group范围的edges）
     */
    createAllChildNodes(nodeIdMap: Record<string, string>, children: Set<string>, current: BaseNodeModel, distance: number): {
        nodesArray: BaseNodeModel[];
        edgesArray: EdgeConfig[];
    };
    createEdgeModel(edge: EdgeConfig, nodeIdMap: Record<string, string>, distance: number): BaseEdgeModel;
    /**
     * 获取一个节点内部所有的子节点，包裹分组的子节点
     */
    getNodeAllChild(model: any): any[];
    graphRendered: (data: any) => void;
    appendNodeToGroup: ({ data }: {
        data: any;
    }) => void;
    deleteGroupChild: ({ data }: {
        data: any;
    }) => void;
    setActiveGroup: ({ data }: {
        data: any;
    }) => void;
    findNodeAndChildMaxZIndex: (nodeModel: BaseNodeModel) => number;
    checkAndCorrectTopGroupZIndex: (nodes: NodeData[]) => void;
    /**
     * 1. 分组节点默认在普通节点下面。
     * 2. 分组节点被选中后，会将分组节点以及其内部的其他分组节点放到其余分组节点的上面。
     * 3. 分组节点取消选中后，不会将分组节点重置为原来的高度。
     * 4. 由于LogicFlow核心目标是支持用户手动绘制流程图，所以不考虑一张流程图超过1000个分组节点的情况。
     */
    nodeSelected: ({ data, isMultiple, isSelected }: {
        data: any;
        isMultiple: any;
        isSelected: any;
    }) => void;
    toFrontGroup: (model: any) => void;
    /**
     * 获取自定位置其所属分组
     * 当分组重合时，优先返回最上层的分组
     */
    getGroup(bounds: Bounds, nodeData: BaseNodeModel): BaseNodeModel | undefined;
    /**
     * 获取某个节点所属的groupModel
     */
    getNodeGroup(nodeId: any): BaseNodeModel;
}
export { Group, GroupNode, };
