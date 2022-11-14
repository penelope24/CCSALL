package fy.CCS.slicing;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import fy.CCS.track.DTEntry;
import fy.CCS.traverse.BreadthFirstTraversal;
import fy.GB.entry.GBEntry;
import fy.GD.basic.GraphNode;
import fy.GD.mgraph.MethodPDG;
import fy.GD.mgraph.MethodPDGExporter;
import fy.TestHelper;
import fy.jp.JPHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SlicerTest {

    String project = "/Users/fy/Documents/MyProjects/slicing_cases";
    String output = "/Users/fy/Documents/fyJavaProjects/CCSALL/src/test/java/fy/CCS/slicing/output";


    public void analyze_basic_case (int idx, int start) {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic" + "/case" + idx + ".java";
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findFirst(MethodDeclaration.class).get();
        MethodPDG graph = GBEntry.one_pass_parse(project, javaFile, n);
        Slicer slicer = new Slicer(graph);
        MethodPDG slice = slicer.slice(start);

    }

    @Test
    void base_case1() {
        analyze_basic_case(1, 21);
    }

    @Test
    /**
     *  max data depth should be 2 for this case;
     */
    void base_case2() {
        analyze_basic_case(2, 22);
    }

    @Test
    void base_case3() {
        analyze_basic_case(3, 16);
    }

    @Test
    void base_case4() {
        analyze_basic_case(4, 10);
    }

    @Test
    void base_case5() {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic" + "/case" + 5 + ".java";
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findFirst(MethodDeclaration.class).get();
        MethodDeclaration n2 = SrcCodeTransformer.slice(n, Arrays.asList(5,7,8,10,11,15));
        System.out.println(n2);
    }

    @Test
    void test () {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic" + "/case" + 5 + ".java";
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findFirst(MethodDeclaration.class).get();
        WhileStmt whileStmt = n.findFirst(WhileStmt.class).get();
        BlockStmt whileBlock = whileStmt.getBody().asBlockStmt();
        IfStmt ifStmt = n.findFirst(IfStmt.class).get();
        BlockStmt ifBlock = ifStmt.getThenStmt().asBlockStmt();
        BlockStmt ifBlockClone = ifBlock.clone();
        ifBlockClone.remove(ifBlockClone.getStatement(0));

    }

}