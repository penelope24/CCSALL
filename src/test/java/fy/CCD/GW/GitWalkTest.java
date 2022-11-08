package fy.CCD.GW;

import fy.CCD.GW.data.CommitDiff;
import fy.CCD.GW.utils.JGitUtils;
import fy.GD.mgraph.MethodPDGExporter;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

class GitWalkTest {
    String path = "/Users/fy/Documents/MyProjects/CodeChangeDataSet/gerrit";
    Repository repository = JGitUtils.buildJGitRepository(path);

    @Test
    void test() throws IOException, GitAPIException {
        GitWalk gitWalk = new GitWalk(path);
        gitWalk.walk1();
        gitWalk.walk(50);
        gitWalk.check();
        gitWalk.log("/Users/fy/Documents/fyJavaProjects/CCData/src/test/java/GW/log.txt");
    }

    @Test
    void run_single () throws IOException {
        String v = "1bef1f7956dbdf072cf115bf43353197311f47c4";
        RevCommit commit = repository.parseCommit(repository.resolve(v));
        GitWalk gitWalk = new GitWalk(path);
        CommitDiff commitDiff = gitWalk.solve(commit);
        System.out.println(commitDiff.ccMap.keySet().size());
        AtomicInteger idx = new AtomicInteger();
        commitDiff.ccMap.keys().forEach(methodPDG -> {
            System.out.println(methodPDG.n.toString());
            System.out.println("---------");
//            String output = "/Users/fy/Documents/fyJavaProjects/CCSALL/src/test/output/tmp/" + "dot_" + idx.getAndIncrement() + ".dot";
//            MethodPDGExporter.export(methodPDG, output);
        });

    }
}