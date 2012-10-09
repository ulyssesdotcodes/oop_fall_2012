package qimpp;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import xtc.tree.Printer;

import java.util.ArrayList;

//TODO: -Inheritance from classes other than Object
//      -Inheritance of fields

/** Manages rich people's money.
*
* Actually, it should modify the AST to contain the inherited methods.
* Instead of a Block it should contain something to indicate that the method is
* inherited, and which class it is inherited from, so HeaderGenerator and
* ImplementationGenerator can write them out.
*
*/
public class InheritanceManager {
  /** Small trees containing descriptions of Object's public methods, so we can overload properly in the vtable 
  * These need to be hard-coded, as we're using hand-made C++ for Object
  * I decide to use GNodes to describe functions as we're going for a soft tree, rather than a hard-defined structure for
  * describing methods
  */
  private GNode ObjectMethods;
  
  // Add in the same for String and Class so we can extend them? That seems like a stress test possibility
  
  //TODO: Add methods for making a new simplified "ClassBody" node to pass to HeaderWriter
  
  public InheritanceManager(){
  
    // Hard-code the Object methods here
    ObjectMethods = GNode.create("ClassBody");
    
    // Add nodes that look like this one, so that we have predefined methods
    /*MethodDeclaration(
        Modifiers(
          Modifier(
            "public"
          )
        ),
        null,
        Type(
          PrimitiveType(
            "double"
          ),
          null
        ),
        "getCoordinate",
        FormalParameters(
          FormalParameter(
            Modifiers(),
            Type(
              PrimitiveType(
                "int"
              ),
              null
            ),
            null,
            "idx",
            null
          )
        ),
        null,
        null,
        null, <-- No Block
        see PointAST.txt
     */
     GNode modifiers = GNode.create("Modifiers", GNode.create("Modifier", "public"));
     GNode type = GNode.create("Type", GNode.create("PrimitiveType", "int"), null);
     GNode formalParameters = GNode.create("FormalParameters"); // No args to hashcode other than self
     GNode hashCode = GNode.create("MethodDeclaration", modifiers, null, type, "hashCode", formalParameters, null, null, null);
     ObjectMethods.add(hashCode);
     
     modifiers = GNode.create("Modifiers", GNode.create("Modifier", "public"));
     type = GNode.create("Type", GNode.create("PrimitiveType", "boolean"), null);
     formalParameters = GNode.create("FormalParameters", GNode.create("FormalParameter", GNode.create("Modifiers"), GNode.create("Type", GNode.create("QualifiedIdentifier", "Object"), null), null, "o", null));
     GNode equals = GNode.create("MethodDeclaration", modifiers, null, type, "equals", formalParameters, null, null, null);
     ObjectMethods.add(equals);
     
     modifiers = GNode.create("Modifiers", GNode.create("Modifier", "public"));
     type = GNode.create("Type", GNode.create("QualifiedIdentifier", "Class"), null);
     formalParameters = GNode.create("FormalParameters"); // No args to hashcode other than self
     GNode getClass = GNode.create("MethodDeclaration", modifiers, null, type, "getClass", formalParameters, null, null, null);
     ObjectMethods.add(getClass);
     
     modifiers = GNode.create("Modifiers", GNode.create("Modifier", "public"));
     type = GNode.create("Type", GNode.create("QualifiedIdentifier", "String"), null);
     formalParameters = GNode.create("FormalParameters"); // No args to hashcode other than self
     GNode toString = GNode.create("MethodDeclaration", modifiers, null, type, "toString", formalParameters, null, null, null);
     ObjectMethods.add(toString);
  }
  
  /** Convert the class declaration from a Java AST to a Qimpp C++ AST with inherited methods at the beginning etc. 
  * The structure is (Modifiers, ClassName[String], ClassBody). ClassBody contains InheritedMethods, MethodDeclarations, and FieldDeclarations 
  */
  public GNode getQimppClassDeclaration(GNode classDeclaration){
    GNode qClassDec = GNode.create("QimppClassDeclaration");
    qClassDec.add(null).add(classDeclaration.getString(1));
    
    GNode qimppClassBody = GNode.create("QimppClassBody");
    GNode classBody = (GNode)classDeclaration.get(5);
    
    // Add all the inherited methods first, so the vtable is ordered correctly
    // We may have to change the structure of InheritedMethod once we do inheritance
    for (int i = 0; i < ObjectMethods.size(); i++){
      qimppClassBody.add(GNode.create("InheritedMethod").add(ObjectMethods.getNode(i)).add(GNode.create("ParentName", "Object")));
    }
    
    // We're going to be naive and check only for matches in the function name for 
    // If there's a match, we swap out that entry for our overloaded function
    // otherwise we just stick it onto the bottom
    //TODO: Improve matching of overrides
    for (int i = 0; i < classBody.size(); i++){
      if (classBody.getNode(i).hasName("MethodDeclaration")){
        boolean replaced = false;
        for (int j = 0; j < qimppClassBody.size(); j++){
          
          if (qimppClassBody.getNode(j).hasName("InheritedMethod")){
            //DEBUG: new Printer(System.out).format(qimppClassBody.getNode(j)).pln().flush();
            if (qimppClassBody.getNode(j).getNode(0).getString(3).equals(classBody.getNode(i).getString(3))){
              qimppClassBody.set(j, classBody.getNode(i));
              replaced = true;
              break;
            }
          }
        }
        
        if (!replaced){
          qimppClassBody.add(classBody.getNode(i));
        }
      }
      // We're not inheriting fields yet, we just stick 'em on to the bottom
      else if (classBody.getNode(i).hasName("FieldDeclaration")){
        qimppClassBody.add(classBody.getNode(i));
      }
    }
    
    qClassDec.add(qimppClassBody);
    return qClassDec;
  }
  
}
