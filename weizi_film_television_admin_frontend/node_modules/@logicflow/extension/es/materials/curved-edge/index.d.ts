import { PolylineEdge, PolylineEdgeModel } from '@logicflow/core';
declare function getCurvedEdgePath(points: number[][], radius: number): string;
declare class CurvedEdge extends PolylineEdge {
    getEdge(): import("preact").VNode<any>;
}
declare class CurvedEdgeModel extends PolylineEdgeModel {
}
declare const defaultCurvedEdge: {
    type: string;
    view: typeof CurvedEdge;
    model: typeof CurvedEdgeModel;
};
export default defaultCurvedEdge;
export { CurvedEdge, CurvedEdgeModel, getCurvedEdgePath };
