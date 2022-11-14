package fy.CCS.traverse;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayDeque;
import java.util.Deque;

public class MethodTraversal {
    MethodDeclaration n;
    Deque<Node> visiting = new ArrayDeque<>();
    Deque<Node> visited = new ArrayDeque<>();

    public MethodTraversal(MethodDeclaration n) {
        this.n = n;
    }
}
