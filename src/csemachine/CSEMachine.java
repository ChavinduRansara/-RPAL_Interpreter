package csemachine;

import java.util.Stack;
import ast.AST;
import ast.Node;
import ast.NodeType;

public class CSEMachine{
  private Stack<Node> value;
  private DeltaClass root;

  public CSEMachine(AST ast){
    if(!ast.isStandardized())
      throw new RuntimeException("Standardized failed!");
    root = ast.deltasCreate();
    root.setLinkedEnvironmet(new Environment());
    value = new Stack<Node>();
  }

  //initiating the evaluation process
  public void evaluate(){
    controlProcess(root, root.getLinkedEnvironmet());
  }

  //create a new control stack and add all of delta's body to it
  private void controlProcess(DeltaClass delta, Environment env){
    Stack<Node> control_Stack = new Stack<Node>();
    control_Stack.addAll(delta.getBody());
    
    while(!control_Stack.isEmpty())
      currentNodeProcess(delta, env, control_Stack);
  }

  //evaluating the current node
  private void currentNodeProcess(DeltaClass delta, Environment env, Stack<Node> control_Stack){

    Node n = control_Stack.pop();

    if(unaryOperation(n)) return;
    else if(binaryOperation(n)) return;
    else{
      switch(n.getType()){
        case IDENTIFIER:
          identifierHandale(n, env);
          break;
        case NIL:
        case TAU:
          tupleBuild(n);
          break;
        case BETA:
          betaHandel((BetaClass)n, control_Stack);
          break;
        case GAMMA:
          gamma(delta, n, env, control_Stack);
          break;
        case DELTA:
          ((DeltaClass)n).setLinkedEnvironmet(env);
          value.push(n);
          break;
        default:
          value.push(n);
          break;
      }
    }
  }

  //comparing two string nodes
  private void stringsCompare(Node oprandNode1, Node oprandNode2, NodeType nodeType){
    if(oprandNode1.getValue().equals(oprandNode2.getValue()))
      if(nodeType== NodeType.EQ)
        exactNodePush();
      else
        falseNodePush();
    else
    if(nodeType== NodeType.EQ)
      falseNodePush();
    else
      exactNodePush();
  }

  //comparing two Integer nodes
  private void integersCompare(Node oprandNode1, Node oprandNode2, NodeType nodeType){
    if(Integer.parseInt(oprandNode1.getValue())==Integer.parseInt(oprandNode2.getValue()))
      if(nodeType== NodeType.EQ)
        exactNodePush();
      else
        falseNodePush();
    else
    if(nodeType== NodeType.EQ)
      falseNodePush();
    else
      exactNodePush();
  }

  //checking if a given string val represents a reserved identifier.
  private boolean checkReservedIdentifier(String val){
    switch (val){
      case "Isinteger":
      case "Isstring":
      case "Istuple":
      case "Isdummy":
      case "Istruthvalue":
      case "Isfunction":
      case "ItoS":
      case "Order":
      case "Conc":
      case "conc":
      case "Stern":
      case "Stem":
      case "Null":
      case "Print":
      case "print":
      case "neg":
        return true;
    }
    return false;
  }

  //checking if a given node represents a binary operation.
  private boolean binaryOperation(Node node){
    switch(node.getType()){
      case PLUS:
      case MINUS:
      case MULT:
      case DIV:
      case EXP:
      case LS:
      case LE:
      case GR:
      case GE:
        binaryArithmeticOperation(node.getType());
        return true;
      case EQ:
      case NE:
        binaryLogicalOperation(node.getType());
        return true;
      case OR:
      case AND:
        OR_AND_Operation(node.getType());
        return true;
      case AUG:
        aug_Tuples();
        return true;
      default:
        return false;
    }
  }

  //perform binary arithmetic operations.
  private void binaryArithmeticOperation(NodeType nodeType){

    Node oprandNode1 = value.pop();
    Node oprandNode2 = value.pop();

    if(oprandNode1.getType()!= NodeType.INTEGER || oprandNode2.getType()!= NodeType.INTEGER)
      Error.printError(oprandNode1.getNumberOfLine(), "Two integers are expected.Not \""+oprandNode1.getValue()+"\", \""+oprandNode2.getValue()+"\"");

    Node resultNode = new Node();
    resultNode.setType(NodeType.INTEGER);

    switch(nodeType){
      case PLUS:
        resultNode.setValue(Integer.toString(Integer.parseInt(oprandNode1.getValue())+Integer.parseInt(oprandNode2.getValue())));
        break;
      case MINUS:
        resultNode.setValue(Integer.toString(Integer.parseInt(oprandNode1.getValue())-Integer.parseInt(oprandNode2.getValue())));
        break;
      case MULT:
        resultNode.setValue(Integer.toString(Integer.parseInt(oprandNode1.getValue())*Integer.parseInt(oprandNode2.getValue())));
        break;
      case DIV:
        resultNode.setValue(Integer.toString(Integer.parseInt(oprandNode1.getValue())/Integer.parseInt(oprandNode2.getValue())));
        break;
      case EXP:
        resultNode.setValue(Integer.toString((int)Math.pow(Integer.parseInt(oprandNode1.getValue()), Integer.parseInt(oprandNode2.getValue()))));
        break;
      case LS:
        if(Integer.parseInt(oprandNode1.getValue())<Integer.parseInt(oprandNode2.getValue()))
          exactNodePush();
        else
          falseNodePush();
        return;
      case LE:
        if(Integer.parseInt(oprandNode1.getValue())<=Integer.parseInt(oprandNode2.getValue()))
          exactNodePush();
        else
          falseNodePush();
        return;
      case GR:
        if(Integer.parseInt(oprandNode1.getValue())>Integer.parseInt(oprandNode2.getValue()))
          exactNodePush();
        else
          falseNodePush();
        return;
      case GE:
        if(Integer.parseInt(oprandNode1.getValue())>=Integer.parseInt(oprandNode2.getValue()))
          exactNodePush();
        else
          falseNodePush();
        return;
      default:
        break;
    }
    value.push(resultNode);
  }

  //perform logical OR and AND operations.
  private void OR_AND_Operation(NodeType nodeType){

    Node oprandNode1 = value.pop();
    Node oprandNode2 = value.pop();

    if((oprandNode1.getType()== NodeType.TRUE || oprandNode1.getType()== NodeType.FALSE) && (oprandNode2.getType()== NodeType.TRUE || oprandNode2.getType()== NodeType.FALSE)){
      realVal_OR_AND(oprandNode1, oprandNode2, nodeType);
      return;
    }
    Error.printError(oprandNode1.getNumberOfLine(), "Error" + nodeType + " \""+oprandNode1.getValue()+"\", \""+oprandNode2.getValue()+"\"");
  }

  //perform binary logical operations
  private void binaryLogicalOperation(NodeType type){

    Node oprandNode1 = value.pop();
    Node oprandNode2 = value.pop();

    if(oprandNode1.getType()== NodeType.TRUE || oprandNode1.getType()== NodeType.FALSE){
      if(oprandNode2.getType()!= NodeType.TRUE && oprandNode2.getType()!= NodeType.FALSE)
        Error.printError(oprandNode1.getNumberOfLine(), "Cannot compare \""+oprandNode1.getValue()+"\", \""+oprandNode2.getValue()+"\"");
      realValuesCompare(oprandNode1, oprandNode2, type);
      return;
    }
    if(oprandNode1.getType()!=oprandNode2.getType())
      Error.printError(oprandNode1.getNumberOfLine(), "Cannot compare \""+oprandNode1.getValue()+"\", \""+oprandNode2.getValue()+"\"");
    if(oprandNode1.getType()== NodeType.STRING)
      stringsCompare(oprandNode1, oprandNode2, type);
    else if(oprandNode1.getType()== NodeType.INTEGER)
      integersCompare(oprandNode1, oprandNode2, type);
    else
      Error.printError(oprandNode1.getNumberOfLine(), "Error" + type + " \""+oprandNode1.getValue()+"\", \""+oprandNode2.getValue()+"\"");
  }

  //handle unary operations.
  private boolean unaryOperation(Node node){
    switch(node.getType()){
      case NOT:
        NOT_op();
        return true;
      case NEG:
        neg_op();
        return true;
      default:
        return false;
    }
  }

  private void realValuesCompare(Node oprandNode1, Node oprandNode2, NodeType nodeType){
    if(oprandNode1.getType()==oprandNode2.getType())
      if(nodeType== NodeType.EQ)
        exactNodePush();
      else
        falseNodePush();
    else
      if(nodeType== NodeType.EQ)
        falseNodePush();
      else
        exactNodePush();
  }

  private void realVal_OR_AND(Node oprandNode1, Node oprandNode2, NodeType nodeType){
    if(nodeType== NodeType.OR){
      if(oprandNode1.getType()== NodeType.TRUE || oprandNode2.getType()== NodeType.TRUE)
        exactNodePush();
      else
        falseNodePush();
    }
    else{
      if(oprandNode1.getType()== NodeType.TRUE && oprandNode2.getType()== NodeType.TRUE)
        exactNodePush();
      else
        falseNodePush();
    }
  }

  //perform the augmentation operation
  private void aug_Tuples(){

    Node node1 = value.pop();
    Node node2 = value.pop();

    if(node1.getType()!= NodeType.TUPLE)
      Error.printError(node1.getNumberOfLine(), "Cannot augment \""+node1.getValue()+"\"");

    Node nodeChild = node1.getChild();

    if(nodeChild==null)
      node1.setChild(node2);
    else{
      while(nodeChild.getSibling()!=null)
        nodeChild = nodeChild.getSibling();
      nodeChild.setSibling(node2);
    }

    node2.setSibling(null);
    value.push(node1);
  }

  //perform the logical NOT operation
  private void NOT_op(){
    Node node = value.pop();
    if(node.getType()!= NodeType.TRUE && node.getType()!= NodeType.FALSE)
      Error.printError(node.getNumberOfLine(), "Error \""+node.getValue()+"\"");
    if(node.getType()== NodeType.TRUE)
      falseNodePush();
    else
      exactNodePush();
  }

  //evaluating operations involving reserved identifiers
  private boolean reservedIdentifiersEvaluate(Node opratorNode, Node oprandNode, Stack<Node> currStack){
    switch(opratorNode.getValue()){
      case "Isinteger":
        checkType(oprandNode, NodeType.INTEGER);
        return true;
      case "Isstring":
        checkType(oprandNode, NodeType.STRING);
        return true;
      case "Isdummy":
        checkType(oprandNode, NodeType.DUMMY);
        return true;
      case "Isfunction":
        checkType(oprandNode, NodeType.DELTA);
        return true;
      case "Istuple":
        checkType(oprandNode, NodeType.TUPLE);
        return true;
      case "Istruthvalue":
        if(oprandNode.getType()== NodeType.TRUE||oprandNode.getType()== NodeType.FALSE)
          exactNodePush();
        else
          falseNodePush();
        return true;
      case "Stem":
        stem(oprandNode);
        return true;
      case "Stern":
        stern(oprandNode);
        return true;
      case "Conc":
      case "conc":
        conc(oprandNode, currStack);
        return true;
      case "Print":
      case "print":
        nodeValuePrint(oprandNode);
        TempNodePush();
        return true;
      case "ItoS":
        itos(oprandNode);
        return true;
      case "Order":
        order(oprandNode);
        return true;
      case "Null":
        checkNullTuple(oprandNode);
        return true;
      default:
        return false;
    }
  }
  private void neg_op(){

    Node node = value.pop();

    if(node.getType()!= NodeType.INTEGER)
      Error.printError(node.getNumberOfLine(), "Error \""+node.getValue()+"\"");

    Node resultNode = new Node();
    resultNode.setType(NodeType.INTEGER);
    resultNode.setValue(Integer.toString(-1*Integer.parseInt(node.getValue())));
    value.push(resultNode);
  }

  //handling the evaluation of function calls
  private void gamma(DeltaClass delta, Node node, Environment env, Stack<Node> stack){

    Node opratorNode = value.pop();
    Node oprandNode = value.pop();

    if(opratorNode.getType()== NodeType.DELTA){
      DeltaClass nxtDelta = (DeltaClass) opratorNode;
      Environment newEnvironment = new Environment();
      newEnvironment.setParents(nxtDelta.getLinkedEnvironmet());

      if(nxtDelta.getVars().size()==1){
        newEnvironment.mapAdding(nxtDelta.getVars().get(0), oprandNode);
      }
      else{
        if(oprandNode.getType()!= NodeType.TUPLE)
          Error.printError(oprandNode.getNumberOfLine(), "A tuple is expected. Not \""+oprandNode.getValue()+"\"");
        int i = 0;
        while(i < nxtDelta.getVars().size()){
          newEnvironment.mapAdding(nxtDelta.getVars().get(i), nthTuple((TupleClass)oprandNode, i+1));
          i++;
        }
      }
      controlProcess(nxtDelta, newEnvironment);
      return;
    }
    else if(opratorNode.getType()== NodeType.YSTAR){
      if(oprandNode.getType()!= NodeType.DELTA)
        Error.printError(oprandNode.getNumberOfLine(), "A Delta is expected. Not\""+oprandNode.getValue()+"\"");
      
      EtaClass nodeEta = new EtaClass();
      nodeEta.setDelta((DeltaClass)oprandNode);
      value.push(nodeEta);
      return;
    }
    else if(opratorNode.getType()== NodeType.ETA){
      value.push(oprandNode);
      value.push(opratorNode);
      value.push(((EtaClass)opratorNode).getDelta());
      stack.push(node);
      stack.push(node);
      return;
    }
    else if(opratorNode.getType()== NodeType.TUPLE){
      selectTuple((TupleClass)opratorNode, oprandNode);
      return;
    }
    else if(reservedIdentifiersEvaluate(opratorNode, oprandNode, stack))
      return;
    else
      Error.printError(opratorNode.getNumberOfLine(), "Can't evaluate \""+opratorNode.getValue()+"\"");
  }

  //checking the type of a node
  private void checkType(Node oprandNode, NodeType nodeType){
    if(oprandNode.getType()==nodeType) exactNodePush();
    else falseNodePush();
  }

  private void exactNodePush(){
    Node exactNode = new Node();
    exactNode.setType(NodeType.TRUE);
    exactNode.setValue("true");
    value.push(exactNode);
  }
  
  private void falseNodePush(){
    Node falseNode = new Node();
    falseNode.setType(NodeType.FALSE);
    falseNode.setValue("false");
    value.push(falseNode);
  }

  private void TempNodePush(){
    Node tempNode = new Node();
    tempNode.setType(NodeType.DUMMY);
    value.push(tempNode);
  }

  //extracting the stem (first character) of a string node
  private void stem(Node oprandNode){
    if(oprandNode.getType()!= NodeType.STRING)
      Error.printError(oprandNode.getNumberOfLine(), "A string is expected. Not  \""+oprandNode.getValue()+"\"");
    else if(oprandNode.getValue().isEmpty())
      oprandNode.setValue("");
    else
      oprandNode.setValue(oprandNode.getValue().substring(0,1));
    
    value.push(oprandNode);
  }

  //extracting the stern (substring excluding the first character) of a string node
  private void stern(Node oprandNode){
    if(oprandNode.getType()!= NodeType.STRING)
      Error.printError(oprandNode.getNumberOfLine(), "A string is expected. Not \""+oprandNode.getValue()+"\"");
    else if(oprandNode.getValue().isEmpty() || oprandNode.getValue().length()==1)
      oprandNode.setValue("");
    else
      oprandNode.setValue(oprandNode.getValue().substring(1));
    
    value.push(oprandNode);
  }

  //concatenating two string nodes
  private void conc(Node oprandNode, Stack<Node> currStack){

    Node oprandNode2 = value.pop();
    currStack.pop();

    if(oprandNode.getType()!= NodeType.STRING || oprandNode2.getType()!= NodeType.STRING)
      Error.printError(oprandNode.getNumberOfLine(), "Two strings are expected. Not \""+oprandNode.getValue()+"\", \""+oprandNode2.getValue()+"\"");

    Node resultNode = new Node();
    resultNode.setType(NodeType.STRING);
    resultNode.setValue(oprandNode.getValue()+oprandNode2.getValue());
    
    value.push(resultNode);
  }
  private void itos(Node oprandNode){
    if(oprandNode.getType()!= NodeType.INTEGER)
      Error.printError(oprandNode.getNumberOfLine(), "An integer is expected. Not \""+oprandNode.getValue()+"\"");
    
    oprandNode.setType(NodeType.STRING);
    value.push(oprandNode);
  }

  private void order(Node oprandNode){

    if(oprandNode.getType()!= NodeType.TUPLE)
      Error.printError(oprandNode.getNumberOfLine(), "A tuple is expected. Not \""+oprandNode.getValue()+"\"");

    Node resultNode = new Node();
    resultNode.setType(NodeType.INTEGER);
    resultNode.setValue(Integer.toString(getChildrenCount(oprandNode)));
    
    value.push(resultNode);
  }

  //checking nullity
  private void checkNullTuple(Node oprandNode){
    if(oprandNode.getType()!= NodeType.TUPLE)
      Error.printError(oprandNode.getNumberOfLine(), "A tuple is expected. Not \""+oprandNode.getValue()+"\"");
    else if(getChildrenCount(oprandNode)==0)
      exactNodePush();
    else
      falseNodePush();
  }
  private void selectTuple(TupleClass oprator, Node oprandNode){
    if(oprandNode.getType()!= NodeType.INTEGER)
      Error.printError(oprandNode.getNumberOfLine(), "Non-integer tuple selection with \""+oprandNode.getValue()+"\"");

    Node resultNode = nthTuple(oprator, Integer.parseInt(oprandNode.getValue()));

    if(resultNode==null)
      Error.printError(oprandNode.getNumberOfLine(), "Tuple selection index "+oprandNode.getValue()+" out of bounds");

    value.push(resultNode);
  }

  // Get nth element of the tuple.
  private Node nthTuple(TupleClass tupleNode, int n){

    int i =1;
    Node child = tupleNode.getChild();

    while (i<n){
      if(child==null)
        break;
      child = child.getSibling();
      i++;
    }
    return child;
  }

  //handling identifiers
  private void identifierHandale(Node node, Environment currEnvironment){
    if(currEnvironment.checkup(node.getValue())!=null)
      value.push(currEnvironment.checkup(node.getValue()));
    else if(checkReservedIdentifier(node.getValue()))
      value.push(node);
    else
      Error.printError(node.getNumberOfLine(), "Identifier is Undeclared \""+node.getValue()+"\"");
  }

  //building tuple nodes
  private void tupleBuild(Node tNode){

    TupleClass tupleNode = new TupleClass();
    int No_ofChildren = getChildrenCount(tNode);

    if(No_ofChildren==0){
      value.push(tupleNode);
      return;
    }

    Node child = null, tempNode = null;
    int i = 0;
    while (i<No_ofChildren){
      if(child==null)
        child = value.pop();
      else if(tempNode==null){
        tempNode = value.pop();
        child.setSibling(tempNode);
      } else{
        tempNode.setSibling(value.pop());
        tempNode = tempNode.getSibling();
      }
      i++;
    }

    tempNode.setSibling(null);
    tupleNode.setChild(child);
    value.push(tupleNode);
  }

  //handling beta reduction
  private void betaHandel(BetaClass bNode, Stack<Node> currStack){

    Node resultNode = value.pop();

    if(resultNode.getType()!= NodeType.TRUE && resultNode.getType()!= NodeType.FALSE)
      Error.printError(resultNode.getNumberOfLine(), "Error found \""+resultNode.getValue()+"\"");
    else if(resultNode.getType()== NodeType.TRUE)
      currStack.addAll(bNode.getBodyOfThen());
    else
      currStack.addAll(bNode.getBodyOfElse());
  }

  //printing the value of a node to the standard output (console).
  private void nodeValuePrint(Node oprandNode){

    String result = oprandNode.getValue();

    result = result.replace("\\t", "\t");
    result = result.replace("\\n", "\n");

    System.out.print(result);
  }

  //counting the number of child nodes.
  private int getChildrenCount(Node node){

    int No_OfChildren = 0;
    Node child = node.getChild();

    while(child!=null){
      No_OfChildren++;
      child = child.getSibling();
    }

    return No_OfChildren;
  }
}
