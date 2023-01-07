package fy.GIO.export;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fy.GD.mgraph.MethodPDG;
import fy.GIO.object.GraphObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonExporter {

    public static GraphObject parse(MethodPDG graph) {
        return new GraphObject(graph);
    }

    public static void export(GraphObject graphObject, String outputFile) {
        Gson gson = new GsonBuilder().disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        String jsonString = gson.toJson(graphObject);
        try {
            Files.write(Paths.get(outputFile), jsonString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String format(String input) {
        input = input.replaceAll("\'", "");
        input = input.replaceAll(":", "");
        return input;
    }

}
