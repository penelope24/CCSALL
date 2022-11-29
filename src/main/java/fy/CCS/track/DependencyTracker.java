package fy.CCS.track;

import fy.GD.mgraph.MethodPDG;

public class DependencyTracker {

    MethodPDG graph;

    public DependencyTracker(MethodPDG graph) {
        this.graph = graph;
    }
}
