package fy.GD.export;

import fy.CCD.GW.data.Hunk;
import fy.GD.basic.GraphNode;
import fy.GD.edges.CFEdge;
import fy.GD.edges.DFEdge;
import fy.GD.mgraph.MethodPDG;
import ghaffarian.graphs.Edge;
import ghaffarian.progex.utils.StringUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExpEntry {

    public static void exportDot(MethodPDG graph, String outputFile) {
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
            for (Edge<GraphNode, CFEdge> edge : graph.copyEdgeSet()) {
                String src = nodeIdMap.get(edge.source);
                String tgt = nodeIdMap.get(edge.target);
                String edgeDotColorStr = "  [";
                dot.println("  " + src + " -> " + tgt + edgeDotColorStr +
                        "label=\"" + edge.label.type + "\"];");
            }
            for (Edge<GraphNode, DFEdge> edge : graph.dataFlowEdges) {
                String src = nodeIdMap.get(edge.source);
                String tgt = nodeIdMap.get(edge.target);
                String edgeDotColorStr = "  [color=red, ";
                dot.println("  " + src + " -> " + tgt + edgeDotColorStr + "label=\" (" + edge.label.var + ")\"];");
            }
            dot.println("   // end-of-graph\n}");
        }
        catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void exportJSON(MethodPDG graph, String outputFile, Hunk hunk) {
        try (PrintWriter json = new PrintWriter(outputFile, "UTF-8")) {
            // start
            writeLeftBracket(json, 0);
            // graph properties
            writeProperty(json, 4,"directed", "true", true);
            writeProperty(json, 4,"multi_graph", "true", true);
            writeProperty(json, 4,"commit_id", hunk.getCommitId(), true);
            writeProperty(json, 4,"file_name", hunk.getSimpleFileName(), true);
            writeProperty(json, 4,"method_name", hunk.getMethodName(), true);
            writeProperty(json, 4,"edit", hunk.edit.toString(), true);
            newLine(json);
            // nodes
            listStart(json, 4, "nodes");
            Map<GraphNode, Integer> nodeIdMap = new LinkedHashMap<>();
            int nodeCount = 0;
            for (GraphNode node : graph.copyVertexSet()) {
                writeLeftBracket(json, 8);
                int id = nodeCount++;
                nodeIdMap.put(node, id);
                writeProperty(json, 12, "id", String.valueOf(id), true);
                writeProperty(json, 12, "line", String.valueOf(node.getCodeLineNum()), true);
                writeProperty(json, 12, "label", node.getSimplifyCodeStr(), false);
                if (nodeCount == graph.vertexCount()) {
                    writeRightBracket(json, 8, false);
                }
                else {
                    writeRightBracket(json, 8, true);
                }
            }
            listEnd(json, 4, true);
             newLine(json);
            // edges
            listStart(json, 4, "links");
            int controlFlowEdgeCount = 0;
            for (Edge<GraphNode, CFEdge> edge : graph.copyEdgeSet()) {
                int src = nodeIdMap.get(edge.source);
                int tgt = nodeIdMap.get(edge.target);
                String label = "control_flow";
                writeLeftBracket(json, 8);
                writeProperty(json, 12, "id", String.valueOf(controlFlowEdgeCount), true);
                writeProperty(json, 12, "source", String.valueOf(src), true);
                writeProperty(json, 12, "target", String.valueOf(tgt), true);
                writeProperty(json, 12, "label", label, false);
                controlFlowEdgeCount++;
                if (graph.dataFlowEdges.size() > 0) {
                    writeRightBracket(json, 8, true);
                }
                else {
                    writeRightBracket(json, 9, controlFlowEdgeCount != graph.edgeCount());
                }
            }
            int dataFlowEdgeCount = 0;
            for (Edge<GraphNode, DFEdge> edge : graph.dataFlowEdges) {
                int src = nodeIdMap.get(edge.source);
                int tgt = nodeIdMap.get(edge.target);
                String label = "data_flow";
                writeLeftBracket(json, 8);
                writeProperty(json, 12, "id", String.valueOf(dataFlowEdgeCount), true);
                writeProperty(json, 12, "source", String.valueOf(src), true);
                writeProperty(json, 12, "target", String.valueOf(tgt), true);
                writeProperty(json, 12, "label", label, false);
                dataFlowEdgeCount++;
                writeRightBracket(json, 8, dataFlowEdgeCount != graph.dataFlowEdges.size());
            }
            listEnd(json, 4, false);
            //end
            writeRightBracket(json, 0, false);
        }
        catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeProperty(PrintWriter json, int indent, String key, String value, boolean endWithComma) {
        if (endWithComma) {
            json.println(" ".repeat(indent) + "\"" + key + "\": " + "\"" + value + "\"" + ",");
        }
        else {
            json.println(" ".repeat(indent) + "\"" + key + "\": " + "\"" + value + "\"");
        }
    }

    public static void writeLeftBracket(PrintWriter json, int indent) {
        json.println(" ".repeat(indent) + "{");
    }

    public static void writeRightBracket(PrintWriter json, int indent, boolean endWithComma) {
        if (endWithComma) {
            json.println(" ".repeat(indent) + "}" + ",");
        }
        else {
            json.println(" ".repeat(indent) + "}");
        }
    }

    public static void listStart(PrintWriter json, int indent, String name) {
        json.println(" ".repeat(indent) + "\"" + name + "\": " + "[");
    }

    public static void listEnd(PrintWriter json, int indent, boolean endWithComma) {
        if (endWithComma) {
            json.println(" ".repeat(indent) + "]" + ",");
        }
        else {
            json.println(" ".repeat(indent) + "]");
        }
    }

    public static void newLine(PrintWriter json) {
        json.println();
    }
}
