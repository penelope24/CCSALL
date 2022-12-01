package fy.CCS.slicing;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.CCS.track.DTEntry;
import fy.GD.basic.GraphNode;
import fy.GD.mgraph.MethodPDG;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Slicer {

    public static MethodDeclaration slice(MethodDeclaration n, MethodPDG graph, List<Integer> startPoints, int MAX_DATA_DEPTH, int MAX_CTRL_DEPTH) {
        MethodDeclaration clone = n.clone();
        Set<Integer> reserved = DTEntry.dependencyTrack(graph, startPoints, MAX_DATA_DEPTH, MAX_CTRL_DEPTH);
        MethodDeclaration n2 = SrcCodeTransformer.slice(clone, reserved);
        Optional<Node> parOpt = n.getParentNode();
        parOpt.ifPresent(n2::setParentNode);
        return n2;
    }

    public static MethodPDG outputSliceAsPDG(MethodDeclaration slice, PDGBuilder builder) {
        return builder.build(slice);
    }

    public static void noteSliceInOriGraph(MethodPDG original, MethodPDG sliced, int version, int hunk_index) {
        sliced.copyVertexSet().forEach(n -> {
            GraphNode node = original.copyVertexSet().stream()
                    .filter(n1 -> n1.equals(n))
                    .findFirst().orElse(null);
            assert node != null;
            String hunkStr = "hunk" + "@v" + version + "__" + hunk_index;
            node.setHunkStr(hunkStr);
        });
    }
}
