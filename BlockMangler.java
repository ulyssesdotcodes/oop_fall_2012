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

  public GNode root;
  
  public BlockMangler(
                GNode block, 
                HashMap<String, String> fieldMap, 
                HashMap<String, String> classNameMap,
                HashMap<String, GNode> qualifiedClassNameMap,
                MethodResolver mr
                ) {
    
    root = block;
  }

}
