package fy.GD.mgraph;

import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GD.basic.EdgeTypes;
import fy.GD.basic.GraphNode;
import fy.GD.edges.*;
import ghaffarian.graphs.Edge;
import ghaffarian.progex.graphs.AbstractProgramGraph;
import ghaffarian.progex.graphs.pdg.ControlDependenceGraph;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class MethodPDG extends AbstractProgramGraph<GraphNode, CFEdge> {
    public MethodDeclaration n;
    public MethodCFG mCFG;
    // extra edges
    public Set<Edge<GraphNode, DFEdge>> dataFlowEdges = new LinkedHashSet<>();
    public Set<Edge<GraphNode, CDEdge>> controlDepEdges = new LinkedHashSet<>();
    public Set<Edge<GraphNode, ASEdge>> astEdges = new LinkedHashSet<>();
    public Set<Edge<GraphNode, NCSEdge>> nceEdges = new LinkedHashSet<>();

    public MethodPDG(MethodCFG mCFG) {
        this.n = mCFG.md;
        this.mCFG = mCFG;
        mCFG.copyVertexSet().forEach(this::addVertex);
        mCFG.copyEdgeSet().forEach(this::addEdge);
    }

    @Override
    public void exportDOT(String s) throws IOException {

    }

    @Override
    public void exportGML(String s) throws IOException {

    }

    @Override
    public void exportJSON(String s) throws IOException {

    }
}
