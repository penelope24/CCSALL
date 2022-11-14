package fy.GB.entry;

import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GB.visitor.MethodVisitor;
import fy.GB.visitor.VarVisitor;
import fy.GD.mgraph.MethodPDG;

import java.io.FileNotFoundException;
import java.util.*;

/**
 *  graph (MethodPDG) build entry
 */
public class GBEntry {

    public static HashMap<String, Set<String>> parse_project(String project) {
        return TypeSolverEntry.solve_pkg2types(project);
    }

    public static VarVisitor parse_file(String javaFile, HashMap<String, Set<String>> pkg2types)  {
        VarVisitor varVisitor = null;
        try {
            varVisitor = TypeSolverEntry.solveVarTypesInFile(javaFile, pkg2types);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (varVisitor == null) return null;
        return varVisitor;
    }


    public static MethodPDG build (MethodDeclaration n, VarVisitor varVisitor) {
        MethodVisitor visitor = new MethodVisitor(varVisitor);
        return visitor.build(n);
    }

    public static MethodPDG one_pass_parse(String project, String file, MethodDeclaration n) {
        HashMap<String, Set<String>> pkg2types = parse_project(project);
        VarVisitor varVisitor = null;
        try {
            varVisitor = TypeSolverEntry.solveVarTypesInFile(file, pkg2types);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (varVisitor == null) return null;
        MethodVisitor visitor = new MethodVisitor(varVisitor);
        MethodPDG graph = visitor.build(n);
        graph.javaFile = file;
        graph.project = project;
        return graph;
    }
}
