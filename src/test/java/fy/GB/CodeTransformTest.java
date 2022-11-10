package fy.GB;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.CCS.slicing.SrcCodeTransformer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class CodeTransformTest {
    String project = "/Users/fy/Documents/MyProjects/slicing_cases";
    String file = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic/case1.java";

    @Test
    void test() throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(new File(file));
        MethodDeclaration n = cu.findFirst(MethodDeclaration.class).get();
        MethodDeclaration n2= SrcCodeTransformer.removeLinesFromMethod(n, Arrays.asList(13, 12));
        System.out.println(n2);
    }
}
