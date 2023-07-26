package csemachine;

import ast.Node;
import ast.NodeType;
/* representing tuples in the abstract syntax tree */
public class TupleClass extends Node {
  
  public TupleClass(){
    setType(NodeType.TUPLE);
  }

  //creating a copy
  public TupleClass acceptance(BackupNode copier){
    return copier.nodeCopy(this);
  }

  //getting string value
  @Override
  public String getValue(){
    Node child = getChild();
    if(child==null) return "nil";
    String valPrint = "(";
    while(child.getSibling()!=null){
      valPrint += child.getValue() + ", ";
      child = child.getSibling();
    }
    valPrint += child.getValue() + ")";
    return valPrint;
  }
}
