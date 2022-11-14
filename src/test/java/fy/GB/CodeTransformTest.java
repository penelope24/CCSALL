package fy.GB;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import fy.CCS.slicing.SrcCodeTransformer;
import fy.GD.basic.GraphNode;
import org.junit.jupiter.api.Test;

import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CodeTransformTest {
    String project = "/Users/fy/Documents/MyProjects/slicing_cases";
    String file = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic/case1.java";

    @Test
    void test() throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(new File(file));
        MethodDeclaration n = cu.findFirst(MethodDeclaration.class).get();
    }

    @Test
    void testConcurrencyException() {
        List<GraphNode> nodes = Arrays.asList(new GraphNode(), new GraphNode());
        nodes.forEach(n -> {
            n.setCodeLineNum(100);

        });
    }
}
