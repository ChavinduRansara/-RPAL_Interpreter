package ast;

import csemachine.BackupNode;

// AST Nodes
public class Node {
  private NodeType type;
  private String value;
  private Node child;
  private Node sibling;
  private int numberOfLine;

  //setting the type of the node.
  public void setType(NodeType type){
    this.type = type;
  }
  //getting the type of the node.
  public NodeType getType(){
    return type;
  }

  //setting the child node for the current node.
  public void setChild(Node child){
    this.child = child;
  }
  //getting the child node of the current node.
  public Node getChild(){
    return child;
  }

  //setting the sibling node for the current node
  public void setSibling(Node sibling){
    this.sibling = sibling;
  }
  //getting the sibling node of the current node
  public Node getSibling(){
    return sibling;
  }

  //setting value
  public void setValue(String value){
    this.value = value;
  }
  //getting value
  public String getValue(){
    return value;
  }

  //setting the line number
  public void setNumberOfLine(int numberOfLine){
    this.numberOfLine = numberOfLine;
  }
  //getting the line number
  public int getNumberOfLine(){
    return numberOfLine;
  }
  public Node acceptance(BackupNode copier){
    return copier.nodeCopy(this);
  }
}
