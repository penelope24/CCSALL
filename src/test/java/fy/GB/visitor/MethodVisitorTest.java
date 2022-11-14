package fy.GB.visitor;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GB.entry.GBEntry;
import fy.GB.entry.TypeSolverEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Set;

class MethodVisitorTest {
    String project = "/Users/fy/Documents/MyProjects/slicing_cases";
    String javaFile = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic/case1.java";
    MethodDeclaration n;
    VarVisitor varVisitor;

    @BeforeEach
    void init() throws FileNotFoundException {
        HashMap<String, Set<String>> pkg2types = GBEntry.parse_project(project);
        varVisitor = TypeSolverEntry.solveVarTypesInFile(javaFile, pkg2types);
        CompilationUnit cu = StaticJavaParser.parse(new File(javaFile));
        n = cu.findFirst(MethodDeclaration.class).get();
    }

    @Test
    void test () {
        MethodVisitor visitor = new MethodVisitor(varVisitor);
        visitor.build(n);
    }
}