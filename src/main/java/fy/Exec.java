package fy;


import fy.CCD.GW.GitWalkContinuous;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Exec {
    boolean configByParam;
    boolean configBasePath;
    boolean multiple;
    String base;
    String input;
    final List<String> inputs = new ArrayList<>();

    public void parseArgs(String[] args) {
        if (args == null || args.length == 0) {
            configByParam = false;
            return;
        }
        configByParam = true;
        List<String> argsList = Arrays.asList(args);
        if (argsList.contains("--base")) {
            configBasePath = true;
            int base_idx = argsList.indexOf("--base");
            if (base_idx + 1 > argsList.size() - 1) {
                throw new IllegalStateException("cannot find param for --base");
            }
            base = argsList.get(base_idx + 1);
            if (!argsList.contains("--input")) {
                throw new IllegalStateException("wrong params");
            }
            int input_idx = argsList.indexOf("--input");
            int path_num = base_idx - 1 - input_idx;
            assert path_num >= 1;
            if (path_num == 1) {
                multiple = false;
                input = argsList.get(input_idx + 1);
            } else {
                multiple = true;
                for (int i = input_idx + 1; i <= input_idx + path_num; i++) {
                    inputs.add(argsList.get(i));
                }
            }
        } else {
            configBasePath = false;
            int input_idx = argsList.contains("--input") ?
                    argsList.indexOf("--input")
                    :
                    -1;
            int last_idx = argsList.size() - 1;
            int path_num = last_idx - input_idx;
            assert path_num >= 1;
            if (path_num == 1) {
                multiple = false;
                input = argsList.get(input_idx + 1);
            } else {
                multiple = true;
                for (int i = input_idx + 1; i <= input_idx + path_num; i++) {
                    inputs.add(argsList.get(i));
                }
            }
        }

    }

    public void exec() throws IOException, GitAPIException {
        // in this case run default project
        if (!configByParam) {
            Config config = new Config();
            run(config);
            return;
        }
        if (configBasePath) {
            if (!multiple) {
                Config config = new Config();
                config.input = input;
                config.base = base;
                run(config);
            } else {
                for (String s : inputs) {
                    Config config = new Config();
                    config.input = s;
                    config.base = base;
                    run(config);
                }
            }
        } else {
            if (!multiple) {
                Config config = new Config();
                config.input = input;
                run(config);
            } else {
                for (String s : inputs) {
                    Config config = new Config();
                    config.input = s;
                    run(config);
                }
            }
        }
    }

    public void run(Config config) throws IOException, GitAPIException {
        GitWalkContinuous walker = new GitWalkContinuous(config);
        walker.preWalk();
        walker.walk();
    }

}
