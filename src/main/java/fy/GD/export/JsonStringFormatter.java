package fy.GD.export;

public class JsonStringFormatter {

    public static String format(String input) {
        input = input.replaceAll("\'", "");
        input = input.replaceAll(":", "");
        return input;
    }

    public static void main(String[] args) {
        String s = "method: param('s', 's2')";
        System.out.println(format(s));
    }
}
