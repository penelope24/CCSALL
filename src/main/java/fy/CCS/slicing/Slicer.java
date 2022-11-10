package fy.CCS.slicing;

import com.github.javaparser.ast.body.MethodDeclaration;
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
    int MAX_CTRL_DEPTH = 3;


    public Slicer(MethodPDG graph) {
        this.graph = graph;
        this.n = graph.n;
        this.project = graph.project;
        this.javaFile = graph.javaFile;
    }

    public MethodPDG slice (int start) {
        GraphNode startNode = getStartNode(start);
        if (startNode != null) {
            Set<GraphNode> reserved = new HashSet<>();
            // data dep track
            BackwardDataDepTracker ddTracker1 = new BackwardDataDepTracker(graph);
            ddTracker1.track(startNode);
            reserved.addAll(ddTracker1.getDataBindNodes());
            ForwardDataDepTracker ddTracker2 = new ForwardDataDepTracker(graph);
            ddTracker2.track(startNode);
            reserved.addAll(ddTracker2.getDataBindNodes());
            // ctrl dep track
            SimplifiedCtrlDependencyTracker cdTracker = new SimplifiedCtrlDependencyTracker(graph);
            cdTracker.track(startNode, MAX_CTRL_DEPTH);
            reserved.addAll(cdTracker.getCtrlBindNodes());
            // lines 2 remove
            Set<Integer> lines2rem = graph.copyVertexSet().stream()
                    .filter(v -> !reserved.contains(v))
                    .map(GraphNode::getCodeLineNum)
                    .collect(Collectors.toSet());
            MethodDeclaration n2 = SrcCodeTransformer.removeLinesFromMethod(n, new ArrayList<>(lines2rem));
            MethodPDG slice = GBEntry.one_pass_parse(project, javaFile, n2);
            return slice;
        }
        return null;
    }

    private GraphNode getStartNode(int start) {
        return graph.copyVertexSet().stream()
                .filter(v -> v.getCodeLineNum() == start)
                .findFirst().orElse(null);
    }
}
