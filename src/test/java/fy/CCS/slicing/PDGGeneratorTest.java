package fy.CCS.slicing;

import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GB.entry.TypeSolverEntry;
import fy.GB.visitor.VarVisitor;
import fy.GD.mgraph.MethodPDG;
import fy.jp.JPHelper;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Set;

class PDGGeneratorTest {

    String project = "/Users/fy/Documents/MyProjects/slicing_cases";
    int MAX_DATA_DEPTH = 3;
    int MAX_CTRL_DEPTH = 2;

}