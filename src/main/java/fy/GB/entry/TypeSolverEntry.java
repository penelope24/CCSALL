package fy.GB.entry;

import com.github.javaparser.ast.CompilationUnit;
import fy.GB.parse.TypeSolver;
import fy.GB.visitor.VarVisitor;
import fy.utils.file.SubFileFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TypeSolverEntry {

    public static HashMap<String, Set<String>> solve_pkg2types(String project) {
        List<String> allJavaFiles = new ArrayList<>();
        // find java files
        allJavaFiles = SubFileFinder.findAllJavaFiles(project);
        TypeSolver typeSolver = new TypeSolver();
        typeSolver.collect(allJavaFiles);
        return typeSolver.getPackage2types();
    }

    public static HashMap<String, Set<String>> solve_pkg2types(List<CompilationUnit> allParseTrees) {
        TypeSolver typeSolver = new TypeSolver();
        typeSolver.collect2(allParseTrees);
        return typeSolver.getPackage2types();
    }

    public static VarVisitor solveVarTypesInFile(CompilationUnit cu, HashMap<String, Set<String>> pkg2types) {
        VarVisitor varVisitor = new VarVisitor(pkg2types, cu);
        varVisitor.analyseFieldTypes();
        return varVisitor;
    }

}
