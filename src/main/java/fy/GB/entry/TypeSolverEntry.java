package fy.GB.entry;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import fy.GB.parse.TypeSolver;
import fy.GB.visitor.VarVisitor;
import fy.file.SubFileFinder;

import java.io.File;
import java.io.FileNotFoundException;
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

    public static VarVisitor solveVarTypesInFile(String javaFile, HashMap<String, Set<String>> pkg2types) {
        CompilationUnit cu = null;
        try {
            cu = StaticJavaParser.parse(new File(javaFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        VarVisitor varVisitor = new VarVisitor(pkg2types, cu);
        varVisitor.analyseFieldTypes();
        return varVisitor;
    }

}
