package parser;

import java.util.Stack;

import ast.AST;
import ast.Node;
import ast.NodeType;
import LexicalAnalyzer.Scanner;
import LexicalAnalyzer.Token;
import LexicalAnalyzer.NameOFToken;

/*
  Recursive descent parser that complies the RPAL's phrase structure grammar.
 */
public class Parser{
  private Token token;
  private Scanner scanner;
  Stack<Node> stack;


  public Parser(Scanner scanner){
    this.scanner = scanner;
    stack = new Stack<Node>();
  }
  
  public AST makeAST(){
    startParse();
    AST ast = new AST(stack.pop());
    return ast;
  }

  // initiating the parsing process
  public void startParse(){
    nextTokenRead();
    phraseStructure_E();
    if(token !=null)
      throw new ExceptionHandleParser("EOF.");
  }

  // reading the next token from the input source
  private void nextTokenRead(){
    do{
      token = scanner.nextTokenRead();
    }while(currentTokenTypeCheck(NameOFToken.DELETE));
    if(token != null){
      if(token.getNameOFToken()== NameOFToken.INTEGER){
        makeASTNodeTerminal(NodeType.INTEGER, token.getValue());
      }
      else if(token.getNameOFToken()== NameOFToken.STRING){
        makeASTNodeTerminal(NodeType.STRING, token.getValue());
      }
      else if(token.getNameOFToken()== NameOFToken.IDENTIFIER){
        makeASTNodeTerminal(NodeType.IDENTIFIER, token.getValue());
      }
    }
  }

  //checking whether the current token matches a specific NameOFToken enumeration and has a specific value
  private boolean currentTokenCheck(NameOFToken name, String value){
    if(token ==null) return false;
    if(token.getNameOFToken()!=name || !token.getValue().equals(value)) return false;
    return true;
  }

  //checking whether the current token belongs to a specific NameOFToken enumeration.
  private boolean currentTokenTypeCheck(NameOFToken name){
    if(token ==null) return false;
    if(token.getNameOFToken()==name) return true;
    return false;
  }

  //creating AST nodes array
  private void makeASTNode(NodeType name, int a){
    Node node = new Node();
    node.setType(name);
    while(a>0){
      Node childNode = stack.pop();
      if(node.getChild()!=null)
        childNode.setSibling(node.getChild());
      node.setChild(childNode);
      node.setNumberOfLine(childNode.getNumberOfLine());
      a--;
    }
    stack.push(node);
  }

  //creating terminal nodes in the AST
  private void makeASTNodeTerminal(NodeType name, String value){
    Node node = new Node();
    node.setType(name);
    node.setValue(value);
    node.setNumberOfLine(token.getNumberOFLine());
    stack.push(node);
  }

  /*
    E-> 'let' D 'in' E => 'let'
     -> 'fn' Vb+ '.' E => 'lambda'
     ->  Ew;
   */
  private void phraseStructure_E(){
    // E -> 'let' D 'in' E => 'let'
    if(currentTokenCheck(NameOFToken.KEYWORDS, "let")){
      nextTokenRead();
      phraseStructure_D();
      if(!currentTokenCheck(NameOFToken.KEYWORDS, "in"))
        throw new ExceptionHandleParser("'in' missing");
      nextTokenRead();
      phraseStructure_E();
      makeASTNode(NodeType.LET, 2);
    }
    // E -> 'fn' Vb+ '.' E => 'lambda'
    else if(currentTokenCheck(NameOFToken.KEYWORDS, "fn")){
      int trees = 0;
      nextTokenRead();
      while(currentTokenTypeCheck(NameOFToken.IDENTIFIER) || currentTokenTypeCheck(NameOFToken.L_PAREN)){
        phraseStructure_Vb();
        trees++;
      }
      
      if(trees==0)
        throw new ExceptionHandleParser("at least one 'Vb' expected");
      
      else if(!currentTokenCheck(NameOFToken.OPERATOR, "."))
        throw new ExceptionHandleParser("'.' missing");
      
      nextTokenRead();
      phraseStructure_E();
      
      makeASTNode(NodeType.LAMBDA, trees+1);
    }
    // E -> Ew
    else
      phraseStructure_Ew();
  }

  /*
    Ew -> T 'where' Dr => 'where'
       -> T;
   */
  private void phraseStructure_Ew(){
    // Ew -> T
    phraseStructure_T();
    // Ew -> T 'where' Dr => 'where'
    if(currentTokenCheck(NameOFToken.KEYWORDS, "where")){
      nextTokenRead();
      phraseStructure_Dr();
      makeASTNode(NodeType.WHERE, 2);
    }
  }

  
  /*
    T -> Ta ( ',' Ta )+ => 'tau'
      -> Ta;
   */
  private void phraseStructure_T(){
    // T -> Ta
    phraseStructure_Ta();
    // T -> Ta (',' Ta )+ => 'tau'
    int trees = 0;
    while(currentTokenCheck(NameOFToken.OPERATOR, ",")){
      nextTokenRead();
      phraseStructure_Ta();
      trees++;
    }
    if(trees > 0) makeASTNode(NodeType.TAU, trees+1);
  }

  /*
    Ta -> Ta 'aug' Tc => 'aug'
       -> Tc;
   */
  private void phraseStructure_Ta(){
    // Ta -> Tc
    phraseStructure_TC();
    // Ta -> Ta 'aug' Tc => 'aug'
    while(currentTokenCheck(NameOFToken.KEYWORDS, "aug")){
      nextTokenRead();
      phraseStructure_TC();
      makeASTNode(NodeType.AUG, 2);
    }
  }

  /*
    Tc -> B '->' Tc '|' Tc => '->'
       -> B;
   */
  private void phraseStructure_TC(){
    // Tc -> B
    phraseStructure_B();
    // Tc -> B '->' Tc '|' Tc => '->'
    if(currentTokenCheck(NameOFToken.OPERATOR, "->")){
      nextTokenRead();
      phraseStructure_TC();
      if(!currentTokenCheck(NameOFToken.OPERATOR, "|"))
        throw new ExceptionHandleParser("'|' missing");
      nextTokenRead();
      phraseStructure_TC();
      makeASTNode(NodeType.CONDITIONAL, 3);
    }
  }

  
  /*
    B -> B 'or' Bt => 'or'
      -> Bt;
   */
  private void phraseStructure_B(){
    // B -> Bt
    phraseStructure_Bt();
    // B -> B 'or' Bt => 'or'
    while(currentTokenCheck(NameOFToken.KEYWORDS, "or")){
      nextTokenRead();
      phraseStructure_Bt();
      makeASTNode(NodeType.OR, 2);
    }
  }
  
  /*
    Bt -> Bs '&' Bt => '&'
       -> Bs;
   */
  private void phraseStructure_Bt(){
    // Bt -> Bs;
    phraseStructure_Bs();
    // Bt -> Bt '&' Bs => '&'
    while(currentTokenCheck(NameOFToken.OPERATOR, "&")){
      nextTokenRead();
      phraseStructure_Bs();
      makeASTNode(NodeType.AND, 2);
    }
  }
  
  /*
    Bs -> 'not Bp => 'not'
       -> Bp;
   */
  private void phraseStructure_Bs(){
    // Bs -> 'not' Bp => 'not'
    if(currentTokenCheck(NameOFToken.KEYWORDS, "not")){
      nextTokenRead();
      phraseStructure_Bp();
      makeASTNode(NodeType.NOT, 1);
    }
    // Bs -> Bp
    else
      phraseStructure_Bp();
  }
  
  /*
    Bp -> A ('gr' | '>' ) A   => 'gr'
       -> A ('ge' | '>=' ) A  => 'ge'
       -> A ('ls' | '<' ) A   => 'ge'
       -> A ('le' | '<=' ) A  => 'ge'
       -> A 'eq' A            => 'eq'
       -> A 'ne' A            => 'ne'
       -> A;
   */
  private void phraseStructure_Bp(){
    //Bp -> A
    phraseStructure_A();
    //Bp -> A ('le' | '<=') A => 'le'
    if(currentTokenCheck(NameOFToken.KEYWORDS,"le")|| currentTokenCheck(NameOFToken.OPERATOR,"<=")){
      nextTokenRead();
      phraseStructure_A();
      makeASTNode(NodeType.LE, 2);
    }
    //Bp -> A ('ls' | '<' ) A => 'ls'
    else if(currentTokenCheck(NameOFToken.KEYWORDS,"ls")|| currentTokenCheck(NameOFToken.OPERATOR,"<")){
      nextTokenRead();
      phraseStructure_A();
      makeASTNode(NodeType.LS, 2);
    }
    //Bp -> A ('ge' | '>=') A => 'ge'
    else if(currentTokenCheck(NameOFToken.KEYWORDS,"ge")|| currentTokenCheck(NameOFToken.OPERATOR,">=")){
      nextTokenRead();
      phraseStructure_A();
      makeASTNode(NodeType.GE, 2);
    }
    //Bp -> A('gr' | '>' ) A => 'gr'
    else if(currentTokenCheck(NameOFToken.KEYWORDS,"gr")|| currentTokenCheck(NameOFToken.OPERATOR,">")){
      nextTokenRead();
      phraseStructure_A();
      makeASTNode(NodeType.GR, 2);
    }
    //Bp -> A 'eq' A => 'eq'
    else if(currentTokenCheck(NameOFToken.KEYWORDS,"eq")){
      nextTokenRead();
      phraseStructure_A();
      makeASTNode(NodeType.EQ, 2);
    }
    //Bp -> A 'ne' A => 'ne'
    else if(currentTokenCheck(NameOFToken.KEYWORDS,"ne")){
      nextTokenRead();
      phraseStructure_A();
      makeASTNode(NodeType.NE, 2);
    }
  }
  

  
  /*
    A -> A '+' At => '+'
      -> A '-' At => '-'
      ->   '+' At
      ->   '-' At => 'neg'
      -> At;
   */
  private void phraseStructure_A(){
    //A -> '+' At
    if(currentTokenCheck(NameOFToken.OPERATOR, "+")){
      nextTokenRead();
      phraseStructure_At();
    }
    //A -> '-' At => 'neg'
    else if(currentTokenCheck(NameOFToken.OPERATOR, "-")){
      nextTokenRead();
      phraseStructure_At();
      makeASTNode(NodeType.NEG, 1);
    }
    else
      phraseStructure_At();
    
    boolean plusMark = true;
    while(currentTokenCheck(NameOFToken.OPERATOR, "+")|| currentTokenCheck(NameOFToken.OPERATOR, "-")){
      if(token.getValue().equals("+"))
        plusMark = true;
      else if(token.getValue().equals("-"))
        plusMark = false;
      nextTokenRead();
      phraseStructure_At();
      // A -> A '+' At => '+'
      if(plusMark)
        makeASTNode(NodeType.PLUS, 2);
      // A -> A '-' At => '-'
      else
        makeASTNode(NodeType.MINUS, 2);
    }
  }
  
  /*
    At -> At '*' Af => '*'
       -> At '/' Af => '/'
       -> Af;
   */
  private void phraseStructure_At(){
    //At -> Af;
    phraseStructure_Af();
    boolean starMark = true;
    while(currentTokenCheck(NameOFToken.OPERATOR, "*")|| currentTokenCheck(NameOFToken.OPERATOR, "/")){
      if(token.getValue().equals("*"))
        starMark = true;
      else if(token.getValue().equals("/"))
        starMark = false;
      nextTokenRead();
      phraseStructure_Af();
      // At -> At '*' Af => '*'
      if(starMark)
        makeASTNode(NodeType.MULT, 2);
        // At -> At '/' Af => '/'
      else
        makeASTNode(NodeType.DIV, 2);
    }
  }
  
  /*
    Af -> Ap '**' Af => '**'
       -> Ap;
   */
  private void phraseStructure_Af(){
    // Af -> Ap;
    phraseStructure_Ap();
    //Af -> Ap '**' Af => '**'
    if(currentTokenCheck(NameOFToken.OPERATOR, "**")){
      nextTokenRead();
      phraseStructure_Af();
      makeASTNode(NodeType.EXP, 2);
    }
  }
  
  
  /*
    Ap -> Ap ’@’ ’<IDENTIFIER>’ R => '@'
       -> R;
   */
  private void phraseStructure_Ap(){
    //Ap -> R;
    phraseStructure_R();
    // Ap ->  Ap ’@’ ’<IDENTIFIER>’ R => '@'
    while(currentTokenCheck(NameOFToken.OPERATOR, "@")){
      nextTokenRead();
      if(!currentTokenTypeCheck(NameOFToken.IDENTIFIER))
        throw new ExceptionHandleParser("Identifier expected");
      nextTokenRead();
      phraseStructure_R();
      makeASTNode(NodeType.AT, 3);
    }
  }
  

  /*
    R -> R Rn => 'gamma'
      -> Rn;
   */
  private void phraseStructure_R(){
    // R -> Rn;
    phraseStructure_RN();
    nextTokenRead();
    //R -> R Rn => 'gamma'
    while(currentTokenTypeCheck(NameOFToken.INTEGER)|| currentTokenTypeCheck(NameOFToken.STRING)|| currentTokenTypeCheck(NameOFToken.IDENTIFIER)|| currentTokenCheck(NameOFToken.KEYWORDS, "true")||
        currentTokenCheck(NameOFToken.KEYWORDS, "false")|| currentTokenCheck(NameOFToken.KEYWORDS, "nil")|| currentTokenCheck(NameOFToken.KEYWORDS, "dummy")|| currentTokenTypeCheck(NameOFToken.L_PAREN)){
      phraseStructure_RN();
      makeASTNode(NodeType.GAMMA, 2);
      nextTokenRead();
    }
  }

  /*
    Rn -> ’<IDENTIFIER>’
       -> ’<INTEGER>’
       -> ’<STRING>’
       -> 'true'          => 'true'
       -> 'false'         => 'false'
       -> 'nil'           => 'nil'
       -> '(' E ')'
       -> 'dummy'         => 'dummy'
   */
  private void phraseStructure_RN(){
    if(currentTokenTypeCheck(NameOFToken.IDENTIFIER)|| currentTokenTypeCheck(NameOFToken.INTEGER)|| currentTokenTypeCheck(NameOFToken.STRING)){
    }
    // R -> 'nil' => 'nil'
    else if(currentTokenCheck(NameOFToken.KEYWORDS, "nil")){
      makeASTNodeTerminal(NodeType.NIL, "nil");
    }
    // R -> 'true' => 'true'
    else if(currentTokenCheck(NameOFToken.KEYWORDS, "true")){
      makeASTNodeTerminal(NodeType.TRUE, "true");
    }
    // R -> 'false' => 'false'
    else if(currentTokenCheck(NameOFToken.KEYWORDS, "false")){
      makeASTNodeTerminal(NodeType.FALSE, "false");
    }
    // R -> 'dummy' => 'dummy'
    else if(currentTokenCheck(NameOFToken.KEYWORDS, "dummy")){
      makeASTNodeTerminal(NodeType.DUMMY, "dummy");
    }
    else if(currentTokenTypeCheck(NameOFToken.L_PAREN)){
      nextTokenRead();
      phraseStructure_E();
      if(!currentTokenTypeCheck(NameOFToken.R_PAREN))
        throw new ExceptionHandleParser("')' missing");
    }
  }

  /*
    D -> Da 'within' D  => 'within'
      -> Da;
   */
  private void phraseStructure_D(){
    // D -> Da
    phraseStructure_DA();
    // D -> Da 'within' D => 'within'
    if(currentTokenCheck(NameOFToken.KEYWORDS, "within")){
      nextTokenRead();
      phraseStructure_D();
      makeASTNode(NodeType.WITHIN, 2);
    }
  }
  
  /*
    Da -> Dr ('and' Dr)+  => 'and'
       -> Dr;
   */
  private void phraseStructure_DA(){
    // Da -> Dr
    phraseStructure_Dr();
    int trees = 0;
    //Da -> Dr ( 'and' Dr )+ => 'and'
    while(currentTokenCheck(NameOFToken.KEYWORDS, "and")){
      nextTokenRead();
      phraseStructure_Dr();
      trees++;
    }
    if(trees > 0) makeASTNode(NodeType.SIMULTDEF, trees+1);
  }
  
  /*
    Dr -> 'rec' Db => 'rec'
       -> Db;
   */
  private void phraseStructure_Dr(){
    // Dr -> 'rec' Db => 'rec'
    if(currentTokenCheck(NameOFToken.KEYWORDS, "rec")){
      nextTokenRead();
      phraseStructure_Db();
      makeASTNode(NodeType.REC, 1);
    }
    //Dr -> Db
    else{
      phraseStructure_Db();
    }
  }
  
  /*
    Db -> Vl '=' E                 => '=';
       -> ’<IDENTIFIER>’ Vb+ ’=’ E => 'fcn_form'
       -> '(' D ')'
   */
  private void phraseStructure_Db(){
    if(currentTokenTypeCheck(NameOFToken.L_PAREN)){
      phraseStructure_D();
      nextTokenRead();
      if(!currentTokenTypeCheck(NameOFToken.R_PAREN))
        throw new ExceptionHandleParser("')' missing");
      nextTokenRead();
    }
    else if(currentTokenTypeCheck(NameOFToken.IDENTIFIER)){
      nextTokenRead();
      // Db -> Vl '=' E => '='
      if(currentTokenCheck(NameOFToken.OPERATOR, ",")){
        nextTokenRead();
        phraseStructure_Vl();
        if(!currentTokenCheck(NameOFToken.OPERATOR, "="))
          throw new ExceptionHandleParser("= missing");
        makeASTNode(NodeType.COMMA, 2);
        nextTokenRead();
        phraseStructure_E();
        makeASTNode(NodeType.EQUAL, 2);
      }
      else{
        // Db -> Vl '=' E => '=';
        if(currentTokenCheck(NameOFToken.OPERATOR, "=")){
          nextTokenRead();
          phraseStructure_E();
          makeASTNode(NodeType.EQUAL, 2);
        }
        // Db -> '<IDENTIFIER>' Vb+ '=' E => 'fcn_form'
        else{
          int trees = 0;

          while(currentTokenTypeCheck(NameOFToken.IDENTIFIER) || currentTokenTypeCheck(NameOFToken.L_PAREN)){
            phraseStructure_Vb();
            trees++;
          }

          if(trees==0)
            throw new ExceptionHandleParser("at least one 'Vb' expected");

          if(!currentTokenCheck(NameOFToken.OPERATOR, "="))
            throw new ExceptionHandleParser("= missing");

          nextTokenRead();
          phraseStructure_E();

          makeASTNode(NodeType.FCNFORM, trees+2);
        }
      }
    }
  }
  

  
  /*
    Vb -> ’<IDENTIFIER>’
       -> '(' Vl ')'
       -> '(' ')'           => '()'
   */
  private void phraseStructure_Vb(){
    if(currentTokenTypeCheck(NameOFToken.IDENTIFIER)){
      nextTokenRead();
    }
    else if(currentTokenTypeCheck(NameOFToken.L_PAREN)){
      nextTokenRead();
      if(currentTokenTypeCheck(NameOFToken.R_PAREN)){
        makeASTNodeTerminal(NodeType.PAREN, "");
        nextTokenRead();
      }
      else{
        phraseStructure_Vl();
        if(!currentTokenTypeCheck(NameOFToken.R_PAREN))
          throw new ExceptionHandleParser("')' missing");
        nextTokenRead();
      }
    }
  }



  // Vl -> ’<IDENTIFIER>’ list ’,’   => ','?;
  private void phraseStructure_Vl(){
    if(!currentTokenTypeCheck(NameOFToken.IDENTIFIER))
      throw new ExceptionHandleParser("Identifier expected");
    else{
      nextTokenRead();
      int trees = 0;
      while(currentTokenCheck(NameOFToken.OPERATOR, ",")){
        nextTokenRead();
        if(!currentTokenTypeCheck(NameOFToken.IDENTIFIER))
          throw new ExceptionHandleParser("Identifier expected");
        nextTokenRead();
        trees++;
      }
      if(trees > 0) {
        makeASTNode(NodeType.COMMA, trees+1);
      }
    }
  }

}

