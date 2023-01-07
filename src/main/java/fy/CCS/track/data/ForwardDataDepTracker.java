package fy.CCS.track.data;

import fy.GD.basic.GraphNode;
import fy.GD.edges.DFEdge;
import fy.GD.mgraph.MethodPDG;
import ghaffarian.graphs.Edge;

import java.util.Set;
import java.util.stream.Collectors;

public class ForwardDataDepTracker extends DataDependencyTracker{

    public ForwardDataDepTracker(MethodPDG graph) {
        super(graph);
    }

    public ForwardDataDepTracker(MethodPDG graph, int depth) {
        super(graph);
        this.MAX_DATA_DEPTH = depth;
    }

    public void track(GraphNode startNode) {
        if (startNode == null) return;
        dataBindNodes.add(startNode);
        // find instant data bind nodes
        Set<Edge<GraphNode, DFEdge>> instantDataBindEdges = graph.dataFlowEdges.stream()
                .filter(e -> e.source == startNode)
                .collect(Collectors.toSet());
        addEdges(instantDataBindEdges);
        // bfs
        visiting.addAll(instantDataBindEdges);
        while (!visiting.isEmpty() && --MAX_DATA_DEPTH >= 0) {
            Edge<GraphNode, DFEdge> curEdge = visiting.pop();
            GraphNode curNode = curEdge.target;
            dataBindNodes.add(curNode);
            if (curEdge.source != null)
                addEdge(curEdge);
            // populating by data flow
            if (visited.add(curEdge)) {
                graph.dataFlowEdges.stream()
                        .filter(edge -> edge.source == curNode)
                        .forEach(visiting::add);
            }
        }
    }

    public void setMAX_DATA_DEPTH(int MAX_DATA_DEPTH) {
        this.MAX_DATA_DEPTH = MAX_DATA_DEPTH;
    }
}
