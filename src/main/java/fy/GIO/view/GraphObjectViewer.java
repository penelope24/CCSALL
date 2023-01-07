package fy.GIO.view;

import com.google.gson.Gson;
import fy.GIO.object.GraphObject;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class GraphObjectViewer {

    public static GraphObject fromJSON (String jsonPath) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(new FileReader(jsonPath), GraphObject.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }
        return null;
    }

}
