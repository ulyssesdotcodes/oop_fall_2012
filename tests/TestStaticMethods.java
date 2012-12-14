package qimpp.tests;

public class TestStaticMethods {
 
  public static void printMe(String whatToPrint){
    System.out.println(whatToPrint);
  }

  public static void main ( String[] args ) {
    String foo = "Hello";
    foo = "Goodbye";
    printMe(foo);
    qimpp.tests.TestStaticMethods.printMe(foo);
  }
}
