package qimpp;
import xtc.tree.*;

public class StaticInitializerPrinter extends Visitor{
  Printer printer;

  public StaticInitializerPrinter(Printer p){
    printer = p;
  }

  public void visitClassDeclaration(GNode n){
    String className = n.getString(0);
    String classType = Type.getClassTypeName(className);
    printer.pln().indent().p(classType).p("::__static_init();").pln();
  }

  public void visit(Node n){
    for (Object o : n){
      if (o instanceof Node)
        dispatch((Node)o);
    }
  }
}
