package fy.CCS.track.ctrl;

import fy.CCS.track.DependencyTracker;
import fy.GD.basic.GraphNode;
import fy.GD.mgraph.MethodPDG;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Set;

public class CtrlDependencyTracker extends DependencyTracker {
    public MethodPDG graph;
    public final Deque<GraphNode> visiting = new ArrayDeque<>();
    public final Deque<GraphNode> visited = new ArrayDeque<>();
    public final Set<GraphNode> ctrlBindNodes = new LinkedHashSet<>();


    public CtrlDependencyTracker(MethodPDG graph) {
        super(graph);
    }

    public Set<GraphNode> getCtrlBindNodes() {
        return ctrlBindNodes;
    }

}
