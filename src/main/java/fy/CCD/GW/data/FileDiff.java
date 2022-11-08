package fy.CCD.GW.data;

import org.eclipse.jgit.diff.DiffEntry;

import java.util.LinkedList;
import java.util.List;

public class FileDiff {

    // change
    public DiffEntry diffEntry;
    public String path1;
    public String path2;
    public List<Hunk> hunks = new LinkedList<>();


    public FileDiff(DiffEntry diffEntry) {
        this.diffEntry = diffEntry;
    }

    public boolean is_valid () {
        if (hunks.stream().anyMatch(Hunk::is_valid)) {
            return true;
        }
        return false;
    }
}
