package LexicalAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/*
  Scanner and a Screener
 */
public class Scanner{
  private String character;  // extra character
  private int numberOfLine; // source Line Number
  private BufferedReader inputBuffer;
  private final List<String> keywords = Arrays.asList(new String[]{"let","in","where","fn","within","aug","not","and","or","gr","ge","ls","le","eq","ne","true",
                                                                              "false","nil","rec","dummy"});
  public Scanner(String input) throws IOException{
    numberOfLine = 1;
    //opens the input file and sets up a BufferedReader to read the source code character by character.
    FileInputStream file = new FileInputStream(new File(input));
    inputBuffer = new BufferedReader(new InputStreamReader(file));
  }

  // Next token returns from the input file
  public Token nextTokenRead(){
    String nextCharacter;
    Token nextT = null;
    if(character !=null){
      nextCharacter = character;
      character = null;
    } else
      nextCharacter = nextCharacterRead();
    if(nextCharacter!=null)
      nextT = tokenMake(nextCharacter);
    return nextT;
  }

  //reads the next character from the input file
  private String nextCharacterRead(){
    String next = null;
    try{
      int reading = inputBuffer.read();
      if(reading!=-1){
        next = Character.toString((char)reading);
        if(next.equals("\n")) {
          numberOfLine++;
        }
      } else
          inputBuffer.close();
    }catch(IOException e){
    }
    return next;
  }

  // next token build from the input file
  private Token tokenMake(String current){
    Token nextT = null;
    if(Regex.DigitPtrn.matcher(current).matches()){
      nextT = integerTokenMake(current);
    }
    else if(Regex.LetterPtrn.matcher(current).matches()){
      nextT = identifierTokenMake(current);
    }
    else if(current.equals("\'")){
      nextT = stringTokenMake(current);
    }
    else if(Regex.OpSymbolPtrn.matcher(current).matches()){
      nextT = operatorTokenMake(current);
    }
    else if(Regex.PunctuationPtrn.matcher(current).matches()){
      nextT = punctuationPatternMake(current);
    }
    else if(Regex.SpacePtrn.matcher(current).matches()){
      nextT = spaceTokenMake(current);
    }
    return nextT;
  }

  // Build integer token
  private Token integerTokenMake(String current){
    Token token = new Token();
    token.setNumberOFLine(numberOfLine);
    token.setNameOFToken(NameOFToken.INTEGER);
    StringBuilder stringBuilder = new StringBuilder(current);
    String nextCharacter = nextCharacterRead();
    while(nextCharacter!=null){
      if(Regex.DigitPtrn.matcher(nextCharacter).matches()){
        stringBuilder.append(nextCharacter);
        nextCharacter = nextCharacterRead();
      }
      else{
        character = nextCharacter;
        break;
      }
    }

    token.setValue(stringBuilder.toString());
    return token;
  }

  // Builds string token.
  private Token stringTokenMake(String current){
    Token token = new Token();
    token.setNameOFToken(NameOFToken.STRING);
    token.setNumberOFLine(numberOfLine);
    StringBuilder stringBuilder = new StringBuilder("");
    String nextCharacter = nextCharacterRead();
    while(nextCharacter!=null){
      if(nextCharacter.equals("\'")){
        token.setValue(stringBuilder.toString());
        return token;
      }
      else if(Regex.StringPtrn.matcher(nextCharacter).matches()){
        stringBuilder.append(nextCharacter);
        nextCharacter = nextCharacterRead();
      }
    }

    return null;
  }

  // Builds Identifier token.
  private Token identifierTokenMake(String current){
    Token token = new Token();
    token.setNameOFToken(NameOFToken.IDENTIFIER);
    token.setNumberOFLine(numberOfLine);
    StringBuilder stringBuilder = new StringBuilder(current);
    String nextCharacter = nextCharacterRead();
    while(nextCharacter!=null){
      if(Regex.IdentifierPtrn.matcher(nextCharacter).matches()){
        stringBuilder.append(nextCharacter);
        nextCharacter = nextCharacterRead();
      }
      else{
        character = nextCharacter;
        break;
      }
    }
    
    String value = stringBuilder.toString();
    if(keywords.contains(value))
      token.setNameOFToken(NameOFToken.KEYWORDS);
    
    token.setValue(value);
    return token;
  }


  // Builds operator token.
  private Token operatorTokenMake(String current){
    Token token = new Token();
    token.setNameOFToken(NameOFToken.OPERATOR);
    token.setNumberOFLine(numberOfLine);
    StringBuilder stringBuilder = new StringBuilder(current);
    String nextCharacter = nextCharacterRead();
    if(current.equals("/") && nextCharacter.equals("/"))
      return commentTokenMake(current+nextCharacter);
    
    while(nextCharacter!=null){
      if(Regex.OpSymbolPtrn.matcher(nextCharacter).matches()){
        stringBuilder.append(nextCharacter);
        nextCharacter = nextCharacterRead();
      }
      else{
        character = nextCharacter;
        break;
      }
    }
    
    token.setValue(stringBuilder.toString());
    return token;
  }

  // Builds space token.
  private Token spaceTokenMake(String current){
    Token token = new Token();
    token.setNameOFToken(NameOFToken.DELETE);
    token.setNumberOFLine(numberOfLine);
    StringBuilder stringBuilder = new StringBuilder(current);
    String nextCharacter = nextCharacterRead();
    while(nextCharacter!=null){ //null indicates the file ended
      if(Regex.SpacePtrn.matcher(nextCharacter).matches()){
        stringBuilder.append(nextCharacter);
        nextCharacter = nextCharacterRead();
      }
      else{
        character = nextCharacter;
        break;
      }
    }
    
    token.setValue(stringBuilder.toString());
    return token;
  }

  // Builds punctuation token.
  private Token punctuationPatternMake(String current){
    Token token = new Token();
    token.setNumberOFLine(numberOfLine);
    token.setValue(current);
    if(current.equals("("))
      token.setNameOFToken(NameOFToken.L_PAREN);
    else if(current.equals(")"))
      token.setNameOFToken(NameOFToken.R_PAREN);
    else if(current.equals(";"))
      token.setNameOFToken(NameOFToken.SEMICOLON);
    else if(current.equals(","))
      token.setNameOFToken(NameOFToken.COMMA);

    return token;
  }

  // Builds comment token.
  private Token commentTokenMake(String current){
    Token token = new Token();
    token.setNameOFToken(NameOFToken.DELETE);
    token.setNumberOFLine(numberOfLine);
    StringBuilder stringBuilder = new StringBuilder(current);
    String nextCharacter = nextCharacterRead();
    while(nextCharacter!=null){
      if(Regex.CommentPtrn.matcher(nextCharacter).matches()){
        stringBuilder.append(nextCharacter);
        nextCharacter = nextCharacterRead();
      }
      else if(nextCharacter.equals("\n"))
        break;
    }
    
    token.setValue(stringBuilder.toString());
    return token;
  }

}

