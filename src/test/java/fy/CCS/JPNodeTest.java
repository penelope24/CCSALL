package fy.CCS;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.jp.JPHelper;
import org.junit.jupiter.api.Test;

public class JPNodeTest {

    @Test
    void t1 () {
        String javaFile = "/Users/fy/Documents/fyJavaProjects/CCSALL/src/test/java/fy/CCS/cases/NodeWithConditionExample.java";
        CompilationUnit cu = JPHelper.getCompilationUnit(javaFile);
        MethodDeclaration n = cu.findFirst(MethodDeclaration.class).get();
        System.out.println(n);
    }
}
