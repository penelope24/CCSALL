package fy;


import fy.CCD.GW.GitWalkContinuous;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

public class Exec {
    Config config = new Config();

    public void parseArgs(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }
        else if (args.length == 1) {
            config.input = args[0];
        }
        else {
            throw new IllegalStateException("unsupported args length");
        }
    }

    public void run() throws IOException, GitAPIException {
        GitWalkContinuous walker = new GitWalkContinuous(config);
        walker.preWalk();
        walker.walk();
    }
}
