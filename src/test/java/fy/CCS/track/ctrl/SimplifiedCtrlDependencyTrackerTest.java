package fy.CCS.track.ctrl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.CCS.slicing.PDGBuilder;
import fy.GD.basic.GraphNode;
import fy.GD.mgraph.MethodPDG;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

class SimplifiedCtrlDependencyTrackerTest {

    MethodPDG graph;
    String output = "/Users/fy/Documents/fyJavaProjects/CCSALL/src/test/output/tmp";

    @BeforeEach
    void init_a_graph() throws FileNotFoundException {
        String project = "/Users/fy/Documents/MyProjects/slicing_cases";
        String file = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic/case1.java";
        CompilationUnit cu = StaticJavaParser.parse(new File(file));
        MethodDeclaration n = cu.findFirst(MethodDeclaration.class).get();
        graph = PDGBuilder.one_pass_parse(project, file, n);
    }

    @Test
    void test() {
        GraphNode startNode = graph.copyVertexSet().stream()
                .filter(n -> n.getCodeLineNum() == 15)
                .findFirst().orElse(null);
        SimplifiedCtrlDependencyTracker tracker = new SimplifiedCtrlDependencyTracker(graph);
        tracker.track(startNode, 2);
        Set<GraphNode> res = tracker.getCtrlBindNodes();
        res.forEach(graphNode -> System.out.println(graphNode.getCodeLineNum()));
    }

}