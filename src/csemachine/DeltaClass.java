package csemachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ast.Node;
import ast.NodeType;

// Lambda closure

public class DeltaClass extends Node {
  private int indexNo;
  private Environment linkedEnvironmet;
  private Stack<Node> body;
  private List<String> Vars;

  public DeltaClass(){
    setType(NodeType.DELTA);
    Vars = new ArrayList<String>();
  }

  @Override
  public String getValue(){
    return "[lambda closure: "+ Vars.get(0)+": "+ indexNo +"]";
  }
  public void setVars(List<String> vars){
    this.Vars = vars;
  }
  public List<String> getVars(){
    return Vars;
  }
  public void setBody(Stack<Node> body){
    this.body = body;
  }
  public Stack<Node> getBody(){
    return body;
  }
  public void setIndexNo(int indexNo){
    this.indexNo = indexNo;
  }
  public int getIndexNo(){
    return indexNo;
  }
  public void setLinkedEnvironmet(Environment linkedEnvironmet){
    this.linkedEnvironmet = linkedEnvironmet;
  }
  public Environment getLinkedEnvironmet(){
    return linkedEnvironmet;
  }

  //adding a bound variable to the list of bound variables in a lambda closure
  public void addBoundVars(String boundVar){
    Vars.add(boundVar);
  }

  //accepting to copy
  public DeltaClass acceptance(BackupNode copier){
    return copier.nodeCopy(this);
  }
}
