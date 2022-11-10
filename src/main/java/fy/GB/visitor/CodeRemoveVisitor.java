package fy.GB.visitor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;

import java.util.List;

/**
 * todo
 */
public class CodeRemoveVisitor extends ModifierVisitor {

    List<Integer> linesToRem;

    public CodeRemoveVisitor(List<Integer> linesToRem) {
        this.linesToRem = linesToRem;
    }

    public int getStmtLineNum(Statement stmt) {
        return stmt.getRange().isPresent() ?
                stmt.getRange().get().begin.line
                :
                -1;
    }

    @Override
    public Node visit(ExpressionStmt stmt, Object args) {
        super.visit(stmt, args);
        int line = stmt.getRange().isPresent() ?
                stmt.getRange().get().begin.line
                :
                -1;
        if (linesToRem.contains(line)) {

        }
        return stmt;
    }

//    @Override
//    public Node visit(IfStmt stmt, Object args){
//        int line = getStmtLineNum(stmt);
//        stmt.setThenStmt(new EmptyStmt());
//        return stmt;
//    }

}
