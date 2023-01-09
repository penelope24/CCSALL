package fy;


import fy.CCD.GW.GitWalkContinuous;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Exec {

    public void exec(String[] args) throws IOException, GitAPIException {
        // in this case run default project
        if (args == null || args.length == 0) {
            Config config = new Config();
            run(config);
        }
        // in this case run single configured project
        else if (args.length == 1) {
            String path = args[0];
            Config config = new Config(path);
            run(config);
        }
        // in this case run multiple projects
        else {
            for (String s : args) {
                Config config = new Config(s);
                run(config);
            }
        }
    }

    public void run(Config config) throws IOException, GitAPIException {
        GitWalkContinuous walker = new GitWalkContinuous(config);
        walker.preWalk();
        walker.walk();
    }
}
