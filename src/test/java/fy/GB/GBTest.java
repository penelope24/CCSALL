package fy.GB;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GB.entry.GBEntry;
import fy.GD.mgraph.MethodPDG;
import fy.GD.mgraph.MethodPDGExporter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class GBTest {


    @Test
    void reproduce () throws FileNotFoundException {
        String project = "/Users/fy/Documents/fyJavaProjects/CCSALL/src/test/java/fy/GB";
        String file = "/Users/fy/Documents/fyJavaProjects/CCSALL/src/test/java/fy/GB/cases/case1.java";
        CompilationUnit cu = StaticJavaParser.parse(new File(file));
        MethodDeclaration n = cu.findFirst(MethodDeclaration.class).get();
        MethodPDG graph = GBEntry.one_pass_parse(project, file, n);
        MethodPDGExporter.export(graph, "/Users/fy/Documents/fyJavaProjects/CCSALL/src/test/java/fy/GB/cases/" + "case1.dot");
    }
}
