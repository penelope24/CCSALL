package fy.CCS.slicing;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.List;
import java.util.Optional;

public class SrcCodeTransformer {

    public static int getNodeLineNum(Node node) {
        return node.getRange().isPresent() ?
                node.getRange().get().begin.line
                :
                -1;
    }

    public static MethodDeclaration removeLineFromMethod(MethodDeclaration n, int line) {
        Node remNode = n.findAll(Node.class).stream()
                .filter(node -> getNodeLineNum(node) == line)
                .findFirst().orElse(null);
        if (remNode != null) {
            Optional<Node> par = remNode.getParentNode();
            if (par.isPresent()) {
                par.get().remove(remNode);
            }
        }
        return n;
    }

    public static MethodDeclaration removeLinesFromMethod(MethodDeclaration n, List<Integer> lines2rem) {
        lines2rem.forEach(line -> removeLineFromMethod(n, line));
        return n;
    }


}
