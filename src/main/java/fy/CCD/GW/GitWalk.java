//package fy.CCD.GW;
//
//
//import com.github.javaparser.ast.CompilationUnit;
//import com.github.javaparser.ast.Node;
//import com.github.javaparser.ast.body.MethodDeclaration;
//import com.google.common.collect.ArrayListMultimap;
//import com.google.common.collect.Multimap;
//import fy.CCD.GW.data.CommitDiff;
//import fy.CCD.GW.data.FileDiff;
//import fy.CCD.GW.data.Hunk;
//import fy.CCS.slicing.PDGBuilder;
//import fy.CCS.slicing.SrcCodeTransformer;
//import fy.CCS.track.DTEntry;
//import fy.Config;
//import fy.GB.entry.TypeSolverEntry;
//import fy.GB.visitor.VarVisitor;
//import fy.GD.mgraph.MethodPDG;
//import fy.GIO.export.JsonExporter;
//import fy.utils.file.JavaFileUtils;
//import fy.utils.file.SubFileFinder;
//import fy.utils.git.DiffEntryHelper;
//import fy.utils.git.JGitUtils;
//import fy.utils.heap.HeapChecker;
//import fy.utils.tools.JPHelper;
//import org.eclipse.jgit.api.errors.GitAPIException;
//import org.eclipse.jgit.diff.DiffEntry;
//import org.eclipse.jgit.diff.EditList;
//import org.eclipse.jgit.lib.ObjectId;
//import org.eclipse.jgit.lib.Repository;
//import org.eclipse.jgit.revwalk.RevCommit;
//import org.eclipse.jgit.revwalk.RevWalk;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.stream.Collectors;
//
//public class GitWalk {
//    Config config = new Config();
//    // input
//    public String projectPath;
//    public Repository repository;
//    public JGitUtils jgit;
//    public String repoName;
//    // result
//    public List<RevCommit> allCommits = new ArrayList<>();
//    public List<CommitDiff> commitDiffs = new LinkedList<>();
//    // running stats
//    int PDG_BUILD_ERR = 0;
//    int SLICING_ERR = 0;
//    int GIT_API_ERR = 0;
//
//    public GitWalk(String projectPath) {
//        this.projectPath = projectPath;
//        this.repository = JGitUtils.buildJGitRepository(projectPath);
//        this.jgit = new JGitUtils(projectPath);
//        String[] ss = projectPath.split("/");
//        repoName = ss[ss.length-1];
//        try {
//            walk1();
//            System.out.printf("total commits of %s : %s%n", repoName, allCommits.size());
//        } catch (IOException | GitAPIException e) {
//            e.printStackTrace();
//        }
//        System.out.println("analyzing project: " + repoName);
//    }
//
//    public void walk1() throws IOException, GitAPIException {
//        jgit.delete_lock_file();
////        jgit.reset();
//
//        ObjectId master = JGitUtils.getMaster(repository);
//        if (master == null) {
//            throw new IllegalStateException("cannot find master head to start traverse");
//        }
//        RevCommit head = repository.parseCommit(master);
//        RevWalk revWalk = new RevWalk(repository);
//        revWalk.markStart(head);
//        for (RevCommit commit : revWalk) {
//            allCommits.add(commit);
//        }
//    }
//
//    public void walk() {
//        System.out.println("analyzing whole project");
//        int K = config.output_batch;
//        int count = 0;
//        for (RevCommit commit : allCommits) {
//            System.out.println("at idx : " + count++);
//            System.out.println("commit id : " + commit.getId().name());
//            CommitDiff commitDiff = solve(commit);
//            if (commitDiff != null) {
//                commitDiffs.add(commitDiff);
//            }
//            if ((count % K) == 0) {
//                output();
//            }
//            else if (count == allCommits.size()) {
//                output();
//            }
//        }
//    }
//
//    public void walk(int s, int t) {
//        System.out.printf("analyzing from %s to %s%n", s, t);
//        int K = config.output_batch;
//        int count = s;
//        for (RevCommit commit : allCommits.subList(s, t)) {
//            System.out.println("at idx : " + count++);
//            System.out.println("commit id : " + commit.getId().name());
//            CommitDiff commitDiff = solve(commit);
//            if (commitDiff != null) {
//                commitDiffs.add(commitDiff);
//            }
//            if ((count % K) == 0) {
//                output();
//            }
//        }
//    }
//
//    public CommitDiff solve (RevCommit commit) {
//        RevCommit par = JGitUtils.findFirstParent(repository, commit);
//        if (par == null) return null;
//        List<DiffEntry> diffEntries = DiffEntryHelper.getDiffEntryList(jgit, par, commit);
//        if (!DiffEntryHelper.is_valid(diffEntries, config.max_entry_num, config.skip_entry_tests)) return null;
//        CommitDiff commitDiff = new CommitDiff(commit, repository, par.getId().name(), commit.getId().name());
//        List<FileDiff> fileDiffs =  diffEntries.stream()
//                .filter(entry -> entry.getChangeType() == DiffEntry.ChangeType.ADD
//                        || entry.getChangeType() == DiffEntry.ChangeType.DELETE
//                        || entry.getChangeType() == DiffEntry.ChangeType.MODIFY)
//                .map(entry -> new FileDiff(entry, repository))
//                .collect(Collectors.toList());
//        fileDiffs.removeIf(fileDiff -> fileDiff.getFullName().toLowerCase().contains("test"));
//        commitDiff.fileDiffs.addAll(fileDiffs);
//        commitDiff.fileDiffs.forEach(fileDiff -> {
//            try {
//                EditList edits = JGitUtils.getEditList(repository, fileDiff.diffEntry);
//                if (edits.size() > config.max_hunk_num) return;
//                edits.stream()
//                        .map(Hunk::new)
//                        .filter(hunk -> hunk.getMaxEditSize() <= config.max_hunk_size)
//                        .forEach(hunk -> fileDiff.hunks.add(hunk));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        // v1
//        {
//            try {
//                jgit.checkout(par.getId().name());
//                // parse project types
//                List<String> allJavaFiles = SubFileFinder.findAllJavaFiles(projectPath);
//                System.out.println("all project files num: " + allJavaFiles.size());
//                List<CompilationUnit> allParseTrees = SubFileFinder.findAllJavaFiles(projectPath).stream()
//                        .filter(javaFile -> !javaFile.toLowerCase().contains("test"))
//                        .map(JPHelper::getCompilationUnit)
//                        .filter(Objects::nonNull)
//                        .collect(Collectors.toList());
//                HashMap<String, Set<String>> pkg2types = TypeSolverEntry.solve_pkg2types(allParseTrees);
//                commitDiff.fileDiffs.forEach(fileDiff -> {
//                    String path = fileDiff.path1;
//                    if (path == null) return;
//                    int codeLines = JavaFileUtils.countSourceLineNum(path);
//                    if (codeLines > config.max_file_size) return;
//                    VarVisitor varVisitor = TypeSolverEntry.solveVarTypesInFile(path, pkg2types);
//                    CompilationUnit cu = JPHelper.getCompilationUnit(path);
//                    List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
//                    Multimap<MethodDeclaration, Hunk> validMap = ArrayListMultimap.create();
//                    fileDiff.hunks.forEach(hunk -> methods.stream()
//                            .filter(n -> n.getRange().isPresent())
//                            .filter(n -> n.getRange().get().contains(hunk.r1))
//                            .findFirst().ifPresent(enclosingMethod -> validMap.put(enclosingMethod, hunk)));
//                    validMap.keySet().forEach(n -> {
//                        Set<Hunk> insides = new HashSet<>(validMap.get(n));
//                        try {
//                            PDGBuilder builder = new PDGBuilder(pkg2types, varVisitor);
//                            MethodPDG graph = builder.build(n);
//                            MethodDeclaration cloned = n.clone();
//                            Optional<Node> parOpt = n.getParentNode();
//                            int k_data = (graph.vertexCount() / config.a) + 1;
//                            AtomicInteger idx = new AtomicInteger();
//                            insides.forEach(hunk -> {
//                                List<Integer> chLines = hunk.getRemLines();
//                                if (!chLines.isEmpty()) {
//                                    Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, chLines, k_data, config.k_ctrl);
//                                    MethodDeclaration slice = SrcCodeTransformer.slice(cloned, reservedLines);
//                                    parOpt.ifPresent(slice::setParentNode);
//                                    MethodPDG slicedSubGraph = builder.build(slice); // incomplete
//                                    PDGBuilder.sliceAsProperty(graph, slicedSubGraph, idx.getAndIncrement());
//                                }
//                            });
//                            graph.slice_num1 = idx.get();
//                            graph.commitId = commitDiff.getCurrentVersion();
//                            graph.simpleName = fileDiff.getSimpleName();
//                            fileDiff.graphs1.add(graph);
//                        }
//                        catch (Exception e) {
//                            e.printStackTrace();
//                            PDG_BUILD_ERR++;
//                            HeapChecker.print();
//                        }
//                    });
//                });
//            } catch (GitAPIException | StackOverflowError e ) {
//                e.printStackTrace();
//                GIT_API_ERR++;
//                HeapChecker.print();
//            }
//        }
//        // v2
//        {
//            try {
//                jgit.checkout(commit.getId().name());
//                // parse project types
//                List<CompilationUnit> allParseTrees = SubFileFinder.findAllJavaFiles(projectPath).stream()
//                        .filter(javaFile -> !javaFile.toLowerCase().contains("test"))
//                        .map(JPHelper::getCompilationUnit)
//                        .filter(Objects::nonNull)
//                        .collect(Collectors.toList());
//                HashMap<String, Set<String>> pkg2types = TypeSolverEntry.solve_pkg2types(allParseTrees);
//                commitDiff.fileDiffs.forEach(fileDiff -> {
//                    String path = fileDiff.path2;
//                    if (path == null) return;
//                    int codeLines = JavaFileUtils.countSourceLineNum(path);
//                    if (codeLines > config.max_file_size) return;
//                    VarVisitor varVisitor = TypeSolverEntry.solveVarTypesInFile(path, pkg2types);
//                    CompilationUnit cu = JPHelper.getCompilationUnit(path);
//                    List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
//                    Multimap<MethodDeclaration, Hunk> validMap = ArrayListMultimap.create();
//                    fileDiff.hunks.forEach(hunk -> methods.stream()
//                            .filter(n -> n.getRange().isPresent())
//                            .filter(n -> n.getRange().get().contains(hunk.r2))
//                            .findFirst().ifPresent(enclosingMethod -> validMap.put(enclosingMethod, hunk)));
//                    validMap.keySet().forEach(n -> {
//                        Set<Hunk> insides = new HashSet<>(validMap.get(n));
//                        try {
//                            PDGBuilder builder = new PDGBuilder(pkg2types, varVisitor);
//                            MethodPDG graph = builder.build(n);
//                            MethodDeclaration cloned = n.clone();
//                            Optional<Node> parOpt = n.getParentNode();
//                            int k_data = (graph.vertexCount() / config.a) + 1;
//                            AtomicInteger idx = new AtomicInteger();
//                            insides.forEach(hunk -> {
//                                List<Integer> chLines = hunk.getAddLines();
//                                if (!chLines.isEmpty()) {
//                                    Set<Integer> reservedLines = DTEntry.dependencyTrack(graph, chLines, k_data, config.k_ctrl);
//                                    MethodDeclaration slice = SrcCodeTransformer.slice(cloned, reservedLines);
//                                    parOpt.ifPresent(slice::setParentNode);
//                                    MethodPDG slicedSubGraph = builder.build(slice); // incomplete
//                                    PDGBuilder.sliceAsProperty(graph, slicedSubGraph, idx.getAndIncrement());
//                                }
//                            });
//                            graph.slice_num2 = idx.get();
//                            graph.commitId = commitDiff.getCurrentVersion();
//                            graph.simpleName = fileDiff.getSimpleName();
//                            fileDiff.graphs2.add(graph);
//                        }
//                        catch (Exception e) {
//                            e.printStackTrace();
//                            PDG_BUILD_ERR++;
//                            HeapChecker.print();
//                        }
//                    });
//                });
//            } catch (GitAPIException | StackOverflowError e) {
//                e.printStackTrace();
//                GIT_API_ERR++;
//                HeapChecker.print();
//            }
//        }
//        return commitDiff;
//    }
//
//    public void check() {
//    }
//
//    public void output() {
//        // write
//        commitDiffs.forEach(commitDiff -> {
//            String dir = config.output + "/" + repoName + "/" + commitDiff.getCurrentVersion();
//            File f1 = new File(dir);
//            if (!f1.exists()) {
//                boolean b = f1.mkdirs();
//                assert b;
//            }
//            commitDiff.fileDiffs.forEach(fileDiff -> {
//                // v1
//                fileDiff.graphs1.forEach(graph -> {
//                    String pathName = dir + "/" + fileDiff.getSimpleName() + "_" + graph.n.getSignature().asString() + "__";
//                    File f2 = new File(pathName);
//                    if (!f2.exists()) {
//                        f2.mkdir();
//                    }
//                    String fileName = pathName + "/" + "g1.json";
//                    JsonExporter.export(JsonExporter.parse(graph), fileName);
////                    DotExporter.export(new GraphObject(graph), pathName + "/" + "g1.dot");
//                });
//                // v2
//                fileDiff.graphs2.forEach(graph -> {
//                    String pathName = dir + "/" + fileDiff.getSimpleName() + "_" + graph.n.getSignature().asString()+ "__";
//                    File f2 = new File(pathName);
//                    if (!f2.exists()) {
//                        f2.mkdir();
//                    }
//                    String fileName = pathName + "/" + "g2.json";
//                    JsonExporter.export(JsonExporter.parse(graph), fileName);
////                    DotExporter.export(new GraphObject(graph), pathName + "/" + "g2.dot");
//                });
//            });
//            try {
//                // if that dir is empty (efficient implement)
//                if (!Files.list(f1.toPath()).findFirst().isPresent()) {
//                    f1.delete();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            commitDiffs.clear();
//            System.gc();
//        });
//    }
//
//
//}
