package fy.CCS.slicing;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.nodeTypes.NodeWithCondition;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.Node.PreOrderIterator;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

public class SrcCodeTransformer {

    public static MethodDeclaration slice (MethodDeclaration n, List<Integer> reserved) {
        Set<BlockStmt> modifiedBlocks = new HashSet<>();
        Map<Statement, Integer> removedStmtIndexMap = new HashMap<>();
        PreOrderIterator iterator = new PreOrderIterator(n);
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
                modifiedBlocks.add(clonedBlock);
            }
        }

        Map<BlockStmt, BlockStmt> originalMap = new HashMap<>();
        Map<BlockStmt, Integer> parBlockMap = new HashMap<>();
        Map<BlockStmt, Node> containingStmtMap = new HashMap<>();
        Map<Pair<BlockStmt, Integer>, Integer> indexMap = new HashMap<>();
        modifiedBlocks.forEach(modified -> {
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

        for (BlockStmt b : modifiedBlocks) {
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

//        for (BlockStmt b : modifiedBlocks) {
//            Node containingNode = containingStmtMap.get(b);
//            BlockStmt oriBlock = originalMap.get(b);
//            if (containingNode != null && oriBlock != null) {
//                containingNode.replace(oriBlock, b);
//            }
//        }

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

    public static BlockStmt findValidParentBlock(BlockStmt block, List<Integer> reserved) {
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




    public static boolean is_valid(Statement stmt, List<Integer> reserved) {
        Optional<Range> rOptional = stmt.getRange();
        if (rOptional.isEmpty()) {
            return false;
        }
        int line = rOptional.get().begin.line;
        return reserved.contains(line);
    }

    public static void print(Statement statement) {
        System.out.println(statement.getClass());
        System.out.println(statement.getBegin().get().line);
        System.out.println("---------");
    }

    public static int getNodeLineNum(Node node) {
        return node.getRange().isPresent() ?
                node.getRange().get().begin.line
                :
                -1;
    }
}
