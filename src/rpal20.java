import ast.AST;
import csemachine.*;
import LexicalAnalyzer.*;
import parser.*;

import java.io.IOException;

public class rpal20 {
    public static void main(String[] args) {
        String fileName = args[0];
        AST ast = null;
        ast = createAST(fileName, true);
        ast.standardize();
        runST(ast);
    
    }
    private static AST createAST(String fileName, boolean output){

        AST ast = null;

        try{
          Scanner scanner = new Scanner(fileName);
          Parser parser = new Parser(scanner);
          ast = parser.makeAST();
        }catch(IOException e){
          throw new ExceptionHandleParser("File can't be read: " + fileName);
        }
        return ast;
      }
    
      private static void runST(AST ast){

        CSEMachine cseMachine = new CSEMachine(ast);
        cseMachine.evaluate();
        System.out.println();
      }

    
}
