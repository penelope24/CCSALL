package fy.CCD.GW.data;


import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.*;

public class CommitDiff {
    RevCommit commit;
    public Repository repository;
    public String v1;
    public String v2;
    public List<FileDiff> fileDiffs = new LinkedList<>();


    public CommitDiff(RevCommit commit, Repository repository, String v1, String v2) {
        this.commit = commit;
        this.repository = repository;
        this.v1 = v1;
        this.v2 = v2;
    }

    public boolean is_valid() {
        if (fileDiffs == null || fileDiffs.isEmpty()) return false;
        return fileDiffs.stream().anyMatch(FileDiff::isValid);
    }


    public String getCurrentVersion() {
        return v2;
    }

    public Set<Hunk> getValidHunks() {
        Set<Hunk> validHunks = new LinkedHashSet<>();
        fileDiffs.forEach(fileDiff -> fileDiff.hunks.forEach(hunk -> {
            if (hunk.is_valid()) {
                validHunks.add(hunk);
            }
        }));
        return validHunks;
    }

    public Set<Hunk> getFullHunks() {
        Set<Hunk> fullHunks = new LinkedHashSet<>();
        fileDiffs.forEach(fileDiff -> fileDiff.hunks.forEach(hunk -> {
            if (hunk.is_full()) {
                fullHunks.add(hunk);
            }
        }));
        return fullHunks;
    }

    public Set<Hunk> getAllHunks() {
        Set<Hunk> allHunks = new LinkedHashSet<>();
        fileDiffs.forEach(fileDiff -> allHunks.addAll(fileDiff.hunks));
        return allHunks;
    }

    public String getCommitMessage() {
        return commit.getShortMessage();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommitDiff that = (CommitDiff) o;
        return Objects.equals(repository, that.repository) &&
                Objects.equals(v1, that.v1) &&
                Objects.equals(v2, that.v2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repository, v1, v2);
    }
}
