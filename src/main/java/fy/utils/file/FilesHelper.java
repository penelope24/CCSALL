package fy.utils.file;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FilesHelper {

    public static void delete(String path) throws IOException {
        FileUtils.deleteDirectory(new File(path));
    }
}
