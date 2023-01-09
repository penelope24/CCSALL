package fy.utils.log;

import fy.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Logger {

    public Config config;
    String mode = "all";
    boolean print = true;

    public Logger(Config config) {
        this.config = config;
        File file1 = new File(config.log_base);
        if (!file1.exists()) {
            file1.mkdir();
        }
        if (mode.equals("all")) {
            File file2 = new File(config.log_project);
            if (!file2.exists()) {
                file2.mkdir();
            }
        }
    }

    public void log(String msg, String note) {
        // parse msg
        if (note != null) {
            msg += " | " + note + System.lineSeparator();
        }
        else {
            msg += System.lineSeparator();
        }
        // write
        String path1 = config.log_base_msg;
        String path2 = config.log_project_msg;
        write(msg, path1, path2);
        // print
        if (print) {
            System.out.println(msg);
        }
    }

    public void log(Throwable e, String note) {
        // parse msg
        String msg = "";
        msg += getSimpleName(e);
        if (note != null) {
            msg += " | " + note;
        }
        msg += System.lineSeparator();
        // write
        String path1 = config.log_base_exp;
        String path2 = config.log_project_exp;
        write(msg, path1, path2);
        // print
        if (print) {
            System.out.println(msg);
        }
    }

    private void write(String msg, String path1, String path2) {
        try {
            Files.write(Paths.get(path1), msg.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            if (mode.equals("all")) {
                Files.write(Paths.get(path2), msg.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    public String getSimpleName(Throwable e) {
        return e.getClass().toString();
    }
}
