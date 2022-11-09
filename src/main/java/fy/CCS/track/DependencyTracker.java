package fy.CCS.track;

import fy.GD.mgraph.MethodPDG;

public abstract class DependencyTracker {
    MethodPDG graph;

    public DependencyTracker(MethodPDG graph) {
        this.graph = graph;
    }
}
