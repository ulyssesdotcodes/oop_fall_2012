package qimpp.tests;

public class TestScopes {
  static int foo = 3;
  public void Bar{
    String bar = "hello";
    System.out.println(bar);
  }
  public static void main ( String[] args ) {
    System.out.println(foo);
    int foo = 5;
    System.out.println(foo);
    
  }
  
}
