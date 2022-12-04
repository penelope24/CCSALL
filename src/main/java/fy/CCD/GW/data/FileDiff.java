package fy.CCD.GW.data;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import fy.GD.mgraph.MethodPDG;
import org.eclipse.jgit.diff.DiffEntry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileDiff {

    // change
    public DiffEntry diffEntry;
    public String path1;
    public String path2;
    public MethodPDG graph1;
    public MethodPDG graph2;
    public List<Hunk> hunks = new LinkedList<>();
    public Multimap<MethodDeclaration, Hunk> ccMap1 = ArrayListMultimap.create();
    public Multimap<MethodDeclaration, Hunk> ccMap2 = ArrayListMultimap.create();


    public FileDiff(DiffEntry diffEntry) {
        this.diffEntry = diffEntry;
    }

    public boolean is_valid () {
        if (graph1 == null && graph2 == null) return false;
        return hunks.stream().anyMatch(Hunk::is_valid);
    }

    public String getSimpleName() {
//        if (!is_valid()) return "";
        String[] ss;
        if (path1 != null) {
            ss = path1.split("/");
        }
        else {
            ss = path2.split("/");
        }
        return ss[ss.length-1];
    }
}
