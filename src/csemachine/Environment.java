package csemachine;

import java.util.HashMap;
import java.util.Map;

import ast.Node;

/* environment for variable binding in a programming language */
public class Environment{
  private Environment parents;
  private Map<String, Node> key_Value_Map;
  
  public Environment(){
    key_Value_Map = new HashMap<String, Node>();
  }

  //setting the parent environment of the current environment
  public void setParents(Environment parents){
    this.parents = parents;
  }

  //adding a new variable binding to the environment.
  public void mapAdding(String key, Node value){
    key_Value_Map.put(key, value);
  }

  //variable lookup operation in the environment
  public Node checkup(String key){
    Node valReturn = null;
    Map<String, Node> map = key_Value_Map;
    
    valReturn = map.get(key);
    
    if(valReturn!=null)
      return valReturn.acceptance(new BackupNode());
    
    if(parents !=null)
      return parents.checkup(key);
    else
      return null;
  }
}
