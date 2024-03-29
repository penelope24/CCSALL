package fy.GD.basic;

public enum EdgeTypes {
    CFG("red"), AST("black"), DFG("green"), NCS("blue"), CALL("yellow");

    private String color;

    EdgeTypes(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


}
