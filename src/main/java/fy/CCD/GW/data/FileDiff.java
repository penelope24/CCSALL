package fy.CCD.GW.data;

import fy.GD.mgraph.MethodPDG;
import fy.utils.file.PathUtils;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileDiff {

    // change
    public final DiffEntry diffEntry;
    public Repository repository;
    public final String path1;
    public final String path2;
    public final List<Hunk> hunks = new LinkedList<>();
    public final List<MethodPDG> graphs1 = new ArrayList<>();
    public final List<MethodPDG> graphs2 = new ArrayList<>();

    public FileDiff(DiffEntry diffEntry, Repository repository) {
        this.diffEntry = diffEntry;
        this.path1 = PathUtils.getOldPath(diffEntry, repository);
        this.path2 = PathUtils.getNewPath(diffEntry, repository);
    }

    public String getFullName() {
        if (path1 != null) {
            return path1;
        }
        assert path2 != null;
        return path2;
    }

    public String getSimpleName() {
        String[] ss;
        if (path1 != null) {
            ss = path1.split("/");
        } else {
            ss = path2.split("/");
        }
        return ss[ss.length - 1];
    }


    public boolean isValid() {
        return !graphs1.isEmpty() || !graphs2.isEmpty();
    }
}
