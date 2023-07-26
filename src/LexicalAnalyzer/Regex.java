package LexicalAnalyzer;

import java.util.regex.Pattern;

/* defining and managing regular expressions (regex) used by the lexical analyzer to match different patterns in the source code */

public class Regex {

  //escape special characters in a given input string
  private static String escapeCharacter(String input, String escapeChar){
    return input.replaceAll(escapeChar,"\\\\\\\\$1");
  }
  private static final String digits = "\\d";
  private static final String letter = "a-zA-Z";
  private static final String punctuation = "();,";
  private static final String space = "[\\s\\t\\n]";
  private static final String operationalSymbols = "+-/~:=|!#%_{}\"*<>.&$^\\[\\]?@";
  private static final String opSymbolToEscape = "([*<>.&$^?])";
  public static final Pattern DigitPtrn = Pattern.compile(digits);
  public static final Pattern IdentifierPtrn = Pattern.compile("["+ letter + digits +"_]");
  public static final Pattern LetterPtrn = Pattern.compile("["+ letter +"]");
  public static final String opSymbolRegex = "[" + escapeCharacter(operationalSymbols, opSymbolToEscape) + "]";
  public static final Pattern OpSymbolPtrn = Pattern.compile(opSymbolRegex);
  public static final Pattern PunctuationPtrn = Pattern.compile("["+ punctuation +"]");
  public static final Pattern SpacePtrn = Pattern.compile(space);
  public static final Pattern CommentPtrn = Pattern.compile("[ \\t\\'\\\\ \\r"+ punctuation + letter + digits + escapeCharacter(operationalSymbols, opSymbolToEscape)+"]");
  public static final Pattern StringPtrn = Pattern.compile("[ \\t\\n\\\\"+ punctuation + letter + digits + escapeCharacter(operationalSymbols, opSymbolToEscape) +"]");


}