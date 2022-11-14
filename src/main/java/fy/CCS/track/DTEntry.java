package fy.CCS.track;

import com.github.javaparser.ast.body.MethodDeclaration;
import fy.CCS.slicing.SrcCodeTransformer;
import fy.CCS.track.ctrl.SimplifiedCtrlDependencyTracker;
import fy.CCS.track.data.BackwardDataDepTracker;
import fy.CCS.track.data.ForwardDataDepTracker;
import fy.GB.entry.GBEntry;
import fy.GD.basic.GraphNode;
import fy.GD.mgraph.MethodPDG;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * entry of dependency tracking
 */
public class DTEntry {

    public static Set<GraphNode> track(MethodPDG graph, int start, int MAX_DATA_DEPTH, int MAX_CTRL_DEPTH) {
        Set<GraphNode> reserved = new HashSet<>();
        GraphNode startNode = graph.copyVertexSet().stream()
                .filter(v -> v.getCodeLineNum() == start)
                .findFirst().orElse(null);
        if (startNode != null) {
            // add root node to reserve set
            reserved.add(graph.root);
            // data dep track
            BackwardDataDepTracker ddTracker1 = new BackwardDataDepTracker(graph, MAX_DATA_DEPTH);
            ddTracker1.track(startNode);
            reserved.addAll(ddTracker1.getDataBindNodes());
            ForwardDataDepTracker ddTracker2 = new ForwardDataDepTracker(graph, MAX_DATA_DEPTH);
            ddTracker2.track(startNode);
            reserved.addAll(ddTracker2.getDataBindNodes());
//            reserved.forEach(v -> System.out.println(v.getCodeLineNum()));
//            System.out.println("------");
            // ctrl dep track
            SimplifiedCtrlDependencyTracker cdTracker = new SimplifiedCtrlDependencyTracker(graph);
            for (GraphNode node : reserved) {
                cdTracker.track(node, MAX_CTRL_DEPTH);
            }
            reserved.addAll(cdTracker.getCtrlBindNodes());
//            reserved.forEach(v -> System.out.println(v.getCodeLineNum()));
            return reserved;
        }
        return null;
    }

    public static Set<GraphNode> track(MethodPDG graph, List<Integer> startPoints, int MAX_DATA_DEPTH, int MAX_CTRL_DEPTH) {
        Set<GraphNode> reserved = new HashSet<>();
        startPoints.forEach(start ->
                reserved.addAll(Objects.requireNonNull(track(graph, start, MAX_DATA_DEPTH, MAX_CTRL_DEPTH))));
        return reserved;
    }

}
