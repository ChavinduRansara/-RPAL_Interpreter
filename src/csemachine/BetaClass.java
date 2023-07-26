package csemachine;

import java.util.Stack;

import ast.Node;
import ast.NodeType;

public class BetaClass extends Node {
  private Stack<Node> bodyOfThen;
  private Stack<Node> bodyOfElse;
  
  public BetaClass(){
    setType(NodeType.BETA);
    bodyOfThen = new Stack<Node>();
    bodyOfElse = new Stack<Node>();
  }

  public void setBodyOfThen(Stack<Node> bodyOfThen){
    this.bodyOfThen = bodyOfThen;
  }
  public Stack<Node> getBodyOfThen(){
    return bodyOfThen;
  }
  public void setBodyOfElse(Stack<Node> bodyOfElse){
    this.bodyOfElse = bodyOfElse;
  }
  public Stack<Node> getBodyOfElse(){
    return bodyOfElse;
  }
  public BetaClass acceptance(BackupNode copier){
    return copier.nodeCopy(this);
  }


  
}
