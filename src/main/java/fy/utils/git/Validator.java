package fy.utils.git;

import fy.Config;
import fy.utils.file.JavaFileUtils;
import org.eclipse.jgit.diff.DiffEntry;

import java.util.List;

public class Validator {

    public static boolean isJavaFileValid(String javaFile, Config config) {
        if (javaFile == null) {
            return false;
        }
        String lowerName = javaFile.toLowerCase();
        if (config.skip_entry_tests) {
            if (lowerName.contains("test") || lowerName.contains("-info")) {
                return false;
            }
        }
        int lineNum = JavaFileUtils.countSourceLineNum(javaFile);
        if (lineNum > config.max_file_size || lineNum == 0) {
            return false;
        }
        return true;
    }

    public static boolean isDiffEntryListValid(List<DiffEntry> diffEntries, Config config) {
        if (diffEntries != null && !diffEntries.isEmpty() && diffEntries.size() <= config.max_entry_num) {
            return true;
        }
        return false;
    }
}
