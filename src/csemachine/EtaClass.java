package csemachine;

import ast.Node;
import ast.NodeType;

/* represents an eta closure */

public class EtaClass extends Node {
  private DeltaClass delta;
  public EtaClass(){
    setType(NodeType.ETA);
  }

  @Override
  public String getValue(){
    return "[eta closure: "+delta.getVars().get(0)+": "+delta.getIndexNo()+"]";
  }
  public EtaClass acceptance(BackupNode copier){
    return copier.nodeCopy(this);
  }
  public void setDelta(DeltaClass delta){
    this.delta = delta;
  }
  public DeltaClass getDelta(){
    return delta;
  }
}
