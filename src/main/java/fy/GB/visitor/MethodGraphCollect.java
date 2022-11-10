package fy.GB.visitor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import fy.GB.frontend.CFGBuild;
import fy.GB.parse.ASTCreater;
import fy.GB.parse.CFGCreator;
import fy.GB.parse.CFGNodeSimplifier;
import fy.GB.parse.DFGCreater;
import fy.GD.basic.DFVarNode;
import fy.GD.basic.GraphNode;
import fy.GD.mgraph.MethodPDG;

import java.util.List;
import java.util.Properties;
import java.util.Set;

public class MethodGraphCollect extends VoidVisitorAdapter<List<MethodPDG>> {
    private final Set<String> package2types;
    private final List<String> imports;
    private final Set<DFVarNode> fields;
    private Properties prop;

    public MethodGraphCollect(VarVisitor varVisitor, Properties prop) {
        this.package2types = varVisitor.getCurrentPackageAllTypes();
        this.imports = varVisitor.getAllImports();
        this.fields = varVisitor.getAllFields();
        this.prop = prop;
    }

    @Override
    public void visit(MethodDeclaration n, List<MethodPDG> c) {
        // parse properties
        // node.cfg & edge.cfg are always true
        // edge.ncs is only used when printing
        boolean node_simplify = Boolean.parseBoolean(prop.getProperty("node.simplify"));
        boolean node_ast = Boolean.parseBoolean(prop.getProperty("node.ast"));
        boolean edge_dataflow = Boolean.parseBoolean(prop.getProperty("edge.dataflow"));

        // filter out constructors
        if (n.getType() != null) {
            if (n.getParentNode().isPresent()) {
                // filter out anonymous methods
                if (!(n.getParentNode().get() instanceof TypeDeclaration)) {
                    return;
                }
                System.out.println("parsing " + n.getNameAsString());
                // build cfg
                CFGCreator cfgCreator = new CFGCreator();
                GraphNode cfgRoot = cfgCreator.buildMethodCFG(n);
                if (node_ast) {
                    // build ast
                    ASTCreater astCreater = new ASTCreater(cfgCreator.getAllNodesMap());
                    astCreater.buildMethodAST(n);
                }
                DFGCreater dfgCreater = new DFGCreater(cfgCreator.getAllNodesMap());
                if (edge_dataflow) {
                    // analyse data flow
                    dfgCreater.buildMethodDFG(n);
                }
                if (node_simplify) {
                    // simplify node
                    CFGNodeSimplifier simplifier = new CFGNodeSimplifier(cfgCreator.getAllNodesMap(), package2types, imports, fields);
                    simplifier.simplifyCFGNodeStr(n);
                }

//                for (GraphNode node : graphNodes) {
//                    try {
//                        CFGBuild builder = new CFGBuild(n, dfgCreater.getAllDFGEdgesList(), prop);
//                        MethodPDG graph = builder.buildGraph(node);
//                        c.add(builder.buildGraph(node));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        System.out.println("输出方法文件失败："+n.getNameAsString());
//                    }
//                }
            }
        }
    }
}
