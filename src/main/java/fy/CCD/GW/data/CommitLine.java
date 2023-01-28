package fy.CCD.GW.data;

import fy.utils.git.JGitUtils;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class CommitLine {
    public final JGitUtils jgit;
    final RevCommit head;
    final List<RevCommit> commits;
    final boolean significant;

    public CommitLine(JGitUtils jgit, List<RevCommit> commits) {
        this.jgit = jgit;
        this.head = commits.get(0);
        this.commits = commits;
        this.significant = commits.size() > 10;
    }

    public boolean isSignificant() {
        return significant;
    }

    public RevCommit getHead() {
        return head;
    }

    public List<RevCommit> getCommits() {
        return commits;
    }

    public int getSize() {
        return commits.size();
    }

    public int indexOf(RevCommit commit) {
        return commits.indexOf(commit);
    }

    public RevCommit getCommitByIndex(int index) {
        return commits.get(index);
    }

    public List<Delta> getDeltaList() {
        int size = getSize();
        if (size < 2) {
            return new ArrayList<>();
        }
        List<Delta> res = new ArrayList<>();
        for (int i = size - 1; i >= 1; i--) {
            res.add(new Delta(getCommitByIndex(i), getCommitByIndex(i - 1)));
        }
        assert res.size() == (size - 1);
        return res;
    }
}
