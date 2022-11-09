package fy.CCS.track.ctrl;

import fy.CCS.track.DependencyTracker;
import fy.GD.basic.GraphNode;
import fy.GD.edges.CDEdge;
import fy.GD.edges.DFEdge;
import fy.GD.mgraph.MethodPDG;
import ghaffarian.graphs.Edge;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Set;

public class CtrlDependencyTracker extends DependencyTracker {
    public MethodPDG graph;
    public Deque<GraphNode> visiting = new ArrayDeque<>();
    public Deque<Edge<GraphNode, CDEdge>> visited = new ArrayDeque<>();
    public Set<GraphNode> ctrlBindNodes = new LinkedHashSet<>();
    public Set<Edge<GraphNode, CDEdge>> ctrlBindEdges = new LinkedHashSet<>();

    public CtrlDependencyTracker(MethodPDG graph) {
        super(graph);
    }


    public void addEdge(Edge<GraphNode, CDEdge> edge) {
        ctrlBindNodes.add(edge.source);
        ctrlBindNodes.add(edge.target);
        ctrlBindEdges.add(edge);
    }

    public void addEdges(Set<Edge<GraphNode, CDEdge>> edges) {
        edges.forEach(this::addEdge);
    }

    public Set<GraphNode> getCtrlBindNodes() {
        return ctrlBindNodes;
    }

    public Set<Edge<GraphNode, CDEdge>> getCtrlBindEdges() {
        return ctrlBindEdges;
    }
}
