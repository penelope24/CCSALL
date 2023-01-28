package fy.CCD.GW.data;

import java.util.Map;
import java.util.Set;

public class MileStone {

    final String id;
    final Map<String, Set<String>> pkg2types;

    public MileStone(String id, Map<String, Set<String>> pkg2types) {
        this.id = id;
        this.pkg2types = pkg2types;
    }

    public String getId() {
        return id;
    }

    public Map<String, Set<String>> getPkg2types() {
        return pkg2types;
    }
}
