package qimpp;

import java.util.*
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.Printer;

import xtc.util.Tool;

/**
 * Mangles Block Nodes into better Block nodes for ImplementationPrinter
 *
 * @author QIMPP
 */

public class BlockMangler {

  public GNode java;
  
  public BlockMangler(
                GNode block, 
                HashMap<String, GNode> fieldMap,
                InhertanceTreeManager itm,
                MethodResolver mr
                ) {
    
    java = block;
  }
 
  // replaces nOld GNode with nNew GNode
  public void copyloc(GNode nOld, GNode nNew) {
    nNew.setLocation(nOld);
    return nNew;
  }

  // takes java block 
  public GNode mangle(GNode java) {
    
    GNode cpp = new GNode("Block");

    new Visitor() {

      
      public void visitCallExpression(GNode n) {
        if (n.getGeneric(0).getGeneric(0).getString(0).equals("System")
                        && n.getGeneric(0).getString(1).equals("out")) {
          String option = (n.getString(2).equals("println")) ? " << endl" : null;  
          GNode printBody = dispatch(n.getGeneric(3));
        }

        cpp.add(GNode.create("PrintExpression", option, printBody));  
      }

      public void visitArguments(GNode n) {
        GNode body = GNode.create("PrintBody");
        visit(n);
        for (Object o : n) {
          if (o instance of Node) {
            body.add((GNode)o);
          } 
          else { 
            body.add((String)o); 
          }
        }
        cpp.add(body);
      }

      public void visitAdditiveExpression(GNode n) {
        visit(n);
        GNode expr;
        left = dispatch(n.getGeneric(0));
        right = dispatch(n.getGeneric(2));
        if (getType(left) == getType(right)) {
          expr = GNode.create("AdditiveExpression", left, n.getString(1), right);
        }
        else {
          expr = GNode.create("ConcatExpression", left, "<<", right);
        }
        cpp.add(expr);
      }


      public void visit(Node n) {
        for (Object o : n) if (o instanceOf Node) dispatch((Node)o);
      } 

    }.dispatch(java);
    
  }

  public GNode getType(GNode n) {
    declaration = n.getProperty(Constants.SCOPE).node();
    
  }

}
