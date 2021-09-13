package cse.ds;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public boolean isAttrNode = true;

    private String attribute;
    private List<Node> children;
    private Node nextAttr;

    public Node(){
        this.children = new ArrayList<>();
    }

    public static Node attrNode(String attribute){
        Node newNode = new Node();
        newNode.isAttrNode = true;
        newNode.attribute = attribute;
        return newNode;
    }

    public static Node verNode(String value){
        Node newNode = new Node();
        newNode.isAttrNode = false;
        newNode.attribute = value;
        return newNode;
    }

    public void setNextAttr(Node attrNode){
        this.nextAttr = attrNode;
    }

    public Node getNextAttr(){
        return this.nextAttr;
    }

    public List<Node> getChildren(){
        return this.children;
    }

    public void setAttribute(String attribute){
        this.attribute = attribute;
    }

    public String getAttribute(){
        return this.attribute;
    }

    public String toString(){
        StringBuilder str = new StringBuilder();
        if(this.isAttrNode){
            str.append("(").append(this.attribute).append(", ");
            for(int i = 0 ; i < this.getChildren().size() ; i++){
                Node child = this.getChildren().get(i);
                str.append(child).append(",");
            }
            str.append(")");
        } else {
            str.append("{").append(this.attribute).append(", ");
            str.append(this.getNextAttr());
            str.append("}");
        }
        return str.toString();
    }
}