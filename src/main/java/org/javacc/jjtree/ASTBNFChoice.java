/* Generated By:JJTree: Do not edit this line. ASTBNFChoice.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.javacc.jjtree;

public class ASTBNFChoice extends JJTreeNode{
  public ASTBNFChoice(int id) {
    super(id);
  }

  public ASTBNFChoice(JJTreeParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JJTreeParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=25281a01d298920e299c68343504e1e4 (do not edit this line) */