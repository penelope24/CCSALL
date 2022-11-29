package fy.CCD.GW.data;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GD.mgraph.MethodPDG;
import org.eclipse.jgit.diff.Edit;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Hunk {
    public Edit edit;
    public Edit.Type type;
    public Range r1;
    public Range r2;
    public MethodDeclaration n1;
    public MethodDeclaration n2;
    public MethodPDG graph1;
    public MethodPDG graph2;
    public MethodPDG slice1;
    public MethodPDG slice2;
    public FileDiff fileDiff;
    public CommitDiff commitDiff;

    public Hunk(Edit edit) {
        this.edit = edit;
        this.type = edit.getType();
        this.r1 = new Range(new Position(edit.getBeginA(), 0), new Position(edit.getEndA(), 0));
        this.r2 = new Range(new Position(edit.getBeginB(), 0), new Position(edit.getEndB(), 0));
    }

    public boolean is_valid() {
        if (slice1 == null && slice2 == null) {
            return false;
        }
        return true;
    }

    public boolean is_full() {
        if (slice1 != null && slice2 != null) {
            return true;
        }
        return false;
    }

    public List<Integer> getRemLines() {
        return IntStream.range(r1.begin.line+1, r1.end.line+1)
                .boxed()
                .collect(Collectors.toList());
    }

    public List<Integer> getAddLines() {
        return IntStream.range(r2.begin.line+1, r2.end.line+1)
                .boxed()
                .collect(Collectors.toList());
    }

    public int getEditStartLine() {
        if (r1 != null) {
            return r1.begin.line;
        }
        else if (r2 !=null) {
            return r2.begin.line;
        }
        else {
            return -1;
        }
    }

    public String getEditType() {
        return edit.getType().name();
    }

    public String getCommitId() {return commitDiff.getCurrentVersion();}

    public String getSimpleFileName() {return fileDiff.getSimpleName();}

    public String getMethodName() {
        if (n1 != null) {
            return n1.getNameAsString();
        }
        else if (n2 != null) {
            return n2.getNameAsString();
        }
        else {
            return "none";
        }
    }

    @Override
    public String toString() {
        String s;
        StringBuilder sb = new StringBuilder();
        if (n1 == null || graph1 == null || slice1 == null) s = "none";
        else {
            sb.append("change region: ").append("\n");
            sb.append(r1).append("\n");

            sb.append("").append("original method: ").append("\n");
            sb.append("").append(n1).append("\n");
            sb.append(n1.getRange().get()).append("\n");
            sb.append("").append("original graph vertexes: ").append(graph1.vertexCount()).append("\n");

            sb.append("").append("sliced method: ").append("\n");
            sb.append(slice1.n).append("\n");
            sb.append(slice1.n.getRange().get());
            sb.append("").append("slice vertexes: ").append(slice1.vertexCount()).append("\n");
            sb.append("\n\n");
        }
        s = sb.toString();
        return s;
    }
}
