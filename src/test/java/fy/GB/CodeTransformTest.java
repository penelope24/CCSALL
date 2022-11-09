package fy.GB;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GB.slicing.SrcCodeTransformer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CodeTransformTest {
    String project = "/Users/fy/Documents/MyProjects/slicing_cases";
    String file = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic/case1.java";

    @Test
    void test() throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(new File(file));
        MethodDeclaration n = cu.findFirst(MethodDeclaration.class).get();
        MethodDeclaration n2 = SrcCodeTransformer.reserveLinesFromMethod(n, Arrays.asList(6, 9, 20));
        System.out.println(n2);
    }
}
