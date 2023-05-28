package fy.GIO.view;

import fy.GIO.object.GraphObject;
import org.junit.jupiter.api.Test;

class GraphObjectViewerTest {

    @Test
    void test() {
        String jsonPath = "/Users/fy/Documents/workspace/ccs2vec/samples/g2.json";
        GraphObject go = GraphObjectViewer.fromJSON(jsonPath);
        System.out.println(go);
    }
}