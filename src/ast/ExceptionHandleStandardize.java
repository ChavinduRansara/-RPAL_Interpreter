package ast;
/* custom exception that is specific to handling errors related to
   the standardization process of an Abstract Syntax Tree (AST).   */
public class ExceptionHandleStandardize extends RuntimeException{
  private static final long serialVersionUID = 1L;
  
  public ExceptionHandleStandardize(String display){
    super(display);
  }

}
