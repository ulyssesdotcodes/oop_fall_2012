/*
 * xtc - The eXTensible Compiler
 * Copyright (C) 2012 Robert Grimm
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
 */
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import xtc.lang.JavaFiveParser;

import xtc.parser.ParseException;
import xtc.parser.Result;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Printer;
import xtc.tree.Visitor;

/**
 * A tool to print static scopes.
 *
 * @author Robert Grimm
 * @version $Revision$
 */
public class Grimm extends xtc.util.Tool {

  /** Create a new tool. */
  public Grimm() {
    // Nothing to do.
  }

  {
    // A class-level anonymous scope.
  }

  public interface ICommand {
    public void run();
  }

  public String getName() {
    return "Static scope printer";
  }

  public File locate(String name) throws IOException {
    File file = super.locate(name);
    if (Integer.MAX_VALUE < file.length()) {
      throw new IllegalArgumentException(file + ": file too large");
    }
    return file;
  }

  public Node parse(Reader in, File file) throws IOException, ParseException {
    JavaFiveParser parser =
      new JavaFiveParser(in, file.toString(), (int)file.length());
    Result result = parser.pCompilationUnit(0);
    return (Node)parser.value(result);
  }

  public void process(Node node) {
    new Visitor() {
      private void scope(String kind, String name, Node node) {
        runtime.console().p(kind).p(" scope ");
        if (null != name) runtime.console().p(name).p(' ');
        runtime.console().p("starting at ").loc(node).pln();
      }

      private void scope(Node node) {
        scope("Anonymous", null, node);
      }

      public void visitCompilationUnit(GNode n) {
        visit(n);
        runtime.console().flush();
      }

      public void visitClassDeclaration(GNode n) {
        scope("Class", n.getString(1), n);
        visit(n.getNode(5));
      }

      public void visitInterfaceDeclaration(GNode n) {
        scope("Interface", n.getString(1), n);
        visit(n.getNode(4));
      }

      public void visitConstructorDeclaration(GNode n) {
        scope("Constructor", n.getString(2), n);
        visit(n.getNode(5));
      }

      public void visitMethodDeclaration(GNode n) {
        scope("Method", n.getString(3), n);
        Node body = n.getNode(7);
        if (null != body) visit(body);
      }

      public void visitBlock(GNode n) {
        scope(n);
        visit(n);
      }

      public void visitForStatement(GNode n) {
        scope(n);
        visit(n.getNode(1));
      }

      public void visitNewClassExpression(GNode n) {
        Node body = n.getNode(4);
        if (null != body) {
          scope(body);
          visit(body);
        }
      }
      
      public void visit(Node n) {
        for (Object o : n) {
          // The scope belongs to the for loop!
          if (o instanceof Node) dispatch((Node)o);
        }
      }
      
    }.dispatch(node);
  }

  /**
   * Run the tool with the specified command line arguments.
   *
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    new Grimm().run(args);
  }

}
