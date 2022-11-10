package fy.GB.entry;

import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GB.visitor.GBVisitor;
import fy.GB.visitor.MethodGraphCollect;
import fy.GB.visitor.VarVisitor;
import fy.GD.mgraph.MethodPDG;

import java.io.FileNotFoundException;
import java.util.*;

/**
 *  graph (MethodPDG) build entry
 */
public class GBEntry {

    public static HashMap<String, Set<String>> parse_project(String project) {
        return SolverEntry.solve_pkg2types(project);
    }

    public static VarVisitor parse_file(String javaFile, HashMap<String, Set<String>> pkg2types)  {
        VarVisitor varVisitor = null;
        try {
            varVisitor = SolverEntry.solveVarTypesInFile(javaFile, pkg2types);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (varVisitor == null) return null;
        return varVisitor;
    }

    public static void parse(MethodDeclaration n, MethodGraphCollect collector, List<MethodPDG> graphs) {
        collector.visit(n, graphs);
    }

    public static MethodPDG build (MethodDeclaration n, VarVisitor varVisitor) {
        GBVisitor visitor = new GBVisitor(varVisitor);
        return visitor.build(n);
    }

    public static MethodPDG one_pass_parse(String project, String file, MethodDeclaration n) {
        HashMap<String, Set<String>> pkg2types = parse_project(project);
        VarVisitor varVisitor = null;
        try {
            varVisitor = SolverEntry.solveVarTypesInFile(file, pkg2types);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (varVisitor == null) return null;
        GBVisitor visitor = new GBVisitor(varVisitor);
        return visitor.build(n);
    }
}
