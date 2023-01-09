package fy.CCD.GW;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import fy.CCD.GW.data.*;
import fy.CCS.slicing.PDGBuilder;
import fy.CCS.slicing.SrcCodeTransformer;
import fy.CCS.track.DTEntry;
import fy.Config;
import fy.GB.entry.TypeSolverEntry;
import fy.GB.visitor.VarVisitor;
import fy.GD.mgraph.MethodPDG;
import fy.GIO.export.DotExporter;
import fy.GIO.export.JsonExporter;
import fy.GIO.object.GraphObject;
import fy.utils.file.PathUtils;
import fy.utils.git.DiffEntryHelper;
import fy.utils.git.JGitUtils;
import fy.utils.git.Validator;
import fy.utils.heap.HeapChecker;
import fy.utils.log.Logger;
import fy.utils.tools.JPHelper;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 *  pseudo-greedy checking out mode
 *  progressive type solving mode
 */
public class LineSolver {
    GitWalkContinuous walker;
    CommitLine commitLine;
    List<Delta> deltaList;
    RevCommit head;
    Config config;
    JGitUtils jgit;
    Repository repository;
    String currVersion;
    TypeStatus currStatus;
    // stats
    int analyzed_commits = 0;
    int valid_commits = 0;
    int valid_but_empty_commits = 0;
    int total_checkout_times = 1;
    int status_creates = 1; // first time build is in constructor
    int status_updates = 0;
    int graph_analyzed = 0;
    int gb_succ = 0;
    int slice_succ = 0;
    int total_err = 0;
    // res
    List<CommitDiff> commitDiffs = new LinkedList<>();
    // log
    Logger logger;


    public LineSolver(CommitLine commitLine, GitWalkContinuous walker) {
        this.config = walker.config;
        this.logger = walker.logger;
        this.walker = walker;
        this.commitLine = commitLine;
        this.deltaList = commitLine.getDeltaList();
        this.jgit = walker.jgit;
        this.repository = walker.repository;
        this.head = commitLine.getHead();
        this.currVersion = head.getId().name();
        this.currStatus = new TypeStatus(jgit, head, logger);
    }

    public void walk() throws GitAPIException, IOException {
        int n = commitLine.getSize();
        for (int i=0; i<n-1; i++) {
            System.out.println("at : " + i);
            CommitDiff commitDiff = solve(i);
            if (commitDiff != null) {
                commitDiffs.add(commitDiff);
            }
            if (i % config.output_batch == 0 && i != 0) {
                output(i);
                commitDiffs.clear();
                System.gc();
            }
        }
        output(n-2);
    }

    public void singleWalk(int i) throws GitAPIException, FileNotFoundException {
        assert i >= 0 && i < commitLine.getSize()-1;
        RevCommit commit = commitLine.getCommitByIndex(i);
        this.currVersion = commit.getId().name();
        this.currStatus = new TypeStatus(jgit, commit, logger);
        solve(i);
    }

    public CommitDiff solve(int i) throws GitAPIException, FileNotFoundException {
        analyzed_commits++;
        RevCommit curr = commitLine.getCommitByIndex(i);
        RevCommit next = commitLine.getCommitByIndex(i+1);
        String v = curr.getId().name();
        assert currVersion.equals(v);
        List<DiffEntry> diffEntries = DiffEntryHelper.getDiffEntryList(jgit, next, curr);
        boolean valid = Validator.isDiffEntryListValid(diffEntries, config);
        if (valid) {
            // build up commit diff
            CommitDiff commitDiff = new CommitDiff(curr, repository, next.getId().name(), curr.getId().name());
            buildCommitDiff(commitDiff, diffEntries);
            valid_commits++;
            // curr
            {
                try {
                    analyze(commitDiff, currStatus, "v2");
                } catch (Exception | Error e) {
                    logger.log(e, "uncaught");
                }
            }
            // next
            {
                List<CompilationUnit> addedParseTrees = getAddedParseTrees(diffEntries);
                String v1 = next.getId().name();
                assert !currVersion.equals(v1);
                jgit.checkout(v1);
                total_checkout_times++;
                currVersion = v1;
                // type solving
                List<CompilationUnit> removedParseTrees = getRemovedParseTrees(diffEntries);
                currStatus.add(removedParseTrees);
                currStatus.rem(addedParseTrees);
                status_updates++;
                // analyze
                try {
                    analyze(commitDiff, currStatus, "v1");
                } catch (Exception | Error e) {
                    logger.log(e, "uncaught");
                }
            }
            return commitDiff;
        }
        else {
            // only solve types, not parsing
            List<CompilationUnit> addedParseTrees = getAddedParseTrees(diffEntries);
            String v1 = next.getId().name();
            jgit.checkout(v1);
            total_checkout_times++;
            currVersion = v1;
            List<CompilationUnit> removedParseTrees = getRemovedParseTrees(diffEntries);
            currStatus.rem(addedParseTrees);
            currStatus.add(removedParseTrees);
            status_updates++;
            return null;
        }
    }

    public void analyze(CommitDiff commitDiff, TypeStatus currStatus, String v) {
        HashMap<String, Set<String>> pkg2types = currStatus.getPkg2types();
        commitDiff.fileDiffs.forEach(fileDiff -> {
            String path = v.equals("v1") ? fileDiff.path1 : fileDiff.path2;
            if (!Validator.isJavaFileValid(path, config)) return;
            CompilationUnit cu = JPHelper.getCompilationUnitWithLog(path, logger);
            if (cu == null) {
                total_err++;
                return;
            }
            VarVisitor varVisitor = TypeSolverEntry.solveVarTypesInFile(cu, pkg2types);
            List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
            Multimap<MethodDeclaration, Hunk> validMap = ArrayListMultimap.create();
            if (v.equals("v1")) {
                fileDiff.hunks.forEach(hunk -> methods.stream()
                        .filter(n -> n.getRange().isPresent())
                        .filter(n -> n.getRange().get().contains(hunk.r1))
                        .findFirst().ifPresent(enclosingMethod -> validMap.put(enclosingMethod, hunk)));
            }
            else {
                fileDiff.hunks.forEach(hunk -> methods.stream()
                        .filter(n -> n.getRange().isPresent())
                        .filter(n -> n.getRange().get().contains(hunk.r2))
                        .findFirst().ifPresent(enclosingMethod -> validMap.put(enclosingMethod, hunk)));
            }
            validMap.keySet().forEach(n -> {
                Set<Hunk> insides = new HashSet<>(validMap.get(n));
                try {
                    MethodDeclaration cloned = n.clone();
                    Optional<Node> parOpt = n.getParentNode();
                    PDGBuilder builder = new PDGBuilder(pkg2types, varVisitor);
                    MethodPDG graph = builder.build(n);
                    graph_analyzed++;
                    gb_succ++;
                    int k_data = (graph.vertexCount() / config.a) + 1;
                    AtomicInteger idx = new AtomicInteger();
                    insides.forEach(hunk -> {
                        List<Integer> chLines = hunk.getRemLines();
                        if (!chLines.isEmpty()) {
                            Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, chLines, k_data, config.k_ctrl);
                            MethodDeclaration slice = SrcCodeTransformer.slice(cloned, reservedLines); // fixme index out of bounds exception
                            parOpt.ifPresent(slice::setParentNode);
                            MethodPDG slicedSubGraph = builder.build(slice); // incomplete
                            PDGBuilder.sliceAsProperty(graph, slicedSubGraph, idx.getAndIncrement());
                            slice_succ++;
                        }
                    });
                    if (v.equals("v1")) {
                        graph.slice_num1 = idx.get();
                        graph.commitId = commitDiff.getCurrentVersion();
                        graph.simpleName = fileDiff.getSimpleName();
                        fileDiff.graphs1.add(graph);
                    }
                    else {
                        graph.slice_num2 = idx.get();
                        graph.commitId = commitDiff.getCurrentVersion();
                        graph.simpleName = fileDiff.getSimpleName();
                        fileDiff.graphs2.add(graph);
                    }
                }
                catch (Exception | Error e) {
//                    e.printStackTrace();
                    HeapChecker.print();
                    total_err++;
                    logger.log(e, null);
                }
            });
        });
    }

    public void buildCommitDiff(CommitDiff commitDiff, List<DiffEntry> diffEntries) {
        List<FileDiff> fileDiffs = diffEntries.stream()
                .filter(entry -> entry.getChangeType() == DiffEntry.ChangeType.ADD
                        || entry.getChangeType() == DiffEntry.ChangeType.DELETE
                        || entry.getChangeType() == DiffEntry.ChangeType.MODIFY)
                .map(entry -> new FileDiff(entry, repository))
                .collect(Collectors.toList());
        if (config.skip_entry_tests) {
            fileDiffs.removeIf(fileDiff -> fileDiff.getFullName().toLowerCase().contains("test"));
        }
        if (fileDiffs.isEmpty()) {
            return;
        }
        commitDiff.fileDiffs.addAll(fileDiffs);
        commitDiff.fileDiffs.forEach(fileDiff -> {
            try {
                EditList edits = JGitUtils.getEditList(repository, fileDiff.diffEntry);
                if (edits.size() > config.max_hunk_num) return;
                edits.stream()
                        .map(Hunk::new)
                        .filter(hunk -> hunk.getMaxEditSize() <= config.max_hunk_size)
                        .forEach(hunk -> fileDiff.hunks.add(hunk));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public List<CompilationUnit> getAddedParseTrees(List<DiffEntry> diffEntries) throws FileNotFoundException {
        List<String> addedJavaFiles = diffEntries.stream()
                .map(entry -> PathUtils.getNewPath(entry, repository))
                .filter(Objects::nonNull)
                .filter(s -> Validator.isJavaFileValid(s, config))
                .collect(Collectors.toList());
        return getCompilationUnits(addedJavaFiles);
    }


    public List<CompilationUnit> getRemovedParseTrees(List<DiffEntry> diffEntries) throws FileNotFoundException {
        List<String> removedJavaFiles = diffEntries.stream()
                .map(entry -> PathUtils.getOldPath(entry, repository))
                .filter(Objects::nonNull)
                .filter(s -> Validator.isJavaFileValid(s, config))
                .collect(Collectors.toList());
        return getCompilationUnits(removedJavaFiles);
    }

    private List<CompilationUnit> getCompilationUnits(List<String> javaFiles) {
        List<CompilationUnit> parseTrees = new ArrayList<>();
        for (String javaFile : javaFiles) {
            CompilationUnit cu = JPHelper.getCompilationUnitWithLog(javaFile, logger);
            if (cu == null) {
                total_err++;
            }
            else {
                parseTrees.add(cu);
            }
        }
        return parseTrees;
    }

    public void stat() {
        logger.log("analyzing project: " + config.repoName, null);
        logger.log("line size: " + commitLine.getSize(), null);
        logger.log("total analyzed commits: " + analyzed_commits, null);
        logger.log("total valid commits: " + valid_commits, null);
        logger.log("total valid but empty commits: " + valid_but_empty_commits, null);
        logger.log("total checkout times: " + total_checkout_times, null);
        logger.log("total status create times: " + status_creates, null);
        logger.log("total status update times: " + status_updates, null);
        logger.log("total analyzed graphs: " + graph_analyzed, null);
        logger.log("total graph build success: " + gb_succ, null);
        logger.log("total slice success: " + slice_succ, null);
        logger.log("total err: " + total_err, null);
    }

    public void output(int idx) {
        logger.log("output at idx: " + idx, null);
        String base = config.output + "/" + config.repoName;
        File baseFile = new File(base);
        if (!baseFile.exists()) {
            baseFile.mkdir();
        }
        logger.log("total commit diffs to output: " + commitDiffs.size(), null);
        // write
        commitDiffs.forEach(commitDiff -> {
            String dir = base + "/" + commitDiff.getCurrentVersion();
            logger.log("outputting to: " + dir, null);
            File f1 = new File(dir);
            if (!f1.exists()) {
                boolean b = f1.mkdirs();
                assert b;
            }
            commitDiff.fileDiffs.forEach(fileDiff -> {
                // v1
                fileDiff.graphs1.forEach(graph -> {
                    String pathName = dir + "/" + fileDiff.getSimpleName() + "_" + graph.n.getSignature().asString() + "__";
                    File f2 = new File(pathName);
                    if (!f2.exists()) {
                        f2.mkdir();
                    }
                    String fileName = pathName + "/" + "g1.json";
                    JsonExporter.export(JsonExporter.parse(graph), fileName);
//                    DotExporter.export(new GraphObject(graph), pathName + "/" + "g1.dot");
                });
                // v2
                fileDiff.graphs2.forEach(graph -> {
                    String pathName = dir + "/" + fileDiff.getSimpleName() + "_" + graph.n.getSignature().asString()+ "__";
                    File f2 = new File(pathName);
                    if (!f2.exists()) {
                        f2.mkdir();
                    }
                    String fileName = pathName + "/" + "g2.json";
                    JsonExporter.export(JsonExporter.parse(graph), fileName); // fixme
//                    DotExporter.export(new GraphObject(graph), pathName + "/" + "g2.dot");
                });
            });
            try {
                Stream<Path> dirs = Files.list(f1.toPath());
                if (dirs.findFirst().isEmpty()) {
                    valid_but_empty_commits++;
                    f1.delete();
                }
                dirs.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
}
