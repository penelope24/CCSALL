package fy.CCS;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import fy.CCS.slicing.PDGBuilder;
import fy.CCS.slicing.SrcCodeTransformer;
import fy.CCS.track.DTEntry;
import fy.GD.basic.GraphNode;
import fy.GD.mgraph.MethodPDG;
import fy.utils.file.JavaFileUtils;
import fy.utils.git.JGitUtils;
import fy.utils.tools.JPHelper;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
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

    @Test
    void test_node_equality() {
        String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/advance/nested2.java";
        int start = 16;
        MethodDeclaration n = JPHelper.getCompilationUnit(javaFile).findFirst(MethodDeclaration.class).get();
        MethodDeclaration nClone = n.clone();
        MethodPDG graph = PDGBuilder.one_pass_parse(project, javaFile, n);
        Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, start, MAX_DATA_DEPTH, MAX_CTRL_DEPTH);
        MethodDeclaration slice = SrcCodeTransformer.slice(nClone, reservedLines);
        n.getParentNode().ifPresent(slice::setParentNode);
        MethodPDG g = PDGBuilder.one_pass_parse(project, javaFile, slice);
        g.copyVertexSet().forEach(v -> {
            GraphNode node = graph.copyVertexSet().stream().filter(n1 -> n1.equals(v)).findFirst().orElse(null);
            assert node != null;
        });
    }

    @Test
    void bug1() throws GitAPIException {
        // scenario
        String project = "/Users/fy/Documents/MyProjects/CodeChangeDataSet/gerrit";
        String v = "f073e35b89c90f3b58978303d0a0b4fb91d85538";
        String javaFile = "/Users/fy/Documents/MyProjects/CodeChangeDataSet/gerrit/java/com/google/gerrit/asciidoctor/DocIndexer.java";
        String methodName = "index";
        List<Integer> chLines = Arrays.asList(113, 114);
        // locate
        JGitUtils jgit = new JGitUtils(project);
        jgit.checkout(v);
        System.out.println("total lines : " + JavaFileUtils.countSourceLineNum(javaFile));
        CompilationUnit cu = JPHelper.getCompilationUnit(javaFile);
        MethodDeclaration n = cu.findAll(MethodDeclaration.class).stream()
                .filter(md -> md.getNameAsString().contains(methodName))
                .findFirst().orElse(null);
//        System.out.println(n);
//        System.out.println(n.getRange().get());
        // reproduce
        MethodPDG graph = PDGBuilder.one_pass_parse(project, javaFile, n);
        Set<Integer> reserved = DTEntry.dependencyTrack(graph, chLines, 3,2);
        System.out.println(reserved);
        MethodDeclaration clone = n.clone();
        MethodDeclaration n2 = SrcCodeTransformer.slice(clone, reserved);
    }
}
