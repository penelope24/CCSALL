package fy.CCD.GW;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.CCD.GW.data.CommitDiff;
import fy.CCD.GW.data.FileDiff;
import fy.CCD.GW.data.Hunk;
import fy.CCD.GW.utils.HKHelper;
import fy.CCD.GW.utils.JGitUtils;
import fy.CCD.GW.utils.PathUtils;
import fy.CCS.slicing.PDGBuilder;
import fy.CCS.slicing.SrcCodeTransformer;
import fy.CCS.track.DTEntry;
import fy.GB.entry.TypeSolverEntry;
import fy.GB.visitor.VarVisitor;
import fy.GD.mgraph.MethodPDG;
import fy.jp.JPHelper;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class GitWalk {
    // input
    String projectPath;
    Repository repository;
    JGitUtils jgit;
    // output
    boolean log = false;
    BufferedWriter logger = null;
    String logPath = "";
    String outputBase = "";
    // configure
    int MAX_ENTRY_SIZE = 100;
    int MAX_HUNKS = 20;
    int K_ctrl = 2;
    int a = 3;
    // result
    public List<RevCommit> allCommits = new ArrayList<>();
    public List<CommitDiff> commitDiffs = new LinkedList<>();
    // running stats
    int PDG_BUILD_ERR = 0;
    int SLICING_ERR = 0;

    public GitWalk(String projectPath) {
        this.projectPath = projectPath;
        this.repository = JGitUtils.buildJGitRepository(projectPath);
        this.jgit = new JGitUtils(projectPath);
    }

    public void walk1() throws IOException, GitAPIException {
        jgit.delete_lock_file();
        jgit.reset();
        ObjectId master = JGitUtils.getMaster(repository);
        if (master == null) {
            throw new IllegalStateException("cannot find master head to start traverse");
        }
        RevCommit head = repository.parseCommit(master);
        RevWalk revWalk = new RevWalk(repository);
        revWalk.markStart(head);
        for (RevCommit commit : revWalk) {
            allCommits.add(commit);
        }
    }

    public void walk() {
        if (log) {
            allCommits.forEach(commit -> commitDiffs.add(solve(commit, logger)));
            try {
                logger.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            allCommits.forEach(commit -> commitDiffs.add(solve(commit, null)));
        }
    }

    public void walk(int num) {
        if (log) {
            allCommits.subList(0, num).forEach(commit -> commitDiffs.add(solve(commit, logger)));
            try {
                logger.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            allCommits.subList(0, num).forEach(commit -> commitDiffs.add(solve(commit, null)));
        }
    }

    public void walk(int s, int t) {
        if (log) {
            allCommits.subList(s, t).forEach(commit -> commitDiffs.add(solve(commit, logger)));
            try {
                logger.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            allCommits.subList(s, t).forEach(commit -> commitDiffs.add(solve(commit, null)));
        }
    }

    public CommitDiff solve (RevCommit commit, BufferedWriter logger) {
        RevCommit par = JGitUtils.findFirstParent(repository, commit);
        if (par == null) return null;
        List<DiffEntry> diffEntries = null;
        try {
            diffEntries = JGitUtils.listDiffEntries(repository, par, commit, ".java");
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
        if (diffEntries == null || diffEntries.isEmpty() || diffEntries.size() > MAX_ENTRY_SIZE) return null;
        CommitDiff commitDiff = new CommitDiff(commit, repository, par.getId().name(), commit.getId().name());
        commitDiff.fileDiffs = diffEntries.stream()
                .map(FileDiff::new)
                .collect(Collectors.toList());
        commitDiff.fileDiffs.forEach(fileDiff -> {
            try {
                EditList edits = JGitUtils.getEditList(repository, fileDiff.diffEntry);
                if (edits.size() > MAX_HUNKS) return;
                edits.stream()
                        .map(Hunk::new)
                        .forEach(hunk -> {
                            hunk.commitDiff = commitDiff;
                            hunk.fileDiff = fileDiff;
                            fileDiff.hunks.add(hunk);
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // v1
        {
            try {
                jgit.checkout(par.getId().name());
                HashMap<String, Set<String>> pkg2types = TypeSolverEntry.solve_pkg2types(projectPath);
                commitDiff.fileDiffs.forEach(fileDiff -> {
                    String path = PathUtils.getOldPath(fileDiff.diffEntry, repository);
                    if (path == null) return;
                    fileDiff.path1 = path;
                    VarVisitor varVisitor = TypeSolverEntry.solveVarTypesInFile(path, pkg2types);
                    CompilationUnit cu = JPHelper.getCompilationUnit(path);
                    cu.findAll(MethodDeclaration.class).forEach(n -> n.getRange().ifPresent(r -> {
                        Set<Hunk> insideHunks = fileDiff.hunks.stream()
                                .filter(hunk -> r.contains(hunk.r1))
                                .collect(Collectors.toSet());
                        if (!insideHunks.isEmpty()) {
                            try {
                                PDGBuilder builder = new PDGBuilder(pkg2types, varVisitor);
                                MethodPDG graph = builder.build(n);
                                MethodDeclaration cloned = n.clone();
                                Optional<Node> parOpt = n.getParentNode();
                                int K_data = (graph.vertexCount() / a) + 1;
                                insideHunks.forEach(hunk -> {
                                    List<Integer> chLines = hunk.getRemLines();
                                    if (!chLines.isEmpty()) {
                                        Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, chLines, K_data, K_ctrl);
                                        MethodDeclaration n2 = SrcCodeTransformer.slice(cloned, reservedLines);
                                        parOpt.ifPresent(n2::setParentNode);
                                        hunk.slice1 = builder.build(n2);
                                        if (hunk.slice1 == null) SLICING_ERR++;
                                        hunk.n1 = n;
                                        hunk.graph1 = graph;
                                        if (this.log) {
                                            HKHelper.output1(outputBase, hunk, logger);
                                        }
                                    }
                                });
                            }
                            catch (Exception e) {
                                PDG_BUILD_ERR++;
                            }
                        }
                    }));
                });
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        }
        // v2
        {
            try {
                jgit.checkout(commit.getId().name());
                HashMap<String, Set<String>> pkg2types = TypeSolverEntry.solve_pkg2types(projectPath);
                commitDiff.fileDiffs.forEach(fileDiff -> {
                    String path = PathUtils.getNewPath(fileDiff.diffEntry, repository);
                    if (path == null) return;
                    fileDiff.path2 = path;
                    VarVisitor varVisitor = TypeSolverEntry.solveVarTypesInFile(path, pkg2types);
                    CompilationUnit cu = JPHelper.getCompilationUnit(path);
                    cu.findAll(MethodDeclaration.class).forEach(n -> n.getRange().ifPresent(r -> {
                        Set<Hunk> insideHunks = fileDiff.hunks.stream()
                                .filter(hunk -> r.contains(hunk.r2))
                                .collect(Collectors.toSet());
                        if (!insideHunks.isEmpty()) {
                            try {
                                PDGBuilder builder = new PDGBuilder(pkg2types, varVisitor);
                                MethodPDG graph = builder.build(n);
                                MethodDeclaration cloned = n.clone();
                                Optional<Node> parOpt = n.getParentNode();
                                int K_data = (graph.vertexCount() / a) + 1;
                                insideHunks.forEach(hunk -> {
                                    List<Integer> chLines = hunk.getAddLines();
                                    if (!chLines.isEmpty()) {
                                        Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, chLines, K_data, K_ctrl);
                                        MethodDeclaration n2 = SrcCodeTransformer.slice(cloned, reservedLines);
                                        parOpt.ifPresent(n2::setParentNode);
                                        hunk.slice2 = builder.build(n2);
                                        if (hunk.slice2 == null) SLICING_ERR++;
                                        hunk.n2 = n;
                                        hunk.graph2 = graph;
                                        if (this.log) {
                                            HKHelper.output2(outputBase, hunk, logger);
                                        }
                                    }
                                });
                            }
                            catch (Exception e) {
                                PDG_BUILD_ERR++;
                            }
                        }
                    }));
                });
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        }
        return commitDiff;
    }

    public void check() {
        System.out.println("pdg build fails: " + PDG_BUILD_ERR);
        System.out.println("slicing fails: " + SLICING_ERR);
        System.out.println("total commit diffs: " + commitDiffs.size());
        Set<CommitDiff> validCommitDiffs = commitDiffs.stream()
                .filter(Objects::nonNull)
                .filter(CommitDiff::is_valid)
                .collect(Collectors.toSet());
        System.out.println("total valid commit diffs: " + validCommitDiffs.size());
        Set<Hunk> totalHunkSet = new HashSet<>();
        validCommitDiffs.forEach(commitDiff -> totalHunkSet.addAll(commitDiff.getValidHunks()));
        System.out.println("total valid hunk set size: " + totalHunkSet.size());
        List<Hunk> totalHunkList = new ArrayList<>();
        validCommitDiffs.forEach(commitDiff -> totalHunkList.addAll(commitDiff.getValidHunks()));
        System.out.println("total valid hunk set size: " + totalHunkList.size());
    }



    public void setLogger(String logPath) {
        this.log = true;
        this.logPath = logPath;
        try {
            this.logger = new BufferedWriter(new FileWriter(new File(logPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GitWalk setOutputBase(String outputBase) {
        this.outputBase = outputBase;
        return this;
    }

    public void setMAX_ENTRY_SIZE(int MAX_ENTRY_SIZE) {
        this.MAX_ENTRY_SIZE = MAX_ENTRY_SIZE;
    }

    public void setMAX_HUNKS(int MAX_HUNKS) {
        this.MAX_HUNKS = MAX_HUNKS;
    }

    public void setK_ctrl(int k_ctrl) {
        K_ctrl = k_ctrl;
    }

    public void setA(int a) {
        this.a = a;
    }
}
