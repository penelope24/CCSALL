package fy.CCD.GW.data;

import com.github.javaparser.Range;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GD.mgraph.MethodPDG;
import org.eclipse.jgit.diff.Edit;

import java.util.List;

public class MethodDiff {
    MethodDeclaration m_old;
    MethodDeclaration m_new;
    MethodPDG g_old;
    MethodPDG g_new;
    List<Hunk> hunks_old;
    List<Hunk> hunks_new;
    CommitDiff commitDiff;
    FileDiff fileDiff;

    public MethodDiff() {
    }

    public MethodDeclaration getM_old() {
        return m_old;
    }

    public MethodDiff setM_old(MethodDeclaration m_old) {
        this.m_old = m_old;
        return this;
    }

    public MethodDeclaration getM_new() {
        return m_new;
    }

    public MethodDiff setM_new(MethodDeclaration m_new) {
        this.m_new = m_new;
        return this;
    }

    public MethodPDG getG_old() {
        return g_old;
    }

    public MethodDiff setG_old(MethodPDG g_old) {
        this.g_old = g_old;
        return this;
    }

    public MethodPDG getG_new() {
        return g_new;
    }

    public MethodDiff setG_new(MethodPDG g_new) {
        this.g_new = g_new;
        return this;
    }

    public List<Hunk> getHunks_old() {
        return hunks_old;
    }

    public MethodDiff setHunks_old(List<Hunk> hunks_old) {
        this.hunks_old = hunks_old;
        return this;
    }

    public List<Hunk> getHunks_new() {
        return hunks_new;
    }

    public MethodDiff setHunks_new(List<Hunk> hunks_new) {
        this.hunks_new = hunks_new;
        return this;
    }

    public CommitDiff getCommitDiff() {
        return commitDiff;
    }

    public MethodDiff setCommitDiff(CommitDiff commitDiff) {
        this.commitDiff = commitDiff;
        return this;
    }

    public FileDiff getFileDiff() {
        return fileDiff;
    }

    public MethodDiff setFileDiff(FileDiff fileDiff) {
        this.fileDiff = fileDiff;
        return this;
    }
}
