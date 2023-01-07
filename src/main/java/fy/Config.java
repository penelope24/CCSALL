package fy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    // todo: gb config
    // paths
    public String input;
    public String output;
    // git walk
    public String repoName;
    public int max_entry_num;
    public int max_file_size;
    public int max_hunk_num;
    public int max_hunk_size;
    public boolean skip_entry_tests;
    public int output_batch;
    // slice
    public int k_ctrl;
    public int a;
    // running
    public String mode;
    public int line;
    public int idx;
    // log
    public String log_base;
    public String log_base_exp;
    public String log_base_msg;
    public String log_project;
    public String log_project_exp;
    public String log_project_msg;

    public Config() {
        Properties prop = loadProperties();
        assert prop != null;
        // paths
        input = prop.getProperty("path.input");
        output = prop.getProperty("path.output");
        // gw
        String[] ss = input.split(File.separator);
        repoName = ss[ss.length-1];
        max_entry_num = Integer.parseInt(prop.getProperty("gw.limit.num.entry"));
        max_file_size = Integer.parseInt(prop.getProperty("gw.limit.size.file"));
        max_hunk_num = Integer.parseInt(prop.getProperty("gw.limit.num.hunk"));
        max_hunk_size = Integer.parseInt(prop.getProperty("gw.limit.size.hunk"));
        skip_entry_tests = Boolean.parseBoolean(prop.getProperty("gw.skip.entry.test"));
        output_batch = Integer.parseInt(prop.getProperty("gw.output.size.batch"));
        // slice
        a = Integer.parseInt(prop.getProperty("ccs.limit.track.data.a"));
        k_ctrl = Integer.parseInt(prop.getProperty("ccs.limit.track.ctrl"));
        // running
        mode = prop.getProperty("running.mode");
        line = Integer.parseInt(prop.getProperty("running.line"));
        idx = Integer.parseInt(prop.getProperty("running.idx"));
        // log
        log_base = prop.getProperty("log.path.base");
        log_base_exp = log_base + File.separator + "exceptions.log";
        log_base_msg = log_base + File.separator + "msg.log";
        log_project = log_base + File.separator + repoName;
        log_project_exp = log_project + File.separator + "exceptions.log";
        log_project_msg = log_project + File.separator + "msg.log";
    }

    public static Properties loadProperties() {
        try (InputStream is = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(is);
            return prop;
        } catch (IOException e) {
            try {
                InputStream is = new FileInputStream("config.properties");
                Properties prop = new Properties();
                prop.load(is);
                return prop;
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

}
