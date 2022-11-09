package fy.CCS.track.data;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.CCD.GW.GitWalk;
import fy.CCD.GW.data.CommitDiff;
import fy.CCD.GW.utils.JGitUtils;
import fy.GB.entry.GBEntry;
import fy.GD.mgraph.MethodPDG;
import fy.GD.mgraph.MethodPDGExporter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DataDependencyTrackerTest {
    MethodPDG graph;
    String output = "/Users/fy/Documents/fyJavaProjects/CCSALL/src/test/output/tmp";

    @BeforeEach
    void init_a_graph() throws FileNotFoundException {
        String project = "/Users/fy/Documents/MyProjects/slicing_cases";
        String file = "/Users/fy/Documents/MyProjects/slicing_cases/custom_slice_cases/basic/case1.java";
        CompilationUnit cu = StaticJavaParser.parse(new File(file));
        MethodDeclaration n = cu.findFirst(MethodDeclaration.class).get();
        graph = GBEntry.one_pass_parse(project, file, n);
    }

    @Test
    void run() {
        System.out.println(graph);
    }
}