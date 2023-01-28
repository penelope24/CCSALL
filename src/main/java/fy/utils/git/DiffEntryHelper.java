package fy.utils.git;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;

public class DiffEntryHelper {

    public static List<DiffEntry> getDiffEntryList(JGitUtils jgit, RevCommit oldCommit, RevCommit newCommit) {
        List<DiffEntry> diffEntries = null;
        try {
            diffEntries = JGitUtils.listDiffEntries(jgit.repository, oldCommit, newCommit, ".java");
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
        return diffEntries;
    }

    public static String getFileName(DiffEntry entry) {
        if (!entry.getOldPath().equals("/dev/null")) {
            return entry.getOldPath();
        } else if (!entry.getNewPath().equals("/dev/null")) {
            return entry.getNewPath();
        }
        return "none";
    }

    public static boolean is_test(DiffEntry entry) {
        String name = getFileName(entry);
        return name.toLowerCase().contains("test");
    }

    public static boolean isNotEmpty(DiffEntry entry) {
        return !getFileName(entry).equals("none");
    }

    public static boolean is_valid(DiffEntry entry, boolean skip_test) {
        if (skip_test) {
            return (!is_test(entry)) && (isNotEmpty(entry));
        } else {
            return isNotEmpty(entry);
        }
    }
}
