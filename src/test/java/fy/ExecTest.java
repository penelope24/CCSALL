package fy;

import org.junit.jupiter.api.Test;

class ExecTest {

    @Test
    void test() {
        String params = "path1 path2 path3";
        String[] args = params.split(" ");
        System.out.println(args.length);
        Exec exec = new Exec();
        exec.parseArgs(args);
        System.out.println("");
    }

}