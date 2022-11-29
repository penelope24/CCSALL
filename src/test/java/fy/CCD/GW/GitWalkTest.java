package fy.CCD.GW;

import fy.CCD.GW.data.CommitDiff;
import fy.CCD.GW.data.FileDiff;
import fy.CCD.GW.data.Hunk;
import fy.CCD.GW.utils.JGitUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.Diff;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

class GitWalkTest {
    String path = "/Users/fy/Documents/MyProjects/CodeChangeDataSet/gerrit";
    Repository repository = JGitUtils.buildJGitRepository(path);
    String logPath = "/Users/fy/Documents/fyJavaProjects/CCSALL/src/test/java/fy/CCD/GW/outputLog.txt";
    String outputBase = "/Users/fy/Documents/fyJavaProjects/CCSALL/src/test/java/fy/CCD/GW/base";

    @Test
    void test() throws IOException, GitAPIException {
        FileUtils.deleteDirectory(new File(outputBase));
        new File(outputBase).mkdir();
        GitWalk gitWalk = new GitWalk(path);
        gitWalk.setLogger(logPath);
        gitWalk.setOutputBase(outputBase);
        gitWalk.walk1();
        System.out.println("pre walk over");
        gitWalk.walk(10);
        gitWalk.check();
    }

    @Test
    void reproduce() throws IOException, GitAPIException {
        String v = "e1d181208d6198682e1ac2d4c702515968323c04";
        String fileName = "ChangeNoteUtil.java";
        RevCommit commit = repository.parseCommit(repository.resolve(v));
        GitWalk walk = new GitWalk(path);
        RevCommit par = JGitUtils.findFirstParent(repository, commit);
        List<DiffEntry> diffEntries = JGitUtils.listDiffEntries(repository, par, commit, ".java");
        DiffEntry diffEntry = diffEntries.stream()
                .filter(entry -> entry.getOldPath().contains(fileName))
                .findFirst().get();
        EditList edits = JGitUtils.getEditList(repository, diffEntry);
        edits.forEach(edit -> {
            Hunk hunk = new Hunk(edit);
            System.out.println(edit);
            System.out.println(edit.getType().name());
            System.out.println(hunk.getRemLines());
            System.out.println(hunk.getAddLines());
        });
    }


}