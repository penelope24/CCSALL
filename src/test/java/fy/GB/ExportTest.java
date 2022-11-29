package fy.GB;

import ghaffarian.progex.graphs.pdg.PDGBuilder;
import ghaffarian.progex.graphs.pdg.ProgramDependeceGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ExportTest {

    String javaFile = "/Users/fy/Documents/fyJavaProjects/CCSALL/src/main/java/fy/CCD/GW/HKOutputEntry.java";
    ProgramDependeceGraph graph;

    @BeforeEach
    void init() throws IOException {
        String[] paths = new String[1];
        paths[0] = javaFile;
        graph = PDGBuilder.buildForAll( "Java", paths)[0];
    }

    @Test
    void test() throws IOException {

    }

    @Test
    void progex() throws IOException {
        graph.DDS.exportGML("/Users/fy/Documents/fyJavaProjects/CCSALL/src/test/java/fy/GB/output");
    }
}
