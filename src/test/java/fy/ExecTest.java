package fy;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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