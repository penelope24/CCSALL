//package fy.CCD.GW;
//
//import fy.CCD.running.GWRunner;
//import fy.Config;
//import fy.utils.git.JGitUtils;
//import org.eclipse.jgit.api.errors.GitAPIException;
//import org.eclipse.jgit.lib.ObjectId;
//import org.eclipse.jgit.lib.Repository;
//import org.eclipse.jgit.revwalk.RevCommit;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.util.*;
//
//class GitWalkTest {
//    String path = "/Users/fy/Documents/MyProjects/CodeChangeDataSet/gerrit";
//    Repository repository = JGitUtils.buildJGitRepository(path);
//    String outputBase = "/Users/fy/Documents/CCSALL/src/test/java/fy/CCD/GW/base";
//    Properties prop = Config.loadProperties();
//
//    @Test
//    /**
//     * 0 - 5851
//     */
//    void run() {
//        String path = prop.getProperty("path.input");
//        GitWalk gitWalk = new GitWalk(path);
//        gitWalk.walk(0,100);
//    }
//
//    @Test
//    void reproduce() throws IOException, GitAPIException {
//        String v = "f073e35b89c90f3b58978303d0a0b4fb91d85538";
//        GitWalk walker = new GitWalk(path);
//        RevCommit commit = repository.parseCommit(repository.resolve(v));
//        walker.solve(commit);
//    }
//
//    @Test
//    void test_multi_thread() {
//        String path1 = "/Users/fy/Documents/MyProjects/CodeChangeDataSet/eclipse.jdt.core";
//        String path2 = "/Users/fy/Documents/MyProjects/CodeChangeDataSet/eclipse.platform";
//        String path3 = "/Users/fy/Documents/MyProjects/CodeChangeDataSet/gerrit";
//        String path4 = "/Users/fy/Documents/MyProjects/CodeChangeDataSet/mineSStuBs";
//        List<String> paths = Arrays.asList(path1, path2, path3, path4);
//        String base = "/Users/fy/Documents/CCSALL/src/test/java/fy/CCD/GW/multi_base";
//        List<String> bases = Arrays.asList(
//                base + "/" + "base1",
//                base + "/" + "base2",
//                base + "/" + "base3",
//                base + "/" + "base4"
//        );
//        int k = 2;
//        for (int i=0; i<k; i++) {
//            GWRunner runner = new GWRunner(String.valueOf(i), paths.get(i), bases.get(i));
//            runner.start();
//        }
//    }
//
//    @Test
//    void traverse() throws GitAPIException, IOException {
//        List<RevCommit> commits = new ArrayList<>();
//        JGitUtils jgit = new JGitUtils(path);
//        jgit.delete_lock_file();
//        jgit.reset();
//        ObjectId master = JGitUtils.getMaster(repository);
//        if (master == null) {
//            throw new IllegalStateException("cannot find master head to start traverse");
//        }
//        RevCommit head = repository.parseCommit(master);
//        System.out.println(head);
//        Deque<RevCommit> worklist = new ArrayDeque<>();
//        RevCommit parent = JGitUtils.findFirstParent(repository, head);
//        worklist.add(parent);
//        List<RevCommit> traversed = new ArrayList<>();
//        while (!worklist.isEmpty()) {
//            RevCommit commit = worklist.pop();
//            traversed.add(commit);
//            RevCommit par = JGitUtils.findFirstParent(repository, commit);
//            if (par != null) {
//                worklist.add(par);
//            }
//        }
//        traversed.subList(0, 100).forEach(System.out::println);
//    }
//}