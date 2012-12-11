
package qimpp;

import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Visitor;

import java.util.ArrayList;

/**
 * Analyzes class member bodies and restructures as necessary.
 *
 * @author Qimpp
 */
public class BodyAnalyzer {

  /**
   * Restructure a body.
   *
   * @param n Node.
   */
  public static void restructure(Klass.Member m) {
    final Klass.Member member = m;
    new Visitor() {

      /** 
       * Determine whether call expression is a
       * System.out print call.
       *
       * @param n CallExpression generic node.
       * @boolean whether a System.out print call.
       */
      public boolean isPrintExpression(GNode n) {
        boolean b1 = n.getString(2).equals("print") ||
            n.getString(2).equals("println");
        if (b1) {
          b1 = null != n.getGeneric(0).getString(1);
          if (b1) {
            b1 = n.getGeneric(0).getString(1).equals("out");
            if (b1) {
              b1 = n.getGeneric(0).getGeneric(0)
                .getName().equals("PrimaryIdentifier");
              if (b1) {
                b1 = n.getGeneric(0).getGeneric(0)
                  .getString(0).equals("System");
                if (b1) return true;
              }
            }
          }
        }
        return false;
      }

      /**
       * Extract selection expression
       *
       * @param n selection expression node. 
       *  It may not be SelectionExpression node.
       * @return list of selectors.
       */
      public ArrayList<String> extractSelectors(GNode n) {
        final ArrayList<String> selectors = new ArrayList<String>();
        if (null == n) { return selectors; }
        
        new Visitor() {
          public void visit(GNode n) {
            for (Object o : n) {
              if (o instanceof Node) { dispatch((Node)o); }
              else if (o instanceof String) {
                selectors.add((String)o);
              } else /* expected: null */ {
                if (n.getName().equals("ThisExpression")) {
                  selectors.add("this");  
                } else if (n.getName().equals("SuperExpression")) {
                  selectors.add("super");
                }
              }
            }
          }
        }.dispatch(n);

        return selectors;   
      }

      // =======================================================================

      /** Copy location from nOld to nNew, then return nNew. */
      public GNode copyLoc(GNode nOld, GNode nNew) {
        nNew.setLocation(nOld);
        return nNew;
      }

      // =======================================================================

      /** 
       * Visit specified call expression node. 
       *
       * The suppression is for the cast from Object to
       * ArrayList<ParameterVariable>, which should be safe since
       * visitArguments returns an ArrayList<ParameterVariable>.
       */
      @SuppressWarnings("unchecked")
      public void visitCallExpression(GNode n) {
        visit(n);
        if (isPrintExpression(n)) {
          GNode arguments = n.getGeneric(3);
          GNode printNode = GNode.create("PrintExpression", arguments);
          if (n.getString(2).equals("println")) {
            printNode = GNode.create("PrintLineExpression", arguments);
          }
          copyLoc(n, printNode);
        } else {
          GNode selectionExpression = n.getGeneric(0);
          ArrayList<String> selectors = extractSelectors(selectionExpression);

          String identifier = n.getString(2);

          ArrayList<Type> arguments = 
            (ArrayList<Type>)dispatch(n.getGeneric(3));
         
          // The magic. 
          String resolvedIdentifier =
            new Resolver(new Invocation(selectors, identifier, arguments),
                         member)
              .match().filter().choose().rename();
        
          n.set(2, resolvedIdentifier);
          System.out.println(n);
        }
      }

      /** Visit specified arguments node. */
      public ArrayList<Type> visitArguments(GNode n) {
        final ArrayList<Type> arguments = new ArrayList<Type>();
        if (0 == n.size()) { return arguments; }
        
        new Visitor() {
          // PRIMITIVES ========================================================
         
          /** Visit long literal. */
          // TODO: Parsed as IntegerLiteral.

          /** Visit integer literal. */ 
          public void visitIntegerLiteral(GNode n) {
            arguments.add(new PrimitiveType("int"));
          }
         
          /** Visit short literal. */
          // TODO

          /** Visit byte literal. */
          // TODO

          /** Visit char literal. */
          public void visitCharacterLiteral(GNode n) {
            arguments.add(new PrimitiveType("char"));
          }

          /** Visit floating point literal. */ 
          public void visitFloatingPointLiteral(GNode n) {
            arguments.add(new PrimitiveType("float"));
          }

          /** Visit double precision point literal. */
          // TODO: Parsed as FloatingPointLiteral.

          /** Visit boolean literal. */
          public void visitBooleanLiteral(GNode n) {
            arguments.add(new PrimitiveType("boolean"));
          }

          // QUALIFIED TYPES ===================================================

          /** Visit string literal. */
          public void visitStringLiteral(GNode n) {
            Klass stringType = Store.getQualifiedType("String");
            arguments.add(new Klass(stringType));
          }

          public void visit(GNode n) {
            for (Object o : n) {
              if (o instanceof Node) { dispatch((Node)o); }
            }
          }
        }.dispatch(n);
         
        return arguments;
      }

      /** Visit specified generic node. */
      public void visit(GNode n) {
        for (Object o : n) {
          if (o instanceof Node) {
            dispatch((Node)o);
          }
        }
      }
    }.dispatch(member.body());
  }
}