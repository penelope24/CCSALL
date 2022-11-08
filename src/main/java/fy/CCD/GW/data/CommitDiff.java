package fy.CCD.GW.data;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import fy.GD.mgraph.MethodPDG;
import org.eclipse.jgit.lib.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class CommitDiff {
    public Repository repository;
    public String v1;
    public String v2;
    public List<FileDiff> fileDiffs = new LinkedList<>();
    public Multimap<MethodPDG, Hunk> ccMap = ArrayListMultimap.create();
    public boolean v1_parsed = false;
    public boolean v2_parsed = false;

    public CommitDiff(Repository repository, String v1, String v2) {
        this.repository = repository;
        this.v1 = v1;
        this.v2 = v2;
    }

    public boolean is_valid() {
        if (fileDiffs.stream().anyMatch(FileDiff::is_valid)) {
            return true;
        }
        return false;
    }

    public boolean is_parsed () {
        return v1_parsed || v2_parsed;
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
