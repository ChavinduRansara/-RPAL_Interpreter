package ast;

// Types of abstract syntax tree node
public enum NodeType {
  LET("let"),
  IDENTIFIER("<ID:%s>"),
  STRING("<STR:'%s'>"),
  INTEGER("<INT:%s>"),
  TAU("tau"),
  LAMBDA("lambda"),
  WHERE("where"),
  CONDITIONAL("->"),
  OR("or"),
  AND("&"),
  NOT("not"),
  AUG("aug"),
  GE("ge"),
  LS("ls"),
  LE("le"),
  EQ("eq"),
  NE("ne"),
  GR("gr"),
  AT("@"),
  PLUS("+"),
  MINUS("-"),
  NEG("neg"),
  MULT("*"),
  DIV("/"),
  EXP("**"),
  NIL("<nil>"),
  DUMMY("<dummy>"),
  WITHIN("within"),
  SIMULTDEF("and"),
  GAMMA("gamma"),
  TRUE("<true>"),
  FALSE("<false>"),
  EQUAL("="),
  FCNFORM("function_form"),
  PAREN("<()>"),
  REC("rec"),
  COMMA(","),
  YSTAR("<Y*>"),
  BETA(""),
  DELTA(""),
  ETA(""),
  TUPLE("");
  
  private String printName;

  NodeType(String name){
    printName = name;
  }

}
