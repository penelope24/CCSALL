package fy.utils.file;


import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class SubFileFinder implements DirWalker {
    private final FileHandler fileHandler;
    private final Filter filter;

    public SubFileFinder(Filter filter, FileHandler fileHandler) {
        this.filter = filter;
        this.fileHandler = fileHandler;
    }

    public static List<String> findAllJavaFiles(String dir) {
        List<String> results = new LinkedList<>();
        new SubFileFinder((level, path, file) -> path.endsWith(".java"),
                ((level, path, file) -> results.add(file.getAbsolutePath()))
        ).explore(new File(dir));
        return results;
    }

    public static List<String> findAllPomFiles(String dir) {
        List<String> results = new LinkedList<>();
        new SubFileFinder((level, path, file) -> path.endsWith("pom.xml"),
                ((level, path, file) -> results.add(file.getAbsolutePath()))
        ).explore(new File(dir));
        return results;
    }

    @Override
    public void explore(File root) {
        explore(0, "", root);
    }

    @Override
    public void explore(int level, String path, File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                explore(level + 1, path + "/" + child.getName(), child);
            }
        } else {
            if (filter.interested(level, path, file)) {
                fileHandler.handle(level, path, file);
            }
        }
    }

    @FunctionalInterface
    public interface FileHandler {
        void handle(int level, String path, File file);
    }

    @FunctionalInterface
    public interface Filter {
        boolean interested(int level, String path, File file);
    }
}
