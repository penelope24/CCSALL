package fy.GD.edges;


import java.util.ArrayList;
import java.util.List;

public class DFEdge {
    public final Type type;
    public final String var;
    public final List<Integer> sliceInfo = new ArrayList<>();

    public DFEdge(fy.GD.edges.DFEdge.Type type, String var) {
        this.type = type;
        this.var = var;
    }

    public void addSLice(int idx) {
        sliceInfo.add(idx);
    }

    public List<Integer> getSliceInfo() {
        return sliceInfo;
    }

    /**
     * Enumeration of different types for DD edges.
     */
    public enum Type {
        FLOW("Flows"),
        ANTI("Anti"),
        OUTPUT("Out");

        public final String label;

        Type(String lbl) {
            label = lbl;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
