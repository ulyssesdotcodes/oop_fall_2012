package qimpp.tests;

/** Test basic number types and arithmetic */
public class TestObjectMethods {
  public static void main ( String[] args ) {
    TestObjectMethods t;
    Child r = new Child();

    t = r;   

    if (t == r) {
      System.out.println("PASS t == r");
    } else {
      System.out.println("FAIL t == r");
    }

  }

}
