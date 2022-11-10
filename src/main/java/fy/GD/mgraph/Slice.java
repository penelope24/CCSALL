package fy.GD.mgraph;

import fy.GD.basic.GraphNode;
import fy.GD.edges.CFEdge;
import fy.GD.edges.DFEdge;
import ghaffarian.graphs.Edge;
import ghaffarian.progex.graphs.AbstractProgramGraph;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class Slice extends AbstractProgramGraph<GraphNode, CFEdge> {

    public Set<Edge<GraphNode, DFEdge>> dataFlowEdges = new LinkedHashSet<>();

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
