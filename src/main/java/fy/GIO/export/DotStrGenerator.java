package fy.GIO.export;

import fy.GIO.object.GraphObject;

import java.util.ArrayList;
import java.util.List;

public class DotStrGenerator {

    public String generate(GraphObject graphObject) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph ").append("generated").append(" {").append("\n");
        sb.append("    // nodes").append("\n");
        graphObject.nodes.forEach(nodeInfo -> {
            String node_stmt = node_stmt(nodeInfo);
            sb.append(node_stmt).append("\n");
        });
        sb.append("    // edges").append("\n");
        graphObject.links.forEach(edgeInfo -> {
            String edge_stmt = edge_stmt(edgeInfo);
            sb.append(edge_stmt).append("\n");
        });
        sb.append("    // end of graph").append("\n");
        sb.append("}");
        return sb.toString();
    }

    public String attr_str(String key, Object value) {
        String s = key + "=";
        if (value == null) {
            return key + "";
        }
        if (value instanceof String || value instanceof List) {
            s += "\"" + value + "\"";
        } else {
            s += value.toString();
        }
        return s;
    }

    public String node_stmt(GraphObject.NodeInfo nodeInfo) {
        String node_id = "n" + nodeInfo.id;
        List<String> attr_list = new ArrayList<>();
        attr_list.add(attr_str("label", nodeInfo.label));
        attr_list.add(attr_str("line", nodeInfo.line));
        attr_list.add(attr_str("slices", nodeInfo.slices));
        return "    " + node_id + "  " + attr_list.toString() + ";";
    }

    public String edge_stmt(GraphObject.EdgeInfo edgeInfo) {
        String edge_op = " -> ";
        String src = "n" + edgeInfo.source;
        String tgt = "n" + edgeInfo.target;
        List<String> attr_list = new ArrayList<>();
        attr_list.add(attr_str("type", edgeInfo.label));
        return "    " + src + edge_op + tgt + "  " + attr_list.toString() + ";";
    }
}
