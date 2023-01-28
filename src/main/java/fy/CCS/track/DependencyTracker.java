package fy.CCS.track;

import fy.GD.mgraph.MethodPDG;

public class DependencyTracker {

    final MethodPDG graph;

    public DependencyTracker(MethodPDG graph) {
        this.graph = graph;
    }
}
