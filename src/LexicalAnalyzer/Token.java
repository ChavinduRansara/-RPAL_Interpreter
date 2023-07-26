package LexicalAnalyzer;

/* represents individual tokens generated by the lexical analyzer during the scanning process. */

public class Token{
  private NameOFToken nameOFToken;
  private String value;
  private int numberOFLine;

  // set the name of the token
  public void setNameOFToken(NameOFToken nameOFToken){
    this.nameOFToken = nameOFToken;
  }

  // get the name of the token
  public NameOFToken getNameOFToken(){
    return nameOFToken;
  }


  public void setValue(String value){
    this.value = value;
  }
  public String getValue(){
    return value;
  }

  // set line number of the source code
  public void setNumberOFLine(int numberOFLine){
    this.numberOFLine = numberOFLine;
  }

  // get line number of the source code
  public int getNumberOFLine(){
    return numberOFLine;
  }
}