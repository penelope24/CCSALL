package fy.CCS.track.data;

import fy.CCS.track.DependencyTracker;
import fy.GD.basic.GraphNode;
import fy.GD.edges.DFEdge;
import fy.GD.mgraph.MethodPDG;
import ghaffarian.graphs.Edge;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Set;

public class DataDependencyTracker extends DependencyTracker {
    public final MethodPDG graph;
    public int MAX_DATA_DEPTH = 3; // default
    public final Deque<Edge<GraphNode, DFEdge>> visiting = new ArrayDeque<>();
    public final Deque<Edge<GraphNode, DFEdge>> visited = new ArrayDeque<>();
    public final Set<GraphNode> dataBindNodes = new LinkedHashSet<>();
    public final Set<Edge<GraphNode, DFEdge>> dataBindEdges = new LinkedHashSet<>();

    public DataDependencyTracker(MethodPDG graph) {
        super(graph);
        this.graph = graph;
    }

    public void addEdge(Edge<GraphNode, DFEdge> edge) {
        dataBindNodes.add(edge.source);
        dataBindNodes.add(edge.target);
        dataBindEdges.add(edge);
    }

    public void addEdges(Set<Edge<GraphNode, DFEdge>> edges) {
        edges.forEach(this::addEdge);
    }

    public Set<GraphNode> getDataBindNodes() {
        return dataBindNodes;
    }

    public Set<Edge<GraphNode, DFEdge>> getDataBindEdges() {
        return dataBindEdges;
    }

    public void setMAX_DATA_DEPTH(int MAX_DATA_DEPTH) {
        this.MAX_DATA_DEPTH = MAX_DATA_DEPTH;
    }
}
