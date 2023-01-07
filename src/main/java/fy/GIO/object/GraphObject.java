package fy.GIO.object;

import fy.GD.basic.GraphNode;
import fy.GD.mgraph.MethodPDG;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GraphObject {

    public boolean directed;
    public boolean multi_graph;
    public String commit_id;
    public String file_name;
    public String method_name;
    public String method_range;
    public int vertex_count;
    public int edge_count;
    public int slice_num1;
    public int slice_num2;
    public List<NodeInfo> nodes = new ArrayList<>();
    public List<EdgeInfo> links = new ArrayList<>();

    public GraphObject(MethodPDG graph) {
        // graph
        this.directed = true;
        this.multi_graph = true;
        this.commit_id = graph.commitId;
        this.file_name = graph.simpleName;
        this.method_name = graph.n.getDeclarationAsString();
        this.method_range = graph.n.getRange().get().toString();
        this.vertex_count = graph.vertexCount();
        this.edge_count = graph.edgeCount();
        this.slice_num1 = graph.slice_num1;
        this.slice_num2 = graph.slice_num2;
        // nodes
        Map<GraphNode, Integer> nodeIdMap = new LinkedHashMap<>();
        AtomicInteger nodeCount = new AtomicInteger();
        graph.copyVertexSet().forEach(node -> {
            int id = nodeCount.getAndIncrement();
            nodeIdMap.put(node, id);
            NodeInfo nodeInfo = new NodeInfo(node);
            nodeInfo.id = id;
            this.nodes.add(nodeInfo);
        });
        // edges
        AtomicInteger controlFlowEdgeCount = new AtomicInteger();
        graph.copyEdgeSet().forEach(edge -> {
            EdgeInfo edgeInfo = new EdgeInfo();
            edgeInfo.id = controlFlowEdgeCount.getAndIncrement();
            edgeInfo.source = nodeIdMap.get(edge.source);
            edgeInfo.target = nodeIdMap.get(edge.target);
            edgeInfo.label = "control_flow";
            edgeInfo.slices = edge.label.getSliceInfo();
            this.links.add(edgeInfo);
        });
        AtomicInteger dataFlowEdgeCount = new AtomicInteger();
        graph.dataFlowEdges.forEach(edge -> {
            EdgeInfo edgeInfo = new EdgeInfo();
            edgeInfo.id = dataFlowEdgeCount.getAndIncrement();
            edgeInfo.source = nodeIdMap.get(edge.source);
            edgeInfo.target = nodeIdMap.get(edge.target);
            edgeInfo.label = "data_flow";
            edgeInfo.slices = edge.label.getSliceInfo();
            this.links.add(edgeInfo);
        });
    }

    public static class NodeInfo{
        public int id;
        public int line;
        public String label;
        public List<Integer> slices;

        public NodeInfo(GraphNode node) {
            this.line = node.getCodeLineNum();
            this.label = node.getSimplifyCodeStr();
            this.slices = node.getSliceInfo();
        }

    }

    public static class EdgeInfo {
        public int id;
        public int source;
        public int target;
        public String label;
        public List<Integer> slices;
    }
}
