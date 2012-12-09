package qimpp;

import java.util.*;
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

  public GNode cppClass;
  public InheritanceTreeManager inheritanceTree;
  public MethodResolver methodResolver;
  
  public BlockMangler(
                GNode cppClass, 
                InheritanceTreeManager itm,
                MethodResolver mr
                ) {
    
    this.cppClass = cppClass;
    this.methodResolver = mr;
    this.inheritanceTree = itm;
  }
 
  // replaces nOld GNode with nNew GNode
  public void copyloc(GNode nOld, GNode nNew) {
    nNew.setLocation(nOld);
    //return nNew;
  }

  // takes java block 
  public GNode mangle(GNode java) {
    
    GNode cpp = GNode.create("Block");

    new Visitor() {

      /** Determine if this is a class, a stackvar, field or the start of a fully qualified class name
       *  and set the proper properties of the node */
      public String visitPrimaryIdentifier(GNode n){
        String identifier = n.getString(0);

        if (selectionExpressionBuilder != null){
          selectionExpressionBuilder.insert(0, identifier);
        }

        GNode classDeclaration = inheritanceTree.getClassDeclarationNode(identifier);
        GNode stackVar = resolveScopes(n);
        GNode classField = resolveClassField(identifier);

        if (classDeclaration != null){
          n.setProperty(Constants.IDENTIFIER_TYPE, Constants.CLASS_IDENTIFIER);
          n.setProperty(Constants.IDENTIFIER_DECLARATION, classDeclaration);
          n.setProperty(Constants.IDENTIFIER_TYPE_NODE, GNode.create("Type", Disambiguator.disambiguate(classDeclaration.getString(0))));
          return Constants.CLASS_IDENTIFIER;
        }

        else if (stackVar != null){
          n.setProperty(Constants.IDENTIFIER_TYPE, Constants.STACKVAR_IDENTIFIER);
          n.setProperty(Constants.IDENTIFIER_DECLARATION, stackVar);
          n.setProperty(Constants.IDENTIFIER_TYPE_NODE, stackVar.getGeneric(1));
          return Constants.STACKVAR_IDENTIFIER; 
        }

        
        else if (classField != null){
          n.setProperty(Constants.IDENTIFIER_TYPE, Constants.FIELD_IDENTIFIER);
          n.setProperty(Constants.IDENTIFIER_DECLARATION, classField);
          n.setProperty(Constants.IDENTIFIER_TYPE_NODE, classField.getGeneric(1));
          return Constants.FIELD_IDENTIFIER;
        }

        // It must be a fully qualified class
        else {
          n.setProperty(Constants.IDENTIFIER_TYPE, Constants.QUALIFIED_CLASS_IDENTIFIER);
          n.setProperty(Constants.IDENTIFIER_DECLARATION, null);
          n.setProperty(Constants.IDENTIFIER_TYPE_NODE, null);
          return Constants.QUALIFIED_CLASS_IDENTIFIER;

        }

      }

      public String visitIntegerLiteral(GNode n){
        n.setProperty(Constants.IDENTIFIER_TYPE, Constants.PRIMITIVE_TYPE_IDENTIFIER);
        //TODO: Handle longs
        n.setProperty(Constants.IDENTIFIER_TYPE_NODE, GNode.create("Type", GNode.create("PrimitiveIdentifier", "int")));
        return Constants.PRIMITIVE_TYPE_IDENTIFIER;
      }
      
      public String visitFloatingPointLiteral(GNode n){
        n.setProperty(Constants.IDENTIFIER_TYPE, Constants.PRIMITIVE_TYPE_IDENTIFIER);
        //TODO: Handle float
        n.setProperty(Constants.IDENTIFIER_TYPE_NODE, GNode.create("Type", GNode.create("PrimitiveIdentifier", "double")));
        return Constants.PRIMITIVE_TYPE_IDENTIFIER;
      }

      public String visitMultiplicativeExpression(GNode n){
        // A multiplicative expression always returns a primitive type
        n.setProperty(Constants.IDENTIFIER_TYPE, Constants.PRIMITIVE_TYPE_IDENTIFIER);
         
        dispatch(n.getGeneric(0));
        dispatch(n.getGeneric(2));
        
        String leftType = ((GNode)n.getGeneric(0).getProperty(Constants.IDENTIFIER_TYPE_NODE)).getGeneric(0).getString(0);

        String rightType = ((GNode)n.getGeneric(2).getProperty(Constants.IDENTIFIER_TYPE_NODE)).getGeneric(0).getString(0);

        String resultType = Type.compare(leftType, rightType);
        n.setProperty(Constants.IDENTIFIER_TYPE_NODE, GNode.create("Type", GNode.create("PrimitiveIdentifier", resultType)));

        return Constants.IDENTIFIER_TYPE;
      }

      public String visitAdditiveExpression(GNode n){

        if (n.get(0) instanceof String || n.get(2) instanceof String){
          n.setProperty(Constants.IDENTIFIER_TYPE, Constants.CLASS_IDENTIFIER);
          n.setProperty(Constants.IDENTIFIER_DECLARATION, inheritanceTree.getClassDeclarationNode("java.lang.String"));
          n.setProperty(Constants.IDENTIFIER_TYPE_NODE, GNode.create("Type", Disambiguator.disambiguate("java.lang.String")));
          return Constants.CLASS_IDENTIFIER;
        }
  
        dispatch(n.getGeneric(0));
        dispatch(n.getGeneric(2));

        GNode leftTypeNode = (GNode) n.getGeneric(0).getProperty(Constants.IDENTIFIER_TYPE_NODE);
        GNode rightTypeNode = (GNode) n.getGeneric(2).getProperty(Constants.IDENTIFIER_TYPE_NODE);

        if (leftTypeNode.getGeneric(0).getString(0).equals("QualifiedIdentifier") ||
            rightTypeNode.getGeneric(0).getString(0).equals("QualifiedIdentifier"))
        {
          n.setProperty(Constants.IDENTIFIER_TYPE, Constants.CLASS_IDENTIFIER);
          n.setProperty(Constants.IDENTIFIER_DECLARATION, inheritanceTree.getClassDeclarationNode("java.lang.String"));
          n.setProperty(Constants.IDENTIFIER_TYPE_NODE, GNode.create("Type", Disambiguator.disambiguate("java.lang.String")));
          return Constants.CLASS_IDENTIFIER; 
        }
         
        String leftType = leftTypeNode.getGeneric(0).getString(0);
        String rightType = rightTypeNode.getGeneric(0).getString(0);

        n.setProperty(Constants.IDENTIFIER_TYPE, Constants.PRIMITIVE_TYPE_IDENTIFIER);
        
        String resultType = Type.compare(leftType, rightType);
        n.setProperty(Constants.IDENTIFIER_TYPE_NODE, GNode.create("Type", GNode.create("PrimitiveIdentifier", resultType)));
        
        return Constants.PRIMITIVE_TYPE_IDENTIFIER;    
      }


      
      public String visitThisExpression(GNode n){
        // Just to be consistent for ThisExpressions
        if (selectionExpressionBuilder != null){
          selectionExpressionBuilder.insert(0, "__this");
        }
        
        n.setProperty(Constants.IDENTIFIER_TYPE, Constants.CLASS_IDENTIFIER);
        n.setProperty(Constants.IDENTIFIER_DECLARATION, cppClass);
        n.setProperty(Constants.IDENTIFIER_TYPE_NODE, GNode.create("Type", Disambiguator.disambiguate(cppClass.getString(0))));

        return Constants.CLASS_IDENTIFIER;
      }

      private int selectionExpressionDepth = 0;
      private StringBuilder selectionExpressionBuilder;
      /**
       * Have the SelectionExpressions carry the innermost PrimaryIdentifier's type, except for the outermost one
       * for a qualified identifier
       */
      public String visitSelectionExpression(GNode n){
        if (selectionExpressionDepth == 0)
          selectionExpressionBuilder = new StringBuilder();

        selectionExpressionBuilder.append("." + n.getString(1));

        selectionExpressionDepth++;
        n.setProperty(Constants.IDENTIFIER_TYPE, dispatch(n.getGeneric(0)));
        //TODO: Debug code
        if (n.getStringProperty(Constants.IDENTIFIER_TYPE) == null){
          System.err.println(n);
          throw new NullPointerException();
        }
        // End debug code
        n.setProperty(Constants.IDENTIFIER_DECLARATION, n.getGeneric(0).getProperty(Constants.IDENTIFIER_DECLARATION));
        n.setProperty(Constants.IDENTIFIER_TYPE_NODE, n.getGeneric(0).getProperty(Constants.IDENTIFIER_TYPE_NODE));
        selectionExpressionDepth--;

        String expression = selectionExpressionBuilder.toString();
        // Part of the way in, we may find that we have a fully qualified type. In that case set the IDENTIFIER_TYPE to CLASS_IDENTIFIER
        if (n.getStringProperty(Constants.IDENTIFIER_TYPE).equals(Constants.QUALIFIED_CLASS_IDENTIFIER)){
          GNode classDeclaration = inheritanceTree.getClassDeclarationNode(selectionExpressionBuilder.toString());
          if (classDeclaration != null){
            n.setProperty(Constants.IDENTIFIER_TYPE, Constants.CLASS_IDENTIFIER);
            n.setProperty(Constants.IDENTIFIER_DECLARATION, classDeclaration);
            n.setProperty(Constants.IDENTIFIER_TYPE_NODE, GNode.create("Type", Disambiguator.disambiguate(classDeclaration.getString(0))));
          }
        }
        // If our child is a CLASS_IDENTIFIER, and we're still in a SelectionExpression, we must be referring to some accessible field
        else if (n.getStringProperty(Constants.IDENTIFIER_TYPE).equals(Constants.CLASS_IDENTIFIER)) {
           n.setProperty(Constants.IDENTIFIER_TYPE, Constants.FOREIGN_CLASS_FIELD_IDENTIFIER);
           GNode foreignFieldDeclaration = resolveClassField(n.getString(1), (GNode)n.getProperty(Constants.IDENTIFIER_DECLARATION));
           // Debug
           if (foreignFieldDeclaration == null) {
              throw new RuntimeException("Failed to identify field " + selectionExpressionBuilder.toString());
           }
           n.setProperty(Constants.IDENTIFIER_DECLARATION, foreignFieldDeclaration);
           n.setProperty(Constants.IDENTIFIER_TYPE_NODE, foreignFieldDeclaration.getGeneric(1));
        }

        // If we're referring to some foreign class, we want to search it for this field's declaration
        else if (n.getStringProperty(Constants.IDENTIFIER_TYPE).equals(Constants.FOREIGN_CLASS_FIELD_IDENTIFIER)){
           GNode searchClassType = ((GNode)n.getProperty(Constants.IDENTIFIER_DECLARATION)).getGeneric(1);
           // Use the Type's QualifiedIdentifier's class
           String searchClassName = Disambiguator.getDotDelimitedName(searchClassType.getGeneric(0));          
           GNode searchClassDeclaration = inheritanceTree.getClassDeclarationNode(searchClassName);
           GNode fieldDeclaration = resolveClassField(n.getString(1), searchClassDeclaration);
           if (fieldDeclaration == null) {
              throw new NullPointerException();
           }

           n.setProperty(Constants.IDENTIFIER_DECLARATION, fieldDeclaration);
           n.setProperty(Constants.IDENTIFIER_TYPE, fieldDeclaration.getGeneric(1));
        }
        
        if (expression.equals("System.out")) {
           n.setProperty(Constants.IDENTIFIER_TYPE, Constants.PRINT_IDENTIFIER);
           //TODO:Hack
           n.setProperty(Constants.IDENTIFIER_DECLARATION, new Object());
        } 

        // Bye this point we should have figured out what the selectionExpression is referring to
        if (selectionExpressionDepth == 0 && n.getProperty(Constants.IDENTIFIER_DECLARATION) == null){
          System.err.println(n.getStringProperty(Constants.IDENTIFIER_TYPE));
          System.err.println(n.getGeneric(0).getStringProperty(Constants.IDENTIFIER_TYPE));
          System.err.println(expression);
          System.err.println(n);
          throw new RuntimeException("Selected unknown class or field!");
        }

        return n.getStringProperty(Constants.IDENTIFIER_TYPE);
      }

      /* 
      public void tempVisitCallExpression(GNode n) {
        if (n.getGeneric(0).getGeneric(0).getString(0).equals("System")
                        && n.getGeneric(0).getString(1).equals("out")) {
          String option = (n.getString(2).equals("println")) ? " << endl" : null;  
          GNode printBody = dispatch(n.getGeneric(3));
        }

        cpp.add(GNode.create("PrintExpression", option, printBody));  
      }

      public void tempVisitArguments(GNode n) {
        GNode body = GNode.create("PrintBody");
        visit(n);
        for (Object o : n) {
          if (o instanceof Node) {
            body.add((GNode)o);
          } 
          else { 
            body.add((String)o); 
          }
        }
        cpp.add(body);
      }

      public void tempVisitAdditiveExpression(GNode n) {
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
      */


      public void visit(Node n) {
        for (Object o : n) if (o instanceof Node) dispatch((Node)o);
      } 

    }.dispatch(java);
    
    return null; 
  }

  public GNode getType(GNode n) {
    //declaration = n.getProperty(Constants.SCOPE).node();
    return null;
  }

  private GNode resolveScopes(GNode primaryIdentifier){
    SymbolTable.Scope scope = (SymbolTable.Scope)primaryIdentifier.getProperty(Constants.SCOPE);
    if (scope == null) {
      return null;
    }
    GNode result = (GNode)scope.lookup(primaryIdentifier.getString(0)); 
    
    if (result == null){
      System.err.println("Could not locate " + primaryIdentifier.getString(0));
    }
    System.err.println(result);
    return result;
  }

  private GNode resolveClassField(String fieldName){
    HashMap<String, GNode> fieldNameMap = (HashMap<String, GNode>)cppClass.getProperty("FieldMap");
    GNode fieldDeclaration = fieldNameMap.get(fieldName);
    return fieldDeclaration;
  }

  // Overload to refer to another class
  private GNode resolveClassField(String fieldName, GNode cppClass){
    HashMap<String, GNode> fieldNameMap = (HashMap<String, GNode>)cppClass.getProperty("FieldMap");
    GNode fieldDeclaration = fieldNameMap.get(fieldName);
    return fieldDeclaration;
  }



}
