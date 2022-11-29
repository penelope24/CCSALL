package fy.CCS.slicing;

import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GB.entry.TypeSolverEntry;
import fy.GB.visitor.MethodVisitor;
import fy.GB.visitor.VarVisitor;
import fy.GD.mgraph.MethodPDG;

import java.util.HashMap;
import java.util.Set;

public class PDGBuilder {

    HashMap<String, Set<String>> pkg2types;
    VarVisitor varVisitor;
    MethodVisitor methodVisitor;

    public PDGBuilder(HashMap<String, Set<String>> pkg2types, VarVisitor varVisitor) {
        this.pkg2types = pkg2types;
        this.varVisitor = varVisitor;
        this.methodVisitor = new MethodVisitor(varVisitor);
    }

    public MethodPDG build (MethodDeclaration n) {
        if (this.varVisitor == null || this.methodVisitor == null || n == null) {
            return null;
        }
        return this.methodVisitor.build(n);
    }

    public static MethodPDG one_pass_parse(String project, String file, MethodDeclaration n) {
        HashMap<String, Set<String>> pkg2types = TypeSolverEntry.solve_pkg2types(project);
        VarVisitor varVisitor;
        varVisitor = TypeSolverEntry.solveVarTypesInFile(file, pkg2types);
        if (varVisitor == null) return null;
        MethodVisitor visitor = new MethodVisitor(varVisitor);
        MethodPDG graph = visitor.build(n);
        assert graph != null;
        graph.javaFile = file;
        graph.project = project;
        return graph;
    }

}
