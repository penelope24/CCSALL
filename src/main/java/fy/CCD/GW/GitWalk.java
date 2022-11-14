package fy.CCD.GW;


import fy.CCD.GW.data.CommitDiff;
import fy.CCD.GW.data.FileDiff;
import fy.CCD.GW.data.Hunk;
import fy.CCD.GW.utils.JGitUtils;
import fy.CCD.GW.utils.PathUtils;
import fy.GB.entry.GBEntry;
import fy.GB.entry.GBConfig;
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
    String outputPath = "/Users/fy/Documents/fyJavaProjects/CCData/src/test/resources";
    // configure
    int MAX_ENTRY_SIZE = 100;
    int MAX_HUNKS = 20;
    // output
    public List<RevCommit> allCommits = new ArrayList<>();
    public List<CommitDiff> commitDiffs = new LinkedList<>();
    // running stats
    int count = 0;
    int PDG_BUILD_SUCC = 0;
    int PDG_BUILD_ERR = 0;
    int TOTAL_CHECKOUT_TIMES = 0;
    int TOTAL_COMMIT_DIFF_CREATE_TIMES = 0;
    // graph properties
    Properties prop = GBConfig.loadProperties();

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
        allCommits.forEach(commit -> {
            CommitDiff commitDiff = solve(commit);
            if (commitDiff != null) {
                commitDiffs.add(commitDiff);
            }
        });
    }

    public void walk(int num) {
        allCommits.subList(0, num).forEach(commit -> {
            CommitDiff commitDiff = solve(commit);
            if (commitDiff != null) {
                commitDiffs.add(commitDiff);
            }
        });
    }

    public void walk(int s, int t) {
        allCommits.subList(s, t).forEach(commit -> {
            CommitDiff commitDiff = solve(commit);
            if (commitDiff != null) {
                commitDiffs.add(commitDiff);
            }
        });
    }

    public CommitDiff solve (RevCommit commit)  {
        RevCommit par = JGitUtils.findFirstParent(repository, commit);
        if (par == null) return null;
        List<DiffEntry> diffEntries = null;
        try {
            diffEntries = JGitUtils.listDiffEntries(repository, par, commit, ".java");
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
        if (diffEntries == null || diffEntries.isEmpty() || diffEntries.size() > MAX_ENTRY_SIZE) return null;
        CommitDiff commitDiff = new CommitDiff(repository, par.getId().name(), commit.getId().name());
        TOTAL_COMMIT_DIFF_CREATE_TIMES++;
        commitDiff.fileDiffs = diffEntries.stream()
                .map(FileDiff::new)
                .collect(Collectors.toList());
        commitDiff.fileDiffs.forEach(fileDiff -> {
            try {
                EditList edits = JGitUtils.getEditList(repository, fileDiff.diffEntry);
                fileDiff.hunks = edits.stream()
                        .map(Hunk::new)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // v1
        {
            try {
                jgit.checkout(par.getId().name());
                TOTAL_CHECKOUT_TIMES++;
                HashMap<String, Set<String>> pkg2types = GBEntry.parse_project(projectPath);
                commitDiff.fileDiffs.forEach(fileDiff -> {
                    String path = PathUtils.getOldPath(fileDiff.diffEntry, repository);
                    if (path == null) return;
                    fileDiff.path1 = path;
//                    MethodGraphCollect collector = GBEntry.parse_file(path, pkg2types);
//                    if (collector == null) return;
//                    try {
//                        CompilationUnit cu = StaticJavaParser.parse(new File(path));
//                        List<MethodDeclaration> mds = cu.findAll(MethodDeclaration.class);
//                        fileDiff.hunks.forEach(hunk -> {
//                            mds.stream()
//                                    .filter(md -> md.getRange().isPresent())
//                                    .filter(md -> md.getRange().get().contains(hunk.r1))
//                                    .findFirst().ifPresent(n -> hunk.n1 = n);
//                        });
//                        Set<MethodDeclaration> ns = fileDiff.hunks.stream()
//                                .map(hunk -> hunk.n1)
//                                .filter(Objects::nonNull)
//                                .collect(Collectors.toSet());
//                        List<MethodPDG> graphs = new LinkedList<>();
//                        ns.forEach(n -> GBEntry.parse(n, collector, graphs));
//                        graphs.forEach(graph -> {
//                            List<Hunk> containedHunks = fileDiff.hunks.stream()
//                                    .filter(hunk -> hunk.n1 == graph.n)
//                                    .collect(Collectors.toList());
//                            containedHunks.forEach(h -> {
//                                h.graph1 = graph;
//                                commitDiff.ccMap.put(graph, h);
//                            });
//                        });
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
                });
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        }
        return commitDiff;
    }

    public void check() {
        System.out.println(TOTAL_CHECKOUT_TIMES);
        System.out.println(TOTAL_COMMIT_DIFF_CREATE_TIMES);
        System.out.println(commitDiffs.size());
        List<CommitDiff> validCommitDiffs = commitDiffs.stream()
                .filter(CommitDiff::is_valid)
                .collect(Collectors.toList());
        System.out.println(validCommitDiffs.size());
    }

    public void log(String logFile) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(logFile)));
        List<CommitDiff> validCommitDiffs = commitDiffs.stream()
                .filter(CommitDiff::is_valid)
                .collect(Collectors.toList());
        validCommitDiffs.forEach(commitDiff -> {
            try {
                bw.write("current version: " + commitDiff.v2);
                bw.newLine();
                commitDiff.fileDiffs.forEach(fileDiff -> {
                    try {
                        bw.write("changed file : " + commitDiff.fileDiffs.indexOf(fileDiff));
                        bw.newLine();
                        fileDiff.hunks.forEach(hunk -> {
                            try {
                                bw.write(" hunk " + fileDiff.hunks.indexOf(hunk));
                                bw.newLine();
                                boolean exist1 = hunk.graph1 != null;
                                bw.write("graph 1: " + exist1);
                                bw.newLine();
                                boolean exist2 = hunk.graph2 != null;
                                bw.write("graph 2: " + exist2);
                                bw.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                bw.newLine();
                bw.newLine();
                bw.newLine();
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bw.close();
    }

}
