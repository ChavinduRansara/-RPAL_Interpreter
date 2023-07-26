package ast;

import java.util.ArrayDeque;
import java.util.Stack;

import csemachine.BetaClass;
import csemachine.DeltaClass;


//Abstract Syntax Tree and standardizing

public class AST{
  private ArrayDeque<deltaBodyP> pendingQueue;
  private Node root;
  private int index;   // index of delta
  private DeltaClass deltaRoot;
  private DeltaClass currDelta;
  private boolean is_Standardized;

  public AST(Node node){
    this.root = node;
  }

  /*
     We do not standardize these key words:
     OR,AND,PLUS,MINUS,MULT,DIV,EXP,GR,GE,LS,LE,EQ,NE,NOT,NEG,CONDITIONAL,TAU,COMMA
   */

  //standardizing the entire Abstract Syntax Tree
  public void standardize(){
    standardize(root);
    is_Standardized = true;
  }

  // standardizing
  private void standardize(Node node){
    if(node.getChild()!=null){
      Node child = node.getChild();
      while(child!=null){
        standardize(child);
        child = child.getSibling();
      }
    }

    // standardize LET
    if (node.getType() == NodeType.LET) {
      Node eqNode = node.getChild();
      if (eqNode.getType() != NodeType.EQUAL) {
        throw new ExceptionHandleStandardize("Expect left child to be 'EQUAL'");
      }
      Node e = eqNode.getChild().getSibling();
      eqNode.getChild().setSibling(eqNode.getSibling());
      eqNode.setSibling(e);
      eqNode.setType(NodeType.LAMBDA);
      node.setType(NodeType.GAMMA);
    }
    // standardize WHERE
    else if (node.getType() == NodeType.WHERE) {
      Node eqNode = node.getChild().getSibling();
      node.getChild().setSibling(null);
      eqNode.setSibling(node.getChild());
      node.setChild(eqNode);
      node.setType(NodeType.LET);
      standardize(node);
    }
    // standardize WITHIN
    else if (node.getType() == NodeType.WITHIN) {
      if (node.getChild().getType() != NodeType.EQUAL || node.getChild().getSibling().getType() != NodeType.EQUAL)
        throw new ExceptionHandleStandardize("Expect one of the children is to be 'EQUAL'");
      Node child1 = node.getChild().getChild();
      Node sibling1 = child1.getSibling();
      Node child2 = node.getChild().getSibling().getChild();
      Node sibling2 = child2.getSibling();
      Node lambda = new Node();
      lambda.setType(NodeType.LAMBDA);
      child1.setSibling(sibling2);
      lambda.setChild(child1);
      lambda.setSibling(sibling1);
      Node gamma = new Node();
      gamma.setType(NodeType.GAMMA);
      gamma.setChild(lambda);
      child2.setSibling(gamma);
      node.setChild(child2);
      node.setType(NodeType.EQUAL);
    }
    // standardize AT
    else if (node.getType() == NodeType.AT) {
      Node child1 = node.getChild();
      Node sibling1 = child1.getSibling();
      Node sibling2 = sibling1.getSibling();
      Node gamma = new Node();
      gamma.setType(NodeType.GAMMA);
      gamma.setChild(sibling1);
      sibling1.setSibling(child1);
      child1.setSibling(null);
      gamma.setSibling(sibling2);
      node.setChild(gamma);
      node.setType(NodeType.GAMMA);
    }
    // standardize FCNFORM
    else if (node.getType() == NodeType.FCNFORM) {
      Node childSibling = node.getChild().getSibling();
      node.getChild().setSibling(constructLambdaChain(childSibling));
      node.setType(NodeType.EQUAL);
    }
    // standardize REC
    else if (node.getType() == NodeType.REC) {
      Node child = node.getChild();
      if (child.getType() != NodeType.EQUAL)
        throw new ExceptionHandleStandardize("Expect child is to be 'EQUAL'");
      Node child1 = child.getChild();
      Node lambda = new Node();
      lambda.setType(NodeType.LAMBDA);
      lambda.setChild(child1);
      Node yStar = new Node();
      yStar.setType(NodeType.YSTAR);
      yStar.setSibling(lambda);
      Node gamma = new Node();
      gamma.setType(NodeType.GAMMA);
      gamma.setChild(yStar);
      Node child1_WithSiblingGamma = new Node();
      child1_WithSiblingGamma.setChild(child1.getChild());
      child1_WithSiblingGamma.setSibling(gamma);
      child1_WithSiblingGamma.setType(child1.getType());
      child1_WithSiblingGamma.setValue(child1.getValue());
      node.setChild(child1_WithSiblingGamma);
      node.setType(NodeType.EQUAL);
    }
    // standardize SIMULTDEF
    else if (node.getType() == NodeType.SIMULTDEF) {
      Node comma = new Node();
      comma.setType(NodeType.COMMA);
      Node tau = new Node();
      tau.setType(NodeType.TAU);
      Node child = node.getChild();
      while (child != null) {
        populateComma_TauNode(child, comma, tau);
        child = child.getSibling();
      }
      comma.setSibling(tau);
      node.setChild(comma);
      node.setType(NodeType.EQUAL);
    }
    // standardize LAMBDA
    else if (node.getType() == NodeType.LAMBDA) {
      Node childSibling = node.getChild().getSibling();
      node.getChild().setSibling(constructLambdaChain(childSibling));
    }
  }


  // creates a new child of the parent
  private void childNodeMake(Node parent, Node child){
    if(parent.getChild()==null)
      parent.setChild(child);
    else{
      Node prevSibling = parent.getChild();
      while(prevSibling.getSibling()!=null)
        prevSibling = prevSibling.getSibling();
      prevSibling.setSibling(child);
    }
    child.setSibling(null);
  }

  //restructuring
  private void populateComma_TauNode(Node equal, Node comma, Node tau){
    if(equal.getType()!= NodeType.EQUAL)
      throw new ExceptionHandleStandardize("Expect one of the children is to be 'EQUAL'");
    Node child = equal.getChild();
    Node sibling = child.getSibling();
    childNodeMake(comma, child);
    childNodeMake(tau, sibling);
  }

  // construct a chain of lambda nodes
  private Node constructLambdaChain(Node node){
    if(node.getSibling()==null) return node;
    Node lambda = new Node();
    lambda.setType(NodeType.LAMBDA);
    lambda.setChild(node);
    if(node.getSibling().getSibling()!=null)
      node.setSibling(constructLambdaChain(node.getSibling()));
    return lambda;
  }

  // Creates delta structures
  public DeltaClass deltasCreate(){
    pendingQueue = new ArrayDeque<deltaBodyP>();
    index = 0;
    currDelta = deltaCreate(root);
    pendingDeltaStackProcess();
    return deltaRoot;
  }

  private DeltaClass deltaCreate(Node startBody){
    deltaBodyP deltaBodyP = new deltaBodyP();
    deltaBodyP.start = startBody;
    deltaBodyP.body = new Stack<Node>();
    pendingQueue.add(deltaBodyP);
    DeltaClass delta = new DeltaClass();
    delta.setBody(deltaBodyP.body);
    delta.setIndexNo(index++);
    currDelta = delta;
    if(startBody==root) deltaRoot = currDelta;
    return delta;
  }

  //creating delta structures that are stored in the pendingQueue
  private void pendingDeltaStackProcess(){
    while(!pendingQueue.isEmpty()){
      deltaBodyP deltaBodyP = pendingQueue.pop();
      ConstructDeltaBody(deltaBodyP.start, deltaBodyP.body);
    }
  }

  //constructing the body of a Delta structure
  private void ConstructDeltaBody(Node node, Stack<Node> body){
    if(node.getType()== NodeType.LAMBDA){
      DeltaClass delta = deltaCreate(node.getChild().getSibling());
      if(node.getChild().getType()== NodeType.COMMA){
        Node comma = node.getChild();
        Node child = comma.getChild();
        while(child!=null){
          delta.addBoundVars(child.getValue());
          child = child.getSibling();
        }
      }
      else
        delta.addBoundVars(node.getChild().getValue());
      body.push(delta);
      return;
    }
    else if(node.getType()== NodeType.CONDITIONAL){
      Node condition = node.getChild();
      Node then = condition.getSibling();
      Node nodeElse = then.getSibling();
      
      //Add a Beta node.
      BetaClass beta = new BetaClass();
      
      ConstructDeltaBody(then, beta.getBodyOfThen());
      ConstructDeltaBody(nodeElse, beta.getBodyOfElse());
      
      body.push(beta);
      
      ConstructDeltaBody(condition, body);
      
      return;
    }
    
    //pre Order walk
    body.push(node);
    Node child = node.getChild();
    while(child!=null){
      ConstructDeltaBody(child, body);
      child = child.getSibling();
    }
  }

  private class deltaBodyP {
    Stack<Node> body;
    Node start;
  }

  //checking whether the AST has been standardized or not.
  public boolean isStandardized(){
    return is_Standardized;
  }
}
