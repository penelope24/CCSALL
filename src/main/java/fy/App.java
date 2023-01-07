package fy;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws IOException, GitAPIException {
        Exec exec = new Exec();
        exec.parseArgs(args);
        exec.run();
    }
}
