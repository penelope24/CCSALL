package fy.GB.slicing;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SrcCodeTransformer {

    public static MethodDeclaration removeLineFromMethod(MethodDeclaration n, int line) {
        n.findAll(Node.class).stream()
                .filter(node -> node.getRange().isPresent())
                .filter(node -> node.getRange().get().begin.line == line)
                .forEach(node -> node.getParentNode().ifPresent(par -> par.remove(node)));
        return n;
    }

    public static MethodDeclaration reserveLinesFromMethod(MethodDeclaration n, List<Integer> reserveLines) {
        List<Statement> statements = new ArrayList<>();
        n.getBody().ifPresent(blockStmt -> {
            blockStmt.findAll(Statement.class).forEach(statement -> {
                if (statement.getRange().isPresent() && !reserveLines.contains(statement.getRange().get().begin.line)) {
                    statements.add(statement);
                }
            });
            statements.forEach(blockStmt::remove);
        });
        return n;
    }
}
