package fy.CCS.cases;

public class NodeWithConditionExample {

    public void f (int a) {
        if (a > 1) {
            System.out.println("stmt");
            System.out.println("stmt");
        }
        else {
            System.out.println("stmt");
            System.out.println("stmt");
            System.out.println("stmt");
        }
    }
}
