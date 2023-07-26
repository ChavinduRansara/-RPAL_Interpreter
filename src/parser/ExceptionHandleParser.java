package parser;

/* handle exceptions related to the parsing phase of the RPAL language. */

public class ExceptionHandleParser extends RuntimeException{
  private static final long serialVersionUID = 1L;
  
  public ExceptionHandleParser(String display){
    super(display);
  }

}
