package fy.GD.edges;

public class CDEdge {
    public final ghaffarian.progex.graphs.pdg.CDEdge.Type type;

    public CDEdge(ghaffarian.progex.graphs.pdg.CDEdge.Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type.toString();
    }

    /**
     * Enumeration of different types for CD edges.
     */
    public enum Type {

        EPSILON(""),
        TRUE("True"),
        FALSE("False"),
        THROWS("Throws"),
        NOT_THROWS("Not-Throws");

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
