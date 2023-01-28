package fy.utils.file;


import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SubDirFinder implements DirWalker {

    private final FileHandler fileHandler;
    private final Filter filter;

    public SubDirFinder(Filter filter, FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.filter = filter;
    }

    public static File findFirstBasePom(String dir) {
        List<File> res = new LinkedList<>();
        Judge judger = new Judge();
        new SubDirFinder(judger, ((level, path, file) -> res.add(file))).explore(new File(dir));
        return res.stream().findFirst().orElse(null);
    }

    public void explore(File root) {
        explore(0, "", root);
    }

    public void explore(int level, String path, File file) {
        if (filter.interested(level, path, file)) {
            fileHandler.handle(level, path, file);
        }
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                explore(level + 1, path + "/" + child.getName(), child);
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

    private static class Judge implements Filter {
        @Override
        public boolean interested(int level, String path, File project) {
            Stream<File> dirs = Arrays.stream(Objects.requireNonNull(
                    project.listFiles(pathname -> pathname.isDirectory() && !pathname.isHidden())));
            Stream<File> files = Arrays.stream(Objects.requireNonNull(
                    project.listFiles(pathname -> !pathname.isDirectory() && !pathname.isHidden())));
            boolean has_src = dirs.anyMatch(file -> file.getName().equals("src"));
            boolean has_pom = files.anyMatch(file -> file.getName().equals("pom.xml"));
            // FIXME: 2022/5/13
            return has_src && has_pom;
        }
    }
}
