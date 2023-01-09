package fy.CCD.GW.data;

import com.github.javaparser.ast.CompilationUnit;
import fy.GB.entry.TypeSolverEntry;
import fy.utils.file.SubFileFinder;
import fy.utils.git.JGitUtils;
import fy.utils.git.Validator;
import fy.utils.log.Logger;
import fy.utils.tools.JPHelper;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.*;
import java.util.stream.Collectors;

public class TypeStatus {

    RevCommit commit; // current commit
    HashMap<String, Set<String>> pkg2types = new HashMap<>();

    public TypeStatus(JGitUtils jgit, RevCommit commit, Logger logger) {
        init(jgit, commit, logger);
    }

    private void init(JGitUtils jgit, RevCommit commit, Logger logger) {
        try {
            jgit.checkout(commit.getId().name());
            List<String> allJavaFiles = SubFileFinder.findAllJavaFiles(jgit.project_path);
            List<String> validJavaFiles = allJavaFiles.stream()
                    .filter(s -> Validator.isJavaFileValid(s, logger.config))
                    .collect(Collectors.toList());
            List<CompilationUnit> parseTrees = validJavaFiles.stream()
                    .map(s -> JPHelper.getCompilationUnitWithLog(s, logger))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            pkg2types = TypeSolverEntry.solve_pkg2types(parseTrees);
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public void add(List<CompilationUnit> parseTrees) {
        Map<String, Set<String>> map = TypeSolverEntry.solve_pkg2types(parseTrees);
        map.forEach((pkg, types) -> pkg2types.put(pkg, types));
    }

    public void rem(List<CompilationUnit> parseTrees) {
        Map<String, Set<String>> map = TypeSolverEntry.solve_pkg2types(parseTrees);
        map.forEach((pkg, types) -> pkg2types.remove(pkg, types));
    }


    public HashMap<String, Set<String>> getPkg2types() {
        return pkg2types;
    }

    public RevCommit getCommit() {
        return commit;
    }
}
