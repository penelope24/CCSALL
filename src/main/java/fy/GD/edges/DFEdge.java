package fy.GD.edges;


public class DFEdge {
    public final Type type;
    public final String var;

    public DFEdge(fy.GD.edges.DFEdge.Type type, String var) {
        this.type = type;
        this.var = var;
    }

    /**
     * Enumeration of different types for DD edges.
     */
    public enum Type {
        FLOW    ("Flows"),
        ANTI    ("Anti"),
        OUTPUT  ("Out");

        public final String label;

        private Type(String lbl) {
            label = lbl;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
