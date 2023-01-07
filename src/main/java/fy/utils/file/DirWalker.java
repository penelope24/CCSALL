package fy.utils.file;

import java.io.File;

public interface DirWalker {
    void explore(File root);

    void explore(int level, String path, File file);
}
