package fy.CCD.GW;

import fy.CCD.GW.data.CommitLine;
import fy.Config;
import fy.utils.file.FilesHelper;
import fy.utils.git.JGitUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

class GitWalkContinuousTest {
    Config config = new Config();
    String path = config.input;
    Repository repository = JGitUtils.buildJGitRepository(path);

    // 8m -> 1m52s -> 17s amazing grace !
    @Test
    void test () throws IOException, GitAPIException {
        GitWalkContinuous walker = new GitWalkContinuous(config);
        walker.preWalk();
        walker.walk();
    }

    @Test
    void delete() throws IOException {
        System.out.println(config.output);
        FilesHelper.delete(config.output);
    }

    @Test
    void print() throws IOException, GitAPIException {
        System.out.println(config.repoName);
    }

    @Test
    void write() throws IOException {
    }

    @Test
    void rep() throws IOException, GitAPIException {
        GitWalkContinuous walker = new GitWalkContinuous(config);
        walker.preWalk();
        List<CommitLine> validLines = walker.getValidCommitLines();
        CommitLine line = validLines.get(0);
        LineSolver solver = new LineSolver(line, walker);
        solver.singleWalk(823);
        solver.output(823);
    }

}