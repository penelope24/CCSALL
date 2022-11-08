package fy.GD.edges;

public class CFEdge {
    public final Type type;

    public CFEdge(Type type) {
        this.type = type;
    }

    /**
     * Enumeration of different types for CF edges.
     */
    public enum Type {
        EPSILON (""),
        TRUE    ("True"),
        FALSE   ("False"),
        THROWS  ("Throws"),
        CALLS   ("Call"),
        RETURN  ("Return");

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
