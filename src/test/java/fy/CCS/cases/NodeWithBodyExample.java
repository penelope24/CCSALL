package fy.CCS.cases;

public class NodeWithBodyExample {

    public void f (int a) {
        do {
            System.out.println("stmt");
            a--;
        }
        while (a > 0);
    }
}
