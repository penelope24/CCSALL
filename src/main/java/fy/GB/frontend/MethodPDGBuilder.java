package fy.GB.frontend;


import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GD.basic.GraphEdge;
import fy.GD.basic.GraphNode;
import fy.GD.edges.CFEdge;
import fy.GD.edges.DFEdge;
import fy.GD.mgraph.MethodCFG;
import fy.GD.mgraph.MethodPDG;
import ghaffarian.graphs.Edge;


import java.util.*;

public class MethodPDGBuilder {
    private MethodDeclaration n;
    private int index = 0; //表示节点的编号开始值
    private List<String> leafNodes;
    private Set<GraphEdge> allDFGEdgesList;
    private Properties prop;

    public MethodPDGBuilder(MethodDeclaration n, Set<GraphEdge> allDFGEdgesList, Properties prop) {
        this.leafNodes = new ArrayList<>();
        this.allDFGEdgesList = allDFGEdgesList;
        this.prop = prop;
        this.n = n;
    }

    private MethodPDG BFS(GraphNode root, boolean cfgFlag, boolean astFlag, boolean dfgFlag, boolean ncsFlag) {
        MethodCFG mCFG = new MethodCFG(n);
        Queue<GraphNode> dealingNodes = new LinkedList<>();
        dealingNodes.add(root);
        while (!dealingNodes.isEmpty()) {
            GraphNode par = dealingNodes.poll();
            String parIndexNum = "";
            if (par.getDotNum() != null) {
                //已经创立

            }
            else {
                parIndexNum = "n" + (index++);
                par.setDotNum(parIndexNum);
                String label = DotStrFilter.filterQuotation(par.getOriginalCodeStr());
                int line = par.getCodeLineNum();
                par.setSimplifyCodeStr(label);
                par.setCodeLineNum(line);
                mCFG.addVertex(par);
                //创立cfg节点同时 创建ast节点
//                if(astFlag) {
//                    ASTRecurive(par.getAstRootNode(), par.getDotNum());
//                }
            }
            //然后就是添加子节点
            List<GraphNode> adjacentPoints = par.getAdjacentPoints();
            //先把没有在dot文件中建立点的点都建立好，才能把边连起来！
            for (GraphNode child : adjacentPoints) {
                if (child.getDotNum() == null) {
                    //没有处理过，就需要入队列
                    dealingNodes.add(child);
                    child.setDotNum("n" + (index));
                    index++;
                    String label = DotStrFilter.filterQuotation(child.getOriginalCodeStr());
                    child.setSimplifyCodeStr(label);
                    mCFG.addVertex(child);
//                    if(astFlag) {
//                        ASTRecurive(child.getAstRootNode(), child.getDotNum());
//                    }
                }
            }
            if(cfgFlag) {
                //建立边结构
                for (GraphEdge edge : par.getEdgs()) {
                    if (edge.getOriginalNode() == null || edge.getAimNode() == null) continue;
                    GraphNode s = edge.getOriginalNode();
                    GraphNode t = edge.getAimNode();
                    if (!mCFG.containsVertex(s)) {
                        mCFG.addVertex(s);
                    }
                    if (!mCFG.containsVertex(t)) {
                        mCFG.addVertex(t);
                    }
                    Edge<GraphNode, CFEdge> e = new Edge<>(s, new CFEdge(CFEdge.Type.EPSILON), t);
                    mCFG.addEdge(e);
                }
            }
        }
        MethodPDG mPDG = new MethodPDG(mCFG);
        if(dfgFlag){
            //数据流创建
            for(GraphEdge edge:this.allDFGEdgesList){
                if (edge.getOriginalNode() == null || edge.getAimNode() == null) continue;
                GraphNode s = edge.getOriginalNode();
                GraphNode t = edge.getAimNode();
                if (!mPDG.containsVertex(s)) mPDG.addVertex(s);
                if (!mPDG.containsVertex(t)) mPDG.addVertex(t);
                mPDG.dataFlowEdges.add(new Edge<>(s, new DFEdge(DFEdge.Type.FLOW, ""), t));
            }
        }
//        if(ncsFlag && astFlag) { //必须有ast才能构建
//            NCS(leafNodes);
//        }
        return mPDG;
    }


    public MethodPDG buildGraph(GraphNode root) {
        boolean cfgFlag = Boolean.parseBoolean(prop.getProperty("node.cfg"));
        boolean astFlag = Boolean.parseBoolean(prop.getProperty("node.ast"));
        boolean dfgFlag = Boolean.parseBoolean(prop.getProperty("edge.dataflow"));
        boolean ncsFlag = Boolean.parseBoolean(prop.getProperty("edge.ncs"));
        MethodPDG graph = BFS(root,cfgFlag,astFlag,dfgFlag,ncsFlag);
        return graph;
    }
}