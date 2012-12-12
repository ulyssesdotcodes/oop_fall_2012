package qimpp.tests;

public class TestNotExpression {

  public static void main(String args[]) {
    boolean awesome = false;
    boolean sweet = true;
    boolean statement;

    System.out.println("awesome = " + awesome);
    System.out.println("sweet = " + sweet);

    System.out.println("Not awesome");
    System.out.println(!awesome);

    System.out.println("Not awesome and not sweet");
    statement = !awesome && !sweet;
    System.out.println(statement);
    
    statement = !awesome || !sweet;
    System.out.println("Not awesome or not sweet");
    System.out.println(statement);
    
    statement = !(awesome && sweet);
    System.out.println("Not (awesome and  sweet)");
    System.out.println(statement);

    statement = !(awesome || sweet);
    System.out.println("Not (awesome or  sweet)");
    System.out.println(statement);
   
  }

}
