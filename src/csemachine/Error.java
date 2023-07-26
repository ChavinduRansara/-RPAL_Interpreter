package csemachine;

/* printing errors during the execution of the program.  */

public class Error {
  
  public static void printError(int numberOfLine, String string){
    System.out.println(":"+numberOfLine+": "+string);
    System.exit(1);
  }

}
