package fy.CCS.track.data;

import fy.GD.basic.GraphNode;
import fy.GD.edges.DFEdge;
import fy.GD.mgraph.MethodPDG;
import ghaffarian.graphs.Edge;

import java.util.Set;
import java.util.stream.Collectors;

public class BackwardDataDepTracker extends DataDependencyTracker {

    public BackwardDataDepTracker(MethodPDG graph) {
        super(graph);
    }

    public BackwardDataDepTracker(MethodPDG graph, int depth) {
        super(graph);
        this.MAX_DATA_DEPTH = depth;
    }

    public void track(GraphNode startNode) {
        if (startNode == null) return;
        dataBindNodes.add(startNode);
        // find instant data bind nodes
        Set<Edge<GraphNode, DFEdge>> instantDataBindEdges = graph.dataFlowEdges.stream()
                .filter(e -> e.target == startNode)
                .collect(Collectors.toSet());
//        addEdges(instantDataBindEdges);
        // bfs
        visiting.addAll(instantDataBindEdges);
        while (!visiting.isEmpty() && --MAX_DATA_DEPTH >= 0) {
            Edge<GraphNode, DFEdge> curEdge = visiting.pop();
            GraphNode curNode = curEdge.source;
            dataBindNodes.add(curNode);
            if (curEdge.target != null)
                addEdge(curEdge);
            // populating by data flow
            if (visited.add(curEdge)) {
                graph.dataFlowEdges.stream()
                        .filter(edge -> edge.target == curNode)
                        .forEach(visiting::add);
            }
        }
    }
}
