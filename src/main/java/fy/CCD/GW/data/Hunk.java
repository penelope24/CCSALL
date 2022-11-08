package fy.CCD.GW.data;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.body.MethodDeclaration;
import fy.GD.mgraph.MethodPDG;
import org.eclipse.jgit.diff.Edit;

public class Hunk {
    public Edit edit;
    public Edit.Type type;
    public Range r1;
    public Range r2;
    public MethodDeclaration n1;
    public MethodDeclaration n2;
    public MethodPDG graph1;
    public MethodPDG graph2;

    public Hunk(Edit edit) {
        this.edit = edit;
        this.type = edit.getType();
        this.r1 = new Range(new Position(edit.getBeginA(), 0), new Position(edit.getEndA(), 0));
        this.r2 = new Range(new Position(edit.getBeginB(), 0), new Position(edit.getEndB(), 0));
    }

    public boolean is_valid() {
        if (graph1 == null && graph2 == null) {
            return false;
        }
        return true;
    }


}
