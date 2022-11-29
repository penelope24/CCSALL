package fy.GB;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.CCS.slicing.PDGBuilder;
import fy.GD.mgraph.MethodPDG;
import fy.GD.export.ExpEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class GBTest {
    MethodDeclaration n;
    MethodPDG graph;
    String output = "/Users/fy/Documents/fyJavaProjects/CCSALL/src/test/output/tmp";

    @BeforeEach
    void init_a_graph() throws FileNotFoundException {
        String project = "/Users/fy/Documents/MyProjects/slicing_cases";
        String file = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic/case2.java";
        CompilationUnit cu = StaticJavaParser.parse(new File(file));
        n = cu.findFirst(MethodDeclaration.class).get();
        graph = PDGBuilder.one_pass_parse(project, file, n);

    }

    @Test
    void print() {
        ExpEntry.exportDot(graph, output + "/case2.dot");
    }




}
