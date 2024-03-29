package fy.CCS.track.ctrl;

import fy.GD.basic.GraphNode;
import fy.GD.mgraph.MethodPDG;

import java.util.Set;

public class SimplifiedCtrlDependencyTracker extends CtrlDependencyTracker {

    public SimplifiedCtrlDependencyTracker(MethodPDG graph) {
        super(graph);
    }

    public void track(GraphNode startNode, int limit) {
        GraphNode firstPar = startNode.getParentNode();
        if (firstPar != null) {
            // bfs
            visiting.add(firstPar);
            while (!visiting.isEmpty() && limit > 0) {
                GraphNode cur = visiting.pop();
                ctrlBindNodes.add(cur);
                limit--;
                GraphNode par = cur.getParentNode();
                if (par != null && !visited.contains(par)) {
                    visiting.add(par);
                }
            }
        }
    }

    public void track(Set<GraphNode> dataBindNodes, int limit) {
        dataBindNodes.forEach(n -> track(n, limit));
    }
}
