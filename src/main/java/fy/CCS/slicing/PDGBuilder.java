package fy.CCS.slicing;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GB.entry.TypeSolverEntry;
import fy.GB.visitor.MethodVisitor;
import fy.GB.visitor.VarVisitor;
import fy.GD.basic.GraphNode;
import fy.GD.edges.CFEdge;
import fy.GD.edges.DFEdge;
import fy.GD.mgraph.MethodPDG;
import fy.utils.tools.JPHelper;
import ghaffarian.graphs.Edge;

import java.util.HashMap;
import java.util.Set;

public class PDGBuilder {

    HashMap<String, Set<String>> pkg2types;
    VarVisitor varVisitor;
    MethodVisitor methodVisitor;

    public PDGBuilder(HashMap<String, Set<String>> pkg2types, VarVisitor varVisitor) {
        this.pkg2types = pkg2types;
        this.varVisitor = varVisitor;
        this.methodVisitor = new MethodVisitor(varVisitor);
    }

    public MethodPDG build (MethodDeclaration n) {
        if (this.varVisitor == null || this.methodVisitor == null || n == null) {
            throw new IllegalStateException("null for graph building");
        }
        return this.methodVisitor.build(n);
    }

    public static MethodPDG one_pass_parse(String project, String file, MethodDeclaration n) {
        HashMap<String, Set<String>> pkg2types = TypeSolverEntry.solve_pkg2types(project);
        VarVisitor varVisitor;
        CompilationUnit cu = JPHelper.getCompilationUnit(file);
        if (cu == null) return null;
        varVisitor = TypeSolverEntry.solveVarTypesInFile(cu, pkg2types);
        MethodVisitor visitor = new MethodVisitor(varVisitor);
        MethodPDG graph = visitor.build(n);
        assert graph != null;
        graph.javaFile = file;
        graph.project = project;
        return graph;
    }

    public static GraphNode findOriginalNode(MethodPDG original, GraphNode node) {
        Set<GraphNode> candidates = original.copyVertexSet();
        // step 1
        GraphNode res1 = candidates.stream()
                .filter(graphNode -> graphNode.equals(node))
                .findFirst().orElse(null);
        if (res1 != null) {
            return res1;
        }
        // step 2
        else {
            GraphNode res2 = candidates.stream()
                    .filter(graphNode -> graphNode.getCodeLineNum() == node.getCodeLineNum())
                    .filter(graphNode -> graphNode.getSimplifyCodeStr().contains(node.getSimplifyCodeStr()))
                    .findFirst().orElse(null);
            if (res2 != null) {
                return res2;
            }
        }
        return null;
    }

    public static Edge<GraphNode, CFEdge> findOriginalControlFlowEdge(MethodPDG original, Edge<GraphNode, CFEdge> edge) {
        GraphNode oriSrc = findOriginalNode(original, edge.source);
        GraphNode oriTgt = findOriginalNode(original, edge.target);
        if (oriSrc != null && oriTgt != null) {
            Edge<GraphNode, CFEdge> oriEdge = original.copyEdgeSet().stream()
                    .filter(e -> e.source == oriSrc && e.target == oriTgt && e.label.type == edge.label.type)
                    .findFirst().orElse(null);
            if (oriEdge != null) {
                return oriEdge;
            }
        }
        return null;
    }

    public static Edge<GraphNode, DFEdge> findOriginalDataFlowEdge(MethodPDG original, Edge<GraphNode, DFEdge> edge) {
        GraphNode oriSrc = findOriginalNode(original, edge.source);
        GraphNode oriTgt = findOriginalNode(original, edge.target);
        if (oriSrc != null && oriTgt != null) {
            Edge<GraphNode, DFEdge> oriEdge = original.dataFlowEdges.stream()
                    .filter(e -> e.source == oriSrc && e.target == oriTgt && e.label.type == edge.label.type)
                    .findFirst().orElse(null);
            if (oriEdge != null) {
                return oriEdge;
            }
        }
        return null;
    }

    public static MethodPDG slice (MethodPDG original, MethodPDG sliced) {
        if (sliced == null) return null;
        sliced.copyVertexSet().forEach(node -> {
            GraphNode oriNode = findOriginalNode(original, node);
            sliced.removeVertex(node);
            if (oriNode != null) {
                sliced.addVertex(oriNode);
            }
        });
        sliced.copyEdgeSet().forEach(edge -> {
            Edge<GraphNode, CFEdge> oriControlFlowEdge = findOriginalControlFlowEdge(original, edge);
            sliced.removeEdge(edge);
            if (oriControlFlowEdge != null) {
                sliced.addEdge(oriControlFlowEdge);
            }
        });
        sliced.dataFlowEdges.forEach(edge -> {
            Edge<GraphNode, DFEdge> oriDataFlowEdge = findOriginalDataFlowEdge(original, edge);
            sliced.dataFlowEdges.remove(edge);
            if (oriDataFlowEdge != null) {
                sliced.dataFlowEdges.add(oriDataFlowEdge);
            }
        });
        return sliced;
    }

    public static void sliceAsProperty(MethodPDG original, MethodPDG sliced, int idx) {
        if (sliced == null) return;
        sliced.copyVertexSet().forEach(node -> {
            GraphNode oriNode = findOriginalNode(original, node);
            if (oriNode != null) {
                oriNode.addSLice(idx);
            }
        });
        sliced.copyEdgeSet().forEach(edge -> {
            Edge<GraphNode, CFEdge> oriControlFlowEdge = findOriginalControlFlowEdge(original, edge);
            if (oriControlFlowEdge != null) {
                oriControlFlowEdge.label.addSlice(idx);
            }
        });
        sliced.dataFlowEdges.forEach(edge -> {
            Edge<GraphNode, DFEdge> oriDataFlowEdge = findOriginalDataFlowEdge(original, edge);
            if (oriDataFlowEdge != null) {
                oriDataFlowEdge.label.addSLice(idx);
            }
        });
    }
}
