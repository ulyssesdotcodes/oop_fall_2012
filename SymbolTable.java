/*
 * xtc - The eXTensible Compiler
 * Copyright (C) 2005-2010 Robert Grimm
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
 */
package qimpp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import qimpp.Constants;
import qimpp.Utilities;
import qimpp.Type;
import qimpp.PrimitiveType;
import qimpp.QualifiedType;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Printer;
import xtc.tree.Visitor;
import xtc.util.EmptyIterator;

/**
 * A symbol table.  This class implements a symbol table, which maps
 * symbols represented as strings to values of any type.  The mapping
 * is organized into hierarchical {@link Scope scopes}, which allows
 * for multiple definitions of the same symbol across different
 * scopes.  Additionally, a symbol may have multiple definitions
 * within the same scope: if the corresponding value is a Java
 * collections framework list, it is recognized as a multiply defined
 * symbol.  Scopes are named, with names being represented as strings.
 * Both scope names and symbols can be unqualified &mdash; that is,
 * they need to be resolved relative to the {@link #current() current
 * scope} &mdash; or qualified by the {@link Constants#QUALIFIER
 * qualification character} "<code>::</code>" &mdash; that is, they are
 * resolved relative to the symbol table's {@link #root() root}.  Once
 * {@link #enter(String) created}, a scope remains in the symbol table
 * and the corresponding AST node should be associated with that scope
 * by setting the corresponding {@link Constants#SCOPE property} to
 * the scope's qualified name.  Subsequent traversals over that node
 * can then automatically {@link #enter(Node) enter} and {@link
 * #exit(Node) exit} that scope.  Alternatively, if traversing out of
 * tree order, the current scope can be set {@link
 * #setScope(SymbolTable.Scope) explicitly}.
 *
 * <p />To support different name spaces within the same scope, this
 * class can optionally {@link #toNameSpace mangle} and {@link
 * #fromNameSpace unmangle} unqualified symbols.  By convention, a
 * name in any name space besides the default name space is prefixed
 * by the name of the name space and an opening parenthesis
 * '<code>(</code>' and suffixed by a closing parenthesis
 * '<code>)</code>'.
 *
 * @author Robert Grimm, adapted by Qimpp
 * @version N/A
 */
public class SymbolTable {

  /**
   * A symbol table scope.  A scope has a name and may have a parent
   * (unless it is the root scope), one or more nested scopes, and one
   * or more definitions.
   */
  public static class Scope {

    /** The name. */
    String name;

    /** The fully qualified name. */
    String qName;

    /** The parent scope. */
    Scope parent;

    /** The nested scopes, if any. */
    Map<String, Scope> scopes;

    /**
     * Create a new root scope with the specified name, which may be
     * the empty string.
     *
     * @param name The name.
     */
    Scope(String name) {
      this.name  = name;
      this.qName = name;
    }

    /**
     * Create a new nested scope with the specified unqualified name
     * and parent.
     *
     * @param name The unqualified name.
     * @param parent The parent.
     * @throws IllegalArgumentException
     *   Signals that the specified parent already has a nested scope
     *   with the specified name.
     */
    Scope(String name, Scope parent) {
      if ((null != parent.scopes) && parent.scopes.containsKey(name)) {
        throw new IllegalArgumentException("Scope " + parent.qName +
                                           " already contains scope " + name);
      }
      this.name   = name;
      this.qName  = Utilities.qualify(parent.qName, name);
      this.parent = parent;
      if (null == parent.scopes) {
        parent.scopes = new HashMap<String, Scope>();
      }
      parent.scopes.put(name, this);
    }

    /**
     * Get this scope's unqualfied name.
     *
     * @return This scope's unqualified name.
     */
    public String getName() {
      return name;
    }

    /**
     * Get this scope's qualified name.
     *
     * @return This scope's qualified name.
     */
    public String getQualifiedName() {
      return qName;
    }

    /**
     * Qualify the specified unqualified symbol with this scope's
     * name.
     *
     * @param symbol The unqualified symbol.
     * @return The qualified symbol.
     */
    public String qualify(String symbol) {
      return Utilities.qualify(qName, symbol);
    }

    /**
     * Update this scope's qualified name relative to the parent
     * scope's qualified name.  This method also requalifies any
     * nested scopes' qualified names.  It must not be called on the
     * root scope.
     */
    void requalify() {
      qName = Utilities.qualify(parent.qName, name);

      if (null != scopes) {
        for (Scope scope : scopes.values()) {
          scope.requalify();
        }
      }
    }

    /**
     * Determine whether this scope is the root scope.
     *
     * @return <code>true</code> if this scope is the root scope.
     */
    public boolean isRoot() {
      return (null == parent);
    }

    /**
     * Get this scope's parent.
     *
     * @return This scope's parent scope or <code>null</code> if this
     *   scope does not have a parent (i.e., is the root scope).
     */
    public Scope getParent() {
      return parent;
    }

    /**
     * Determine whether this scope has any nested scopes.
     *
     * @return <code>true</code> if this scope has any nested scopes.
     */
    public boolean hasNested() {
      return ((null != scopes) && (0 < scopes.size()));
    }

    /**
     * Get an iterator over the names of all nested scopes.
     *
     * @return An iterator over the nested scopes.
     */
    public Iterator<String> nested() {
      if (null == scopes) {
        return EmptyIterator.value();
      } else {
        return scopes.keySet().iterator();
      }
    }

    /**
     * Determine whether this scope has the specified unqualified
     * nested scope.
     *
     * @param name The nested scope's unqualified name.
     * @return <code>true</code> if the corresponding scope exists.
     */
    public boolean hasNested(String name) {
      return (null != getNested(name));
    }

    /**
     * Get the nested scope with the specified unqualified name.
     *
     * @param name The nested scope's unqualified name.
     * @return The corresponding scope or <code>null</code> if there is
     *   no such scope.
     */
    public Scope getNested(String name) {
      return (null == scopes)? null : scopes.get(name);
    }

    /**
     * Get the scope defining the specified unqualified symbol.  This
     * method searches this scope and all its ancestors, returning the
     * first defining scope.
     *
     * @param symbol The unqualified symbol.
     * @return The definining scope or <code>null</code> if there is
     *   no such scope.
     */
    public Scope lookupScope(String symbol) {
      Scope scope = this;
      do {
        if ((null != scope.scopes) && (scope.scopes.containsKey(symbol))) {
          return scope;
        }
        scope = scope.parent;
      } while (null != scope);
      return null;
    }

    /**
     * Dump the contents of this scope.  This method pretty prints the
     * contents of this scope and all nested scopes with the specified
     * printer. 
     *
     * TODO: Perhaps print a bit nicer?
     *
     * @param printer The printer, which need not be registered with a
     *   visitor.
     */
    public void dump(Printer printer) {
      printer.indent().p(Constants.QUALIFIER).pln(name).incr();
        
      if (null != scopes) {
        Iterator iterator = scopes.entrySet().iterator();
        while (iterator.hasNext()) {
          Map.Entry entry = (Map.Entry)iterator.next();
          ((Scope)entry.getValue()).dump(printer);
        }
      }
      
      printer.decr().indent().pln();
    }
  }

  // =========================================================================

  /** The root scope. */
  protected Scope root;

  /** The current scope. */
  protected Scope current;

  /** The fresh name count. */
  protected int freshNameCount;

  /** The fresh identifier count. */
  protected int freshIdCount;

  // =========================================================================

  /**
   * Create a new symbol table with the global namespace resolution operator
   * as the root.
   */
  public SymbolTable() {
    this("");
  }

  /**
   * Create a new symbol table.
   *
   * @param root The name of the root scope.
   */
  public SymbolTable(String root) {
    this.root      = new Scope(root);
    current        = this.root;
    freshNameCount = 0;
    freshIdCount   = 0;
  }

  // =========================================================================

  /**
   * Clear this symbol table.  This method deletes all scopes and
   * their definitions from this symbol table.
   */
  public void reset() {
    root.scopes    = null;
    current        = root;
    freshNameCount = 0;
    freshIdCount   = 0;
  }

  /**
   * Get the root scope.
   *
   * @return The root scope.
   */
  public Scope root() {
    return root;
  }

  /**
   * Get the current scope.
   *
   * @return The current scope.
   */
  public Scope current() {
    return current;
  }

  /**
   * Get the scope with the specified qualified name.
   *
   * @param name The qualified name.
   * @return The corresponding scope or <code>null</code> if no such
   *   scope exits.
   */
  public Scope getScope(String name) {
    // Optimize for the common case where the specified name denotes a
    // scope directly nested in the current scope.
    Scope scope = current;

    if (name.startsWith(scope.qName) && 
        (name.lastIndexOf(Constants.QUALIFIER) == scope.qName.length())) {
      return scope.getNested(Utilities.getName(name));
    }

    String[] components = Utilities.toComponents(name);
    scope               = root.name.equals(components[0])? root : null;
    int      index      = 1;

    while ((null != scope) && (index < components.length)) {
      scope = scope.getNested(components[index]);
      index++;
    }

    return scope;
  }

  /**
   * Set the current scope to the specified scope.
   *
   * @param scope The new current scope.
   * @throws IllegalArgumentException Signals that this symbol table's
   *   root is not the specified scope's root.
   */
  public void setScope(Scope scope) {
    // Check the specified scope.
    Scope s = scope;
    while (null != s.parent) s = s.parent;
    if (s != root) {
      throw new IllegalArgumentException("Scope " + scope.qName + " not " +
                                         "in this symbol table " + this);
    }

    // Make the scope the current scope.
    current = scope;
  }

  /**
   * Get the scope for the specified symbol.  If the symbol is
   * qualified, this method returns the named scope (without checking
   * whether the symbol is defined in that scope).  Otherwise, it
   * searches the current scope and all its ancestors, returning the
   * first defining scope.
   *
   * @param symbol The symbol.
   * @return The corresponding scope or <code>null</code> if no such
   *   scope exits.
   */
  public Scope lookupScope(String symbol) {
    if (Utilities.isQualified(symbol)) {
      return getScope(Utilities.getQualifier(symbol));
    } else {
      return current.lookupScope(symbol);
    }
  }


  /**
   * Enter the scope with the specified unqualified name.  If the
   * current scope does not have a scope with the specified name, a
   * new scope with the specified name is created.  In either case,
   * the scope with that name becomes the current scope.
   *
   * @param name The unqualified name.
   */
  public void enter(String name) {
    Scope parent = current;
    Scope child  = parent.getNested(name);
    if (null == child) {
      child      = new Scope(name, parent);
    }
    current = child;
  }

  /**
   * Exit the current scope.
   *
   * @throws IllegalStateException
   *   Signals that the current scope is the root scope.
   */
  public void exit() {
    if (null == current.parent) {
      throw new IllegalStateException("Unable to exit root scope");
    }
    current = current.parent;
  }

  /**
   * Delete the scope with the specified unqualified name.  If the
   * current scope contains a nested scope with the specified name,
   * this method deletes that scope and <em>all its contents</em>,
   * including nested scopes.
   *
   * @param name The unqualified name.
   */
  public void delete(String name) {
    if (null != current.scopes) {
      current.scopes.remove(name);
    }
  }

  /**
   * Determine whether the specified node has an associated {@link
   * Constants#SCOPE scope}.
   *
   * @param n The node.
   * @return <code>true</code> if the node has an associated scope.
   */
  public static boolean hasScope(Node n) {
    return n.hasProperty(Constants.SCOPE);
  }
  
  /**
   * Mark the specified node.  If the node does not have an associated
   * {@link Constants#SCOPE scope}, this method set the property with
   * the current scope.
   *
   * @param n The node.
   */
  public void mark(Node n) {
    if (! n.hasProperty(Constants.SCOPE)) {
      n.setProperty(Constants.SCOPE, current);
    }
  }

  /**
   * Enter the specified node.  If the node has an associated {@link
   * Constants#SCOPE scope}, this method tries to enter the scope.
   * Otherwise, it does not change the scope.
   *
   * @param n The node.
   * @throws IllegalStateException Signals that the node's scope is
   *   invalid or not nested within the current scope.
   */
  public void enter(Node n) {
    if (n.hasProperty(Constants.SCOPE)) {
      String name  = n.getStringProperty(Constants.SCOPE);
      Scope  scope = getScope(name);

      if (null == scope) {
        throw new IllegalStateException("Invalid scope " + name);
      } else if (scope.getParent() != current) {
        throw new IllegalStateException("Scope " + name + " not nested in " +
                                        current.getQualifiedName());
      }

      current = scope;
    }
  }

  /**
   * Exit the specified node.  If the node has an associated {@link
   * Constants#SCOPE scope}, the current scope is exited.
   *
   * @param n The node.
   */
  public void exit(Node n) {
    if (n.hasProperty(Constants.SCOPE)) {
      exit();
    }
  }

  /**
   * Create a fresh name.  The returned name has
   * "<code>anonymous</code>" as it base name.
   *
   * @see #freshName(String)
   * 
   * @return A fresh name.
   */
  public String freshName() {
    return freshName("anonymous");
  }

  /**
   * Create a fresh name incorporating the specified base name.  The
   * returned name is of the form
   * <code><i>name</i>(<i>count</i>)</code>.
   *
   * @param base The base name.
   * @return The corresponding fresh name.
   */
  public String freshName(String base) {
    StringBuilder buf = new StringBuilder();
    buf.append(base);
    buf.append(Constants.START_OPAQUE);
    buf.append(freshNameCount++);
    buf.append(Constants.END_OPAQUE);
    return buf.toString();
  }

  /**
   * Create a fresh C identifier.  The returned identifier has
   * "<code>tmp</code>" as its base name.
   *
   * @see #freshCId(String)
   *
   * @return A fresh C identifier.
   */
  public String freshCId() {
    return freshCId("tmp");
  }

  /**
   * Create a fresh C identifier incorporating the specified base
   * name.  The returned name is of the form
   * <code>__<i>name</i>_<i>count</i></code>.
   *
   * @param base The base name.
   * @return The corresponding fresh C identifier.
   */
  public String freshCId(String base) {
    StringBuilder buf = new StringBuilder();
    buf.append("__");
    buf.append(base);
    buf.append('_');
    buf.append(freshIdCount++);
    return buf.toString();
  }

  /** The end of opaqueness marker as a string. */
  private static final String END_OPAQUE =
    Character.toString(Constants.END_OPAQUE);

  // ===================================================================

  /**
   * Incorporate a Node tree into the instance. This method looks for
   * identifiers, user-defined names contained in QualifiedIdentifier,
   * PrimaryIdentifier, etc.
   *
   * @param node a tree to incorporate into the SymbolTable
   */
  public void incorporate(Node node) {
    final SymbolTable table = this;
    
    new Visitor() {

      // root of static scope tree
      public void visitCompilationUnit(GNode n) {
        visit(n);
      }

      public void visitClassDeclaration(GNode n) {
        table.enter(n.getString(1));
        table.mark(n);
        visit(n.getNode(5));
        table.exit();
      }

      public void visitInterfaceDeclaration(GNode n) {
        table.enter(table.freshCId(n.getName()));
        table.mark(n);
        visit(n.getNode(4));
        table.exit();
      }

      public void visitConstructorDeclaration(GNode n) {
        table.enter(table.freshCId("constructor"));
        table.mark(n);
        visit(n.getNode(5)); // block
        visit(n.getNode(3)); // parameters
        table.exit();
      }

      public void visitMethodDeclaration(GNode n) {
        Node parameters = n.getNode(4);
        if (parameters.size() > 0) {
          table.enter(n.getString(3));
          table.mark(n);
          visit(parameters);
          table.exit();
        }

        Node body = n.getNode(7);
        if (null != body) {
          table.enter(n.getString(3));
          table.mark(n);
          visit(body);
          table.exit();
        }
      }

      public void visitBlock(GNode n) {
        table.enter(table.freshCId());
        table.mark(n);
        visit(n);
        table.exit();
      }

      public void visitForStatement(GNode n) {
        // if any declarations. TODO: Handle multiple declarations
        Node declarators = n.getNode(1);
        if (null != declarators) {
          table.enter(table.freshCId("for"));
          table.mark(n);
          visit(n);
          table.exit();
        }
      }

      public void visitNewClassExpression(GNode n) {
        Node body = n.getNode(4);
        if (null != body) {
          table.enter(table.freshCId(n.getName()));
          table.mark(n);
          visit(body);
          table.exit();
        }
      }

      // ======================================================================
      
      /**
       * Visits Declarator and FormalParameter nodes and marks them with the 
       * appropriate scope.
       *
       * Adding the definition is just for determining the context of nested
       * scopes.
       *
       */
      public void visitDeclarator(GNode n) {
        table.enter(n.getString(0));
        table.mark(n);
        table.exit();
      }

      public void visitFormalParameter(GNode n) {
        table.enter(n.getString(3));
        table.mark(n);
        table.exit();
      }

      // ======================================================================

      /**
       * Visits identifier nodes and marks them with the appropriate
       * scope. It requires looking up if scope for the identifier has been 
       * created already. If so, do not call mark.
       *
       * Note that the QualifiedIdentifier may be contain several symbols, 
       * as in a package name.
       */

      public void visitQualifiedIdentifier(GNode n) {
        Scope currentScope = table.current();
        Scope context = table.current().lookupScope(n.getString(0));
        if (null != context) {
          table.setScope(context);
          table.mark(n); // mark scope
          table.setScope(currentScope);
        }
      }

      public void visitPrimaryIdentifier(GNode n) {
        Scope currentScope = table.current();
        Scope context = table.current().lookupScope(n.getString(0));
        if (null != context) {
          table.setScope(context);
          table.mark(n);
          table.setScope(currentScope);
        }
      }

      // I haven't seen this called yet, actually.
      public void visitIdentifier(GNode n) {
        table.mark(n);
      }

      // ======================================================================

      public void visit(Node n) {
        for (Object o : n) {
          if (o instanceof Node) {
              dispatch((Node)o);
          }
        }
      }
    }.dispatch(node);

    if (Constants.DEBUG) {
      Printer printer = new Printer(System.out);
      table.root().dump(printer);
      printer.flush();
    }
  }
}
