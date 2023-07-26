package csemachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ast.Node;

// Used to make copies of nodes on value stack.

public class BackupNode {

  //creating a deep copy of a generic AST node.
  public Node nodeCopy(Node nodeAST){
    Node cp = new Node();
    if(nodeAST.getChild()!=null)
      cp.setChild(nodeAST.getChild().acceptance(this));
    if(nodeAST.getSibling()!=null)
      cp.setSibling(nodeAST.getSibling().acceptance(this));
    cp.setType(nodeAST.getType());
    cp.setValue(nodeAST.getValue());
    cp.setNumberOfLine(nodeAST.getNumberOfLine());
    return cp;
  }
  //creating a deep copy of a BetaClass.
  public BetaClass nodeCopy(BetaClass beta){
    BetaClass cp = new BetaClass();
    if(beta.getChild()!=null)
      cp.setChild(beta.getChild().acceptance(this));
    if(beta.getSibling()!=null)
      cp.setSibling(beta.getSibling().acceptance(this));
    cp.setType(beta.getType());
    cp.setValue(beta.getValue());
    cp.setNumberOfLine(beta.getNumberOfLine());
    Stack<Node> copy_bodyOfThen = new Stack<Node>();
    for(Node element_bodyOfThen: beta.getBodyOfThen()){
      copy_bodyOfThen.add(element_bodyOfThen.acceptance(this));
    }
    cp.setBodyOfThen(copy_bodyOfThen);
    Stack<Node> copy_bodyOfElse = new Stack<Node>();
    for(Node element_bodyOfElse: beta.getBodyOfElse()){
      copy_bodyOfElse.add(element_bodyOfElse.acceptance(this));
    }
    cp.setBodyOfElse(copy_bodyOfElse);

    return cp;
  }
  //creating a deep copy of a DeltaClass.
  public DeltaClass nodeCopy(DeltaClass delta){
    DeltaClass cp = new DeltaClass();
    if(delta.getChild()!=null)
      cp.setChild(delta.getChild().acceptance(this));
    if(delta.getSibling()!=null)
      cp.setSibling(delta.getSibling().acceptance(this));
    cp.setType(delta.getType());
    cp.setValue(delta.getValue());
    cp.setIndexNo(delta.getIndexNo());
    cp.setNumberOfLine(delta.getNumberOfLine());
    Stack<Node> copyBody = new Stack<Node>();
    for(Node elementOfBody: delta.getBody()){
      copyBody.add(elementOfBody.acceptance(this));
    }
    cp.setBody(copyBody);
    List<String> copyVars = new ArrayList<String>();
    copyVars.addAll(delta.getVars());
    cp.setVars(copyVars);
    cp.setLinkedEnvironmet(delta.getLinkedEnvironmet());
    return cp;
  }
  //creating a deep copy of a EtaClass.
  public EtaClass nodeCopy(EtaClass eta){
    EtaClass cp = new EtaClass();
    if(eta.getChild()!=null)
      cp.setChild(eta.getChild().acceptance(this));
    if(eta.getSibling()!=null)
      cp.setSibling(eta.getSibling().acceptance(this));
    cp.setType(eta.getType());
    cp.setValue(eta.getValue());
    cp.setNumberOfLine(eta.getNumberOfLine());
    cp.setDelta(eta.getDelta().acceptance(this));
    return cp;
  }
  //creating a deep copy of a TupleClass.
  public TupleClass nodeCopy(TupleClass tuple){
    TupleClass cp = new TupleClass();
    if(tuple.getChild()!=null)
      cp.setChild(tuple.getChild().acceptance(this));
    if(tuple.getSibling()!=null)
      cp.setSibling(tuple.getSibling().acceptance(this));
    cp.setType(tuple.getType());
    cp.setValue(tuple.getValue());
    cp.setNumberOfLine(tuple.getNumberOfLine());
    return cp;
  }
}
