package fy.GB.parse;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TypeSolver {
    private final HashMap<String, Set<String>> package2types;
    private final Set<String> hasDealJavaFiles;
    private final Set<CompilationUnit> hasDealParseTrees;

    public TypeSolver() {
        this.package2types = new HashMap<>();
        this.hasDealJavaFiles = new HashSet<>();
        this.hasDealParseTrees = new HashSet<>();
    }

    public static String solveSimpleTypeName(String simpleTypeName, List<String> allImports, String pkg,
                                             HashMap<String, Set<String>> pkg2types) {
        // find in imports
        for (String str : allImports) {
            if (simpleTypeName.equals(str.substring(str.lastIndexOf(".") + 1))) {
                return str;
            }
        }
        //if not in imports , find in types in current package

        Set<String> currentPackageTypes = pkg2types.get(pkg);
        if (currentPackageTypes != null) {
            for (String packType : currentPackageTypes) {
                if (simpleTypeName.equals(packType.substring(packType.lastIndexOf(".") + 1))) {
                    return packType;
                }
            }
        }
        //if not imports nor package types, then it's java lang types
        return "java.lang." + simpleTypeName;
    }

    public void collect(List<String> javaFiles) {
        javaFiles.forEach(s -> {
            if (!hasDealJavaFiles.contains(s)) {
                try {
                    CompilationUnit cu = StaticJavaParser.parse(new File(s));
                    boolean is_pkg_present = cu.findFirst(PackageDeclaration.class).isPresent();
                    String pkgInfo = is_pkg_present ? cu.findFirst(PackageDeclaration.class).get().getNameAsString() : "";
                    Set<String> typeSet = new HashSet<>();
                    HashMap<TypeDeclaration, String> type2parent = new HashMap<>();
                    Queue<TypeDeclaration> queue = new LinkedList<>();
                    NodeList<TypeDeclaration<?>> types = cu.getTypes();
                    types.forEach(typeDeclaration -> {
                        queue.add(typeDeclaration);
                        type2parent.put(typeDeclaration, pkgInfo);
                    });
                    while (!queue.isEmpty()) {
                        TypeDeclaration tmpType = queue.poll();
                        boolean has_parent = type2parent.get(tmpType).equals("");
                        String currType = has_parent ? tmpType.getNameAsString() :
                                type2parent.get(tmpType) + "." + tmpType.getNameAsString();
                        typeSet.add(currType);
                        tmpType.getChildNodes().forEach(node -> {
                            if (node instanceof TypeDeclaration) {
                                TypeDeclaration td = ((TypeDeclaration) node).asTypeDeclaration();
                                queue.add(td);
                                type2parent.put(td, currType);
                            }
                        });
                    }
                    if (this.package2types.get(pkgInfo) == null) {
                        this.package2types.put(pkgInfo, typeSet);
                    } else {
                        this.package2types.get(pkgInfo).addAll(typeSet);
                    }
                    this.hasDealJavaFiles.add(s);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void collect2(List<CompilationUnit> parseTrees) {
        parseTrees.forEach(cu -> {
            if (!hasDealParseTrees.contains(cu)) {
                boolean is_pkg_present = cu.findFirst(PackageDeclaration.class).isPresent();
                String pkgInfo = is_pkg_present ? cu.findFirst(PackageDeclaration.class).get().getNameAsString() : "";
                Set<String> typeSet = new HashSet<>();
                HashMap<TypeDeclaration, String> type2parent = new HashMap<>();
                Queue<TypeDeclaration> queue = new LinkedList<>();
                NodeList<TypeDeclaration<?>> types = cu.getTypes();
                types.forEach(typeDeclaration -> {
                    queue.add(typeDeclaration);
                    type2parent.put(typeDeclaration, pkgInfo);
                });
                while (!queue.isEmpty()) {
                    TypeDeclaration tmpType = queue.poll();
                    boolean has_parent = type2parent.get(tmpType).equals("");
                    String currType = has_parent ? tmpType.getNameAsString() :
                            type2parent.get(tmpType) + "." + tmpType.getNameAsString();
                    typeSet.add(currType);
                    tmpType.getChildNodes().forEach(node -> {
                        if (node instanceof TypeDeclaration) {
                            TypeDeclaration td = ((TypeDeclaration) node).asTypeDeclaration();
                            queue.add(td);
                            type2parent.put(td, currType);
                        }
                    });
                }
                if (this.package2types.get(pkgInfo) == null) {
                    this.package2types.put(pkgInfo, typeSet);
                } else {
                    this.package2types.get(pkgInfo).addAll(typeSet);
                }
                this.hasDealParseTrees.add(cu);
            }
        });
    }

    public HashMap<String, Set<String>> getPackage2types() {
        return package2types;
    }
}
