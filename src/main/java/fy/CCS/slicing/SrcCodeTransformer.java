package fy.CCS.slicing;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class SrcCodeTransformer {

    public static MethodDeclaration slice (MethodDeclaration n, Set<Integer> reserved) {
        List<BlockStmt> traversedBlocks = new ArrayList<>();
        Map<Statement, Integer> removedStmtIndexMap = new HashMap<>();
        Node.BreadthFirstIterator iterator = new Node.BreadthFirstIterator(n);
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node instanceof BlockStmt) {
                BlockStmt block = (BlockStmt) node;
                BlockStmt clonedBlock = ((BlockStmt) node).clone();
                int idx = 0;
                for (Statement stmt : block.getStatements()) {
                    Statement clonedStmt = clonedBlock.getStatements().stream()
                            .filter(s -> s.equals(stmt))
                            .findFirst().get();
                    if (!is_valid(clonedStmt, reserved)) {
                        clonedBlock.remove(clonedStmt);
                        removedStmtIndexMap.put(stmt, idx);
                    }
                    idx++;
                }
                traversedBlocks.add(clonedBlock);
            }
        }

        Map<BlockStmt, BlockStmt> originalMap = new HashMap<>();
        Map<BlockStmt, Integer> parBlockMap = new HashMap<>();
        Map<BlockStmt, Node> containingStmtMap = new HashMap<>();
        Map<Pair<BlockStmt, Integer>, Integer> indexMap = new HashMap<>();
        traversedBlocks.forEach(modified -> {
            BlockStmt original = findOriginalBlock(n, modified);
            if (original != null) {
                originalMap.put(modified, original);
                BlockStmt parBlock = findValidParentBlock(original, reserved);
                if (parBlock != null) {
                    parBlockMap.put(modified, getNodeLineNum(parBlock));

                    Node containingNode = findContainingStmt(original);
                    if (containingNode != null) containingStmtMap.put(modified, containingNode);

                    int idx = calculateModifiedIndex(original, parBlock, removedStmtIndexMap);
                    if (idx != -1) {
                        indexMap.put(Pair.of(modified, getNodeLineNum(parBlock)), idx);
                    }
                }
            }
        });

        for (BlockStmt b : traversedBlocks) {
            if (!is_valid(b, reserved)) continue;
            if (parBlockMap.containsKey(b)) {
                int parBlockLine = parBlockMap.get(b);
                BlockStmt parBlock = n.findAll(BlockStmt.class).stream()
                        .filter(blockStmt -> getNodeLineNum(blockStmt) == parBlockLine)
                        .findFirst().orElse(null);
                Node stmt = containingStmtMap.get(b);
                if (parBlock != null && !parBlock.getStatements().contains(stmt)) {
                    if (stmt instanceof Statement) {
                        Pair<BlockStmt, Integer> pair = Pair.of(b, getNodeLineNum(parBlock));
                        if (indexMap.containsKey(pair)) {
                            int idx = indexMap.get(pair);
                            parBlock.addStatement(idx, (Statement) stmt);
                        }
                        else {
                            parBlock.addStatement((Statement) stmt);
                        }
                    }
                }
            }
        }

        Node.BreadthFirstIterator iterator2 = new Node.BreadthFirstIterator(n);
        while (iterator2.hasNext()) {
            Node node = iterator2.next();
            if (node instanceof BlockStmt) {
                BlockStmt block = (BlockStmt) node;
                BlockStmt blockClone = block.clone();
                blockClone.getStatements().forEach(stmtClone -> {
                    Statement stmt = block.getStatements().stream()
                            .filter(s -> s.equals(stmtClone))
                            .findFirst().orElse(null);
                    assert stmt != null;
                    if (!is_valid(stmt, reserved)) {
                        block.remove(stmt);
                    }
                });
            }
        }
        return n;
    }


    public static BlockStmt findOriginalBlock(MethodDeclaration n, BlockStmt modifiedBlock) {
        return n.findAll(BlockStmt.class).stream()
                .filter(blockStmt -> getNodeLineNum(blockStmt) == getNodeLineNum(modifiedBlock))
                .findFirst().orElse(null);
    }

    public static BlockStmt findValidParentBlock(BlockStmt block, Set<Integer> reserved) {
        Deque<Node> visiting = new ArrayDeque<>();
        Node parentNode = block.getParentNode().get();
        visiting.add(parentNode);
        while (!visiting.isEmpty()) {
            Node node = visiting.pop();
            if (node instanceof BlockStmt) {
                BlockStmt blockStmt = (BlockStmt) node;
                if (is_valid(blockStmt, reserved)) {
                    return blockStmt;
                }
            }
            node.getParentNode().ifPresent(visiting::add);
        }
        return null;
    }

    public static int calculateModifiedIndex(BlockStmt block, BlockStmt validParBlock, Map<Statement, Integer> removedStmtIndexMap) {
        int idx = -1;
        Deque<Node> visiting = new ArrayDeque<>();
        Node parentNode = block.getParentNode().get();
        visiting.add(parentNode);
        while (!visiting.isEmpty()) {
            Node node = visiting.pop();
            if (node.equals(validParBlock)) {
                return idx;
            }
            if (node instanceof Statement) {
                Statement nodeStmt = (Statement) node;
                if (removedStmtIndexMap.containsKey(nodeStmt)) {
                    idx = removedStmtIndexMap.get(nodeStmt);
                }
            }
            node.getParentNode().ifPresent(visiting::add);
        }
        return idx;
    }

    public static Node findContainingStmt (BlockStmt block) {
        Deque<Node> visiting = new ArrayDeque<>();
        Node parentNode = block.getParentNode().get();
        visiting.add(parentNode);
        while (!visiting.isEmpty()) {
            Node node = visiting.pop();
            if (node instanceof NodeWithBody || node instanceof IfStmt) {
                return node;
            }
            if (node instanceof MethodDeclaration) {
                return node;
            }
            node.getParentNode().ifPresent(visiting::add);
        }
        return null;
    }




    public static boolean is_valid(Statement stmt, Set<Integer> reserved) {
        Optional<Range> rOptional = stmt.getRange();
        if (rOptional.isEmpty()) {
            return false;
        }
        int line = rOptional.get().begin.line;
        return reserved.contains(line);
    }

    public static void print(Node node) {
       System.out.println(node.getClass());
       System.out.println(node.getRange().get().begin.line);
       System.out.println("----------------");
    }

    public static int getNodeLineNum(Node node) {
        return node.getRange().isPresent() ?
                node.getRange().get().begin.line
                :
                -1;
    }
}
