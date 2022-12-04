package fy.CCD.GW.data;

import com.github.javaparser.Range;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GD.mgraph.MethodPDG;
import org.eclipse.jgit.diff.Edit;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MethodDiff {

    MethodPDG g1;
    MethodPDG g2;
    MethodDeclaration n1;
    MethodDeclaration n2;
    CommitDiff commitDiff;
    FileDiff fileDiff;
    Set<Hunk> hunks;

    public MethodDiff(Hunk hunk) {
        this.g1 = hunk.graph1;
        this.g2 = hunk.graph2;
        this.n1 = hunk.n1;
        this.n2 = hunk.n2;
        this.commitDiff = hunk.commitDiff;
        this.fileDiff = hunk.fileDiff;
    }

    public MethodPDG getG1() {
        return g1;
    }

    public MethodDiff setG1(MethodPDG g1) {
        this.g1 = g1;
        return this;
    }

    public MethodPDG getG2() {
        return g2;
    }

    public MethodDiff setG2(MethodPDG g2) {
        this.g2 = g2;
        return this;
    }

    public MethodDeclaration getN1() {
        return n1;
    }

    public MethodDiff setN1(MethodDeclaration n1) {
        this.n1 = n1;
        return this;
    }

    public MethodDeclaration getN2() {
        return n2;
    }

    public MethodDiff setN2(MethodDeclaration n2) {
        this.n2 = n2;
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

    public Set<Hunk> getHunks() {
        return hunks;
    }

    public MethodDiff setHunks(Set<Hunk> hunks) {
        this.hunks = hunks;
        return this;
    }

    public String getMethodName() {
        if (n1 != null) {
            return n1.getNameAsString();
        }
        else if (n2 != null) {
            return n2.getNameAsString();
        }
        else {
            return "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodDiff that = (MethodDiff) o;
        return Objects.equals(getN1(), that.getN1()) &&
                Objects.equals(getN2(), that.getN2());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getN1(), getN2());
    }
}
