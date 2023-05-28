package fy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

public class Config {
    // paths
    public String base; // base dataset path
    public String input; // current project path to parse
    public String output; // where to store the output data
    public String log; // global log path
    public String log_exp; //   global exceptions.txt
    public String log_msg; //  global msg.txt
    public String log_project; //  current project log path
    public String log_project_exp; // current exceptions.txt
    public String log_project_msg; // current msg.txt
    public int projects_num;
    public List<String> project_names = new ArrayList<>();
    // git walk
    public String repoName; // project name
    public int max_entry_num; // max num of entries (files) tp consider
    public int max_file_size; // max size of a single file
    public int max_hunk_num; // max num of hunks in a file
    public int max_hunk_size; // max size of a single hunk
    public boolean skip_entry_tests; // whether to consider entries in test folders
    public int output_batch; // after parsing how many commits, we write output to disk
    // slice
    public int k_ctrl; // slice param
    public int a; // slice param
    // file separator
    public String sep = parseSeparator();

    /**
     * since the file separator will be used in java regex,
     * when used in windows, the separator should be "\\" rather
     * than default File.separator, which is "\" in windows.
     * @return a correct separator for regex.
     */
    private String parseSeparator() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows")) {
            sep = "\\";
            return sep;
        }
        else {
            return File.separator;
        }
    }

    private List<String> parseProjects(Properties prop, int num) {
        List<String> projects = new ArrayList<>();
        for (int i=1; i<=9; i++) {
            String key = "path.name" + i;
            String project = prop.getProperty(key);
            projects.add(project);
        }
        return projects;
    }

    public Config() {
        Properties prop = loadProperties();
        assert prop != null;
        // paths
        base = prop.getProperty("path.base");
        input = prop.getProperty("path.target"); // input path is default set to target path.
        output = base + sep + "output";
        String[] ss = input.split(sep);
        repoName = ss[ss.length - 1];
        log = output + sep + "log";
        log_exp = log + sep + "exceptions.log";
        log_msg = log + sep + "msg.log";
        log_project = log + sep + repoName;
        log_project_exp = log_project + sep + "exceptions.log";
        log_project_msg = log_project + sep + "msg.log";
        projects_num = Integer.parseInt(prop.getProperty("path.num.projects"));
        project_names = parseProjects(prop, projects_num);
        // gw
        max_entry_num = Integer.parseInt(prop.getProperty("gw.limit.num.entry"));
        max_file_size = Integer.parseInt(prop.getProperty("gw.limit.size.file"));
        max_hunk_num = Integer.parseInt(prop.getProperty("gw.limit.num.hunk"));
        max_hunk_size = Integer.parseInt(prop.getProperty("gw.limit.size.hunk"));
        skip_entry_tests = Boolean.parseBoolean(prop.getProperty("gw.skip.entry.test"));
        output_batch = Integer.parseInt(prop.getProperty("gw.output.size.batch"));
        // slice
        a = Integer.parseInt(prop.getProperty("ccs.limit.track.data.a"));
        k_ctrl = Integer.parseInt(prop.getProperty("ccs.limit.track.ctrl"));
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

    public void printPublicFields() {
        Class<?> clazz = this.getClass();
        Field[] fields = clazz.getFields();

        for (Field field : fields) {
            if (java.lang.reflect.Modifier.isPublic(field.getModifiers())) {
                try {
                    String fieldName = field.getName();
                    Object fieldValue = field.get(this);
                    String fieldString = "\"" + fieldName + "\": \"" + fieldValue + "\"";
                    System.out.println(fieldString);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
