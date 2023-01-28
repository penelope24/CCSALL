package fy.GIO.export;

import fy.GIO.object.GraphObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DotExporter {

    public static void export(GraphObject graphObject, String output) {
        String dotStr = new DotStrGenerator().generate(graphObject);
        try {
            Files.write(Paths.get(output), dotStr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
