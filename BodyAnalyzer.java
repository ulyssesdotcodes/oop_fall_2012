
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

  /** Current method. */
  Klass.Method currentMethod;

  /** Current field. */
  Klass.Field currentField;

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
      public void visitCallExpression(GNode n) throws Exception {
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

          ArrayList<ParameterVariable> arguments = 
            (ArrayList<ParameterVariable>)dispatch(n.getGeneric(3));
         
          // The magic. 
          String resolvedIdentifier =
            new Resolver(new Invocation(selectors, identifier, arguments),
                         member)
              .match().filter().choose().rename();
         
          n.set(2, resolvedIdentifier); 
        }
      }

      /** Visit specified arguments node. */
      public ArrayList<ParameterVariable> visitArguments(GNode n) {
        // TODO
        return null;
      }

      /** Visit specified generic node. */
      public void visit(GNode n) {
        for (Object o : n) {
          if (o instanceof Node) dispatch((Node)o);
        }
      }
    }.dispatch(member.body());
  }
}
