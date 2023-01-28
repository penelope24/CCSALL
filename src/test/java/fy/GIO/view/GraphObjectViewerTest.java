package fy.GIO.view;

import fy.GIO.object.GraphObject;
import org.junit.jupiter.api.Test;

class GraphObjectViewerTest {

    @Test
    void test() {
        String jsonPath = "/Users/fy/Documents/CCSALL/src/test/java/fy/CCD/GW/base/436905c9a9a2895519bcb837a58fdb7750a676c0/SchemaModule.java_configure()__/g1.json";
        GraphObject go = GraphObjectViewer.fromJSON(jsonPath);
        System.out.println(go);
    }
}