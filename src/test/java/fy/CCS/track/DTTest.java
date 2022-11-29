package fy.CCS.track;

import com.github.javaparser.ast.body.MethodDeclaration;
import fy.CCS.slicing.PDGBuilder;
import fy.GD.mgraph.MethodPDG;
import fy.jp.JPHelper;
import org.junit.jupiter.api.Test;

public class DTTest {

    String project = "/Users/fy/Documents/MyProjects/slicing_cases";
    int MAX_DATA_DEPTH = 3;
    int MAX_CTRL_DEPTH = 1;

    @Test
    // dependency tracking of else branch in if stmt
    void test_case1() {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic" + "/case" + 4 + ".java";
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findFirst(MethodDeclaration.class).get();
        MethodPDG graph = PDGBuilder.one_pass_parse(project, javaFile, n);
        DTEntry.dependencyTrack(graph, 17, MAX_DATA_DEPTH, MAX_CTRL_DEPTH);
    }
}
