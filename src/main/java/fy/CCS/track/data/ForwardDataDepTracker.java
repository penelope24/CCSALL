package fy.CCS.track.data;

import fy.GD.basic.GraphNode;
import fy.GD.edges.DFEdge;
import fy.GD.mgraph.MethodPDG;
import ghaffarian.graphs.Edge;

import java.util.*;
import java.util.stream.Collectors;

public class ForwardDataDepTracker extends DataDependencyTracker{


    public ForwardDataDepTracker(MethodPDG graph) {
        super(graph);
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
        while (!visiting.isEmpty()) {
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
}
