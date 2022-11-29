package fy.CCS;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import fy.CCS.slicing.PDGBuilder;
import fy.CCS.slicing.SrcCodeTransformer;
import fy.CCS.track.DTEntry;
import fy.GD.mgraph.MethodPDG;
import fy.jp.JPHelper;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * test sets for code change slice module
 */
public class CCSTest {
    String project = "/Users/fy/Documents/MyProjects/slicing_cases";
    int MAX_DATA_DEPTH = 3;
    int MAX_CTRL_DEPTH = 2;

    public MethodDeclaration analyze_basic_case (int idx, int start) {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic" + "/case" + idx + ".java";
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findFirst(MethodDeclaration.class).get();
        MethodDeclaration nClone = n.clone();
        MethodPDG graph = PDGBuilder.one_pass_parse(project, javaFile, n);
        Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, start, MAX_DATA_DEPTH, MAX_CTRL_DEPTH);
        MethodDeclaration slice = SrcCodeTransformer.slice(nClone, reservedLines);
        return slice;
    }

    @Test
    void base_case1() {
        MethodDeclaration slice = analyze_basic_case(1, 9);
        System.out.println(slice);
    }

    @Test
    /**
     *  max data depth should be 2 for this case;
     */
    void base_case2() {
        MethodDeclaration slice = analyze_basic_case(2, 18);
        System.out.println(slice);
    }

    @Test
    void base_case3() {
        MethodDeclaration slice = analyze_basic_case(3, 24);
        System.out.println(slice);
    }

    @Test
    // fixme results have issue with else branch of an if stmt.
    // todo add handling for null cases.
    void base_case4() {
        MethodDeclaration slice = analyze_basic_case(4, 14);
        System.out.println(slice);
    }

    @Test
    void test_case5() {
        MethodDeclaration slice = analyze_basic_case(5, 16);
        System.out.println(slice);
    }

    @Test
    void test_case6() {
        MethodDeclaration slice = analyze_basic_case(6, 21);
        System.out.println(slice);
    }

    @Test
    void test__advance_lambda1() {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/advance/lambda/ForEach.java";
        int start = 12;
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findFirst(MethodDeclaration.class).get();
        MethodDeclaration nClone = n.clone();
        MethodPDG graph = PDGBuilder.one_pass_parse(project, javaFile, n);
        Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, start, MAX_DATA_DEPTH, MAX_CTRL_DEPTH);
        MethodDeclaration slice = SrcCodeTransformer.slice(nClone, reservedLines);
        System.out.println(slice);
    }

    @Test
    void test_advance_lambda2() {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/advance/lambda/stream_api.java";
        int start = 11;
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findFirst(MethodDeclaration.class).get();
        MethodDeclaration nClone = n.clone();
        MethodPDG graph = PDGBuilder.one_pass_parse(project, javaFile, n);
        Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, start, MAX_DATA_DEPTH, MAX_CTRL_DEPTH);
        MethodDeclaration slice = SrcCodeTransformer.slice(nClone, reservedLines);
        n.getParentNode().ifPresent(slice::setParentNode);
        System.out.println(slice);
        MethodPDG g = PDGBuilder.one_pass_parse(project, javaFile, slice);
        System.out.println(g);
    }

    @Test
    void test_advance_anonymous() {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/advance/anonymous.java";
        int start = 16;
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findAll(MethodDeclaration.class).get(1);
        MethodDeclaration nClone = n.clone();
        MethodPDG graph = PDGBuilder.one_pass_parse(project, javaFile, n);
        Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, start, MAX_DATA_DEPTH, MAX_CTRL_DEPTH);
        MethodDeclaration slice = SrcCodeTransformer.slice(nClone, reservedLines);
        n.getParentNode().ifPresent(slice::setParentNode);
        System.out.println(slice);
        MethodPDG g = PDGBuilder.one_pass_parse(project, javaFile, slice);
        System.out.println(g);
    }

    @Test
    void test_advance_nested1() {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/advance/nested1.java";
        int start = 20;
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findFirst(MethodDeclaration.class).get();
        MethodDeclaration nClone = n.clone();
        MethodPDG graph = PDGBuilder.one_pass_parse(project, javaFile, n);
        Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, start, MAX_DATA_DEPTH, MAX_CTRL_DEPTH);
        MethodDeclaration slice = SrcCodeTransformer.slice(nClone, reservedLines);
        n.getParentNode().ifPresent(slice::setParentNode);
        System.out.println(slice);
        MethodPDG g = PDGBuilder.one_pass_parse(project, javaFile, slice);
        System.out.println(g);
    }

    @Test
    void test_advance_nested2() {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/advance/nested2.java";
        int start = 16;
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findFirst(MethodDeclaration.class).get();
        MethodDeclaration nClone = n.clone();
        MethodPDG graph = PDGBuilder.one_pass_parse(project, javaFile, n);
        Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, start, MAX_DATA_DEPTH, MAX_CTRL_DEPTH);
        MethodDeclaration slice = SrcCodeTransformer.slice(nClone, reservedLines);
        n.getParentNode().ifPresent(slice::setParentNode);
        System.out.println(slice);
        MethodPDG g = PDGBuilder.one_pass_parse(project, javaFile, slice);
        System.out.println(g);
    }

    @Test
    void testcase1() {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/testcase/SingleReturn.java";
        int start = 4;
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findFirst(MethodDeclaration.class).get();
        MethodDeclaration nClone = n.clone();
        MethodPDG graph = PDGBuilder.one_pass_parse(project, javaFile, n);
        Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, start, MAX_DATA_DEPTH, MAX_CTRL_DEPTH);
        MethodDeclaration slice = SrcCodeTransformer.slice(nClone, reservedLines);
        n.getParentNode().ifPresent(slice::setParentNode);
        System.out.println(slice);

    }

    @Test
    void test_find_par_block() {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic" + "/case" + 4 + ".java";
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findFirst(MethodDeclaration.class).get();
        Set<Integer> allLines = IntStream.range(n.getRange().get().begin.line,
                 n.getRange().get().end.line)
                .boxed().collect(Collectors.toSet());
        BlockStmt elseStmt = n.findAll(BlockStmt.class).stream()
                .filter(blockStmt -> blockStmt.getRange().get().begin.line == 16)
                .findFirst().get();
        BlockStmt parBlock = SrcCodeTransformer.findValidParentBlock(elseStmt, allLines);
        SrcCodeTransformer.print(parBlock);
    }

    @Test
    void test_find_containing_stmt() {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic" + "/case" + 4 + ".java";
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findFirst(MethodDeclaration.class).get();
        BlockStmt elseStmt = n.findAll(BlockStmt.class).stream()
                .filter(blockStmt -> blockStmt.getRange().get().begin.line == 16)
                .findFirst().get();
        Node stmt = SrcCodeTransformer.findContainingStmt(elseStmt);
        SrcCodeTransformer.print(stmt);
    }

    @Test
    void test_traverse_blocks() {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic/case1.java";
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findFirst(MethodDeclaration.class).get();
        Node.PreOrderIterator iterator = new Node.PreOrderIterator(n);
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node instanceof BlockStmt) {
                System.out.println(node.getRange().get());
            }
        }
    }
}
