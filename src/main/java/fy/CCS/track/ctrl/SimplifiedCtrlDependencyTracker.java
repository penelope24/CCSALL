package fy.CCS.track.ctrl;

import fy.GD.basic.GraphNode;
import fy.GD.edges.CDEdge;
import fy.GD.mgraph.MethodPDG;
import ghaffarian.graphs.Edge;

import java.util.Set;
import java.util.stream.Collectors;

public class SimplifiedCtrlDependencyTracker extends CtrlDependencyTracker{

    public SimplifiedCtrlDependencyTracker(MethodPDG graph) {
        super(graph);
    }

    public void track(GraphNode startNode, int limit) {
        visiting.add(startNode);
        while (--limit >= 0) {
            GraphNode cur = visiting.pop();
            Edge<GraphNode, CDEdge> parentCtrlDepEdge = graph.controlDepEdges.stream()
                    .filter(e -> e.target == cur)
                    .findFirst().orElse(null);
            if (parentCtrlDepEdge != null) {
                addEdge(parentCtrlDepEdge);
                if (!visited.add(parentCtrlDepEdge)) {
                    visiting.add(parentCtrlDepEdge.source);
                }
            }
        }
    }

    public void track (Set<GraphNode> dataBindNodes, int limit) {
        dataBindNodes.forEach(n -> track(n, limit));
    }
}
