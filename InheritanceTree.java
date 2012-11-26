package qimpp;

import xtc.tree.Node;

/**
 * Maintains a class relationship tree. This aims to aid in 
 * determining order for importing and integration.
 */
public class InheritanceTree {

  /** Inheritance tree. */
  Node root;
  Node parent;

	public InheritanceTree() {
    // Nothing to do.
	}

  public InheritanceTree(Node n) {
    this.root = GNode.create("::");
  }


  public void add(Node n) {
    
  }




}




GNode root = GNode.create("::");


visitClassDeclaration(GNode n) {
  root.addNode(n.getString(1));
  Node extension = n.getNode(4);
  if (null != extension) {
    // 1. set parent to this extension, and
    // 2. set this extension's parent to the root.
  }
}
