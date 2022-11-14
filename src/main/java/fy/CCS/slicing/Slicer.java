package fy.CCS.slicing;

import com.github.javaparser.ast.body.MethodDeclaration;
import fy.CCS.track.DTEntry;
import fy.CCS.track.ctrl.SimplifiedCtrlDependencyTracker;
import fy.CCS.track.data.BackwardDataDepTracker;
import fy.CCS.track.data.ForwardDataDepTracker;
import fy.GB.entry.GBEntry;
import fy.GD.basic.GraphNode;
import fy.GD.mgraph.MethodPDG;
import fy.GD.mgraph.Slice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Slicer {
    String project;
    String javaFile;
    MethodDeclaration n;
    MethodPDG graph;
    int MAX_DATA_DEPTH = 2;
    int MAX_CTRL_DEPTH = 1;


    public Slicer(MethodPDG graph) {
        this.graph = graph;
        this.n = graph.n;
        this.project = graph.project;
        this.javaFile = graph.javaFile;
    }

    public MethodPDG slice (int start) {
        Set<GraphNode> reserved = DTEntry.track(graph, start, MAX_DATA_DEPTH, MAX_CTRL_DEPTH);
        if (reserved != null) {
            MethodDeclaration n2 = SrcCodeTransformer.slice(n, reserved.stream()
                    .map(GraphNode::getCodeLineNum).collect(Collectors.toList()));
            return GBEntry.one_pass_parse(project, javaFile, n2);
        }
        return null;
    }

}
