package fy.GB.visitor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import fy.GB.entry.Config;
import fy.GB.frontend.CFGBuild;
import fy.GB.parse.ASTCreater;
import fy.GB.parse.CFGCreator;
import fy.GB.parse.CFGNodeSimplifier;
import fy.GB.parse.DFGCreater;
import fy.GD.basic.DFVarNode;
import fy.GD.basic.GraphNode;
import fy.GD.mgraph.MethodPDG;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

public class GBVisitor {

    private final Set<String> package2types;
    private final List<String> imports;
    private final Set<DFVarNode> fields;
    private Properties prop = Config.loadProperties();

    public GBVisitor(VarVisitor varVisitor) {
        this.package2types = varVisitor.getCurrentPackageAllTypes();
        this.imports = varVisitor.getAllImports();
        this.fields = varVisitor.getAllFields();
    }

    public MethodPDG build (MethodDeclaration n) {
        boolean node_simplify = Boolean.parseBoolean(prop.getProperty("node.simplify"));
        boolean node_ast = Boolean.parseBoolean(prop.getProperty("node.ast"));
        boolean edge_dataflow = Boolean.parseBoolean(prop.getProperty("edge.dataflow"));
        // filter out constructors
        if (n.getType() == null) return null;
        // filter out anonymous methods
        Optional<Node> parentNode = n.getParentNode();
        if (parentNode.isEmpty() || !(parentNode.get() instanceof TypeDeclaration)) return null;
        // build CFG
        CFGCreator cfgCreator = new CFGCreator();
        GraphNode cfgRoot = cfgCreator.buildMethodCFG(n);
        if (node_ast) {
            // build ast
            ASTCreater astCreater = new ASTCreater(cfgCreator.getAllNodesMap());
            astCreater.buildMethodAST(n);
        }
        DFGCreater dfgCreater = new DFGCreater(cfgCreator.getAllNodesMap());
        if (edge_dataflow) {
            dfgCreater.buildMethodDFG(n);
        }
        if (node_simplify) {
            CFGNodeSimplifier simplifier = new CFGNodeSimplifier(cfgCreator.getAllNodesMap(), package2types, imports, fields);
            simplifier.simplifyCFGNodeStr(n);
        }
        CFGBuild builder = new CFGBuild(n, dfgCreater.getAllDFGEdgesList(), prop);
        return builder.buildGraph(cfgRoot);
    }
}
