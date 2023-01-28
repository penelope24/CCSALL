package fy.GD.mgraph;

import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GD.basic.GraphNode;
import fy.GD.edges.CFEdge;
import ghaffarian.progex.graphs.AbstractProgramGraph;

import java.io.IOException;

public class MethodCFG extends AbstractProgramGraph<GraphNode, CFEdge> {

    final MethodDeclaration md;

    public MethodCFG(MethodDeclaration md) {
        this.md = md;
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
