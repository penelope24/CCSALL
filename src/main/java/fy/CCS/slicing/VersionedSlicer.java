package fy.CCS.slicing;

import com.github.javaparser.ast.body.MethodDeclaration;
import fy.CCS.track.DTEntry;
import fy.GD.mgraph.MethodPDG;

import java.util.List;
import java.util.Set;

public class VersionedSlicer {
    MethodDeclaration n;
    MethodPDG graph;
    PDGBuilder generator;
    int MAX_DATA_DEPTH = 3;
    int MAX_CTRL_DEPTH = 2;

    public VersionedSlicer(MethodDeclaration n, MethodPDG graph, PDGBuilder generator) {
        this.n = n;
        this.graph = graph;
        this.generator = generator;
    }

    public MethodPDG slice(int start) {
        MethodDeclaration nClone = n.clone();
        Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, start, MAX_DATA_DEPTH, MAX_CTRL_DEPTH);
        MethodDeclaration slice = SrcCodeTransformer.slice(nClone, reservedLines);
        n.getParentNode().ifPresent(slice::setParentNode);
        MethodPDG sliceSubGraph = generator.build(slice);
        if (sliceSubGraph != null) {
            return sliceSubGraph;
        }
        else {
            throw new IllegalStateException("no slice sub graph valid");
        }
    }

    public MethodPDG slice(List<Integer> starts) {
        MethodDeclaration nClone = n.clone();
        Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, starts, MAX_DATA_DEPTH, MAX_CTRL_DEPTH);
        MethodDeclaration slice = SrcCodeTransformer.slice(nClone, reservedLines);
        n.getParentNode().ifPresent(slice::setParentNode);
        MethodPDG sliceSubGraph = generator.build(slice);
        if (sliceSubGraph != null) {
            return sliceSubGraph;
        }
        else {
            throw new IllegalStateException("no slice sub graph valid");
        }
    }

    public void setMAX_DATA_DEPTH(int MAX_DATA_DEPTH) {
        this.MAX_DATA_DEPTH = MAX_DATA_DEPTH;
    }

    public void setMAX_CTRL_DEPTH(int MAX_CTRL_DEPTH) {
        this.MAX_CTRL_DEPTH = MAX_CTRL_DEPTH;
    }
}
