package fy.GD.mgraph;

import fy.GD.basic.GraphNode;
import fy.GD.edges.CFEdge;
import fy.GD.edges.DFEdge;
import ghaffarian.graphs.Edge;
import ghaffarian.progex.graphs.ast.ASNode;
import ghaffarian.progex.graphs.cfg.CFNode;
import ghaffarian.progex.graphs.pdg.PDNode;
import ghaffarian.progex.utils.StringUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MethodPDGExporter {

    public static void export(MethodPDG graph, String outputFile) {
        try (PrintWriter dot = new PrintWriter(outputFile, "UTF-8")) {
            dot.println("digraph " + "SLICE_{");
            Map<GraphNode, String> nodeIdMap = new LinkedHashMap<>();
            int nodeCount = 1;
            for (GraphNode node : graph.copyVertexSet()) {
                String name = "v" + nodeCount++;
                nodeIdMap.put(node, name);
                StringBuilder label = new StringBuilder("  [label=\"");
                if (node.getCodeLineNum() > 0)
                    label.append(node.getCodeLineNum()).append(":  ");
                String coloredDotStr = "";
                label.append(StringUtils.escape(node.getSimplifyCodeStr())).append("\"").append(coloredDotStr).append("];");
                dot.println("  " + name + label.toString());
            }
            for (Edge<GraphNode, CFEdge> edge : graph.controlFlowEdges) {
                String src = nodeIdMap.get(edge.source);
                String tgt = nodeIdMap.get(edge.target);
                String edgeDotColorStr = "  [";
                dot.println("  " + src + " -> " + tgt + edgeDotColorStr +
                        "label=\"" + edge.label.type + "\"];");
            }
            for (Edge<GraphNode, DFEdge> edge : graph.dataFlowEdges) {
                String src = nodeIdMap.get(edge.source);
                String tgt = nodeIdMap.get(edge.target);
                String edgeDotColorStr = "  [";
                dot.println("  " + src + " -> " + tgt + edgeDotColorStr + "label=\" (" + edge.label.var + ")\"];");
            }
            dot.println("   // end-of-graph\n}");
        }
        catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
