package fy.GD.basic;

import com.github.javaparser.ast.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个class 是专门为后期往cfg图结构中加入ast的结构信息定义的数据结构
 */
public class AstNode implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name; // 这个name 是用来生成dot 中的name用的
    private String typeName;
    private int lineBegin;
    private Node rootPrimary;

    /**
     * 叶子节点 所以遍历的时候无需再继续递归了
     */
    private List<String> attributes;

    /**
     * 中间节点 不是一种集合类别
     */
    private List<AstNode> subNodes;

    /**
     * javaPaser 中自己的Node节点
     */
    private List<Node> subNodesPrimary;
    /**
     * 一种集合类别的节点 比如imports
     */
    private List<String> subLists;

    /**
     * 只是dot遍历时候需要用,用在集合类别节点那一快
     */
    private List<String> subLists_name;

    /**
     * 集合中所有的Nodes
     */
    private List<List<AstNode>> subListNodes;
    /**
     * 集合中所有的原来JavaPaser的Nodes
     */
    private List<List<Node>> subListNodesPrimary;

    public AstNode() {
        this.attributes = new ArrayList<>();
        this.subNodes = new ArrayList<>();
        this.subLists = new ArrayList<>();
        this.subListNodes = new ArrayList<>();
        this.subLists_name = new ArrayList<>();
        this.subListNodesPrimary = new ArrayList<>();
        this.subNodesPrimary = new ArrayList<>();
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public List<AstNode> getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(List<AstNode> subNodes) {
        this.subNodes = subNodes;
    }

    public List<String> getSubLists() {
        return subLists;
    }

    public void setSubLists(List<String> subLists) {
        this.subLists = subLists;
    }

    public List<List<AstNode>> getSubListNodes() {
        return subListNodes;
    }

    public void setSubListNodes(List<List<AstNode>> subListNodes) {
        this.subListNodes = subListNodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSubLists_name() {
        return subLists_name;
    }

    public void setSubLists_name(List<String> subLists_name) {
        this.subLists_name = subLists_name;
    }

    public int getLineBegin() {
        return lineBegin;
    }

    public void setLineBegin(int lineBegin) {
        this.lineBegin = lineBegin;
    }

    public List<Node> getSubNodesPrimary() {
        return subNodesPrimary;
    }

    public void setSubNodesPrimary(List<Node> subNodesPrimary) {
        this.subNodesPrimary = subNodesPrimary;
    }

    public List<List<Node>> getSubListNodesPrimary() {
        return subListNodesPrimary;
    }

    public void setSubListNodesPrimary(List<List<Node>> subListNodesPrimary) {
        this.subListNodesPrimary = subListNodesPrimary;
    }

    public Node getRootPrimary() {
        return rootPrimary;
    }

    public void setRootPrimary(Node rootPrimary) {
        this.rootPrimary = rootPrimary;
    }

    @Override
    public String toString() {
        return "AstNode{" +
                "name='" + name + '\'' +
                ", typeName='" + typeName + '\'' +
                ", lineBegin=" + lineBegin +
                ", rootPrimary=" + rootPrimary +
                ", attributes=" + attributes +
                ", subNodes=" + subNodes +
                ", subNodesPrimary=" + subNodesPrimary +
                ", subLists=" + subLists +
                ", subLists_name=" + subLists_name +
                ", subListNodes=" + subListNodes +
                ", subListNodesPrimary=" + subListNodesPrimary +
                '}';
    }
}
