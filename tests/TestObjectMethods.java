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

    if (t.equals(r)) {
      System.out.println("PASS t.equals(r)");
    } else {
      System.out.println("FAIL t.equals(r)");
    }

    if (r.equals(t)) {
      System.out.println("PASS r.equals(t)");
    } else {
      System.out.println("FAIL r.equals(t)");
    }

    int h = r.hashCode();

    if (7353 == h) {
      System.out.println("PASS 7353 == r.hashCode()");
    } else {
      System.out.println("FAIL 7353 == r.hashCode()");
    }

/*    String s1 = t.toString();
    String s2 = r.toString();

    if (s1.equals(s2)) {
      System.out.println("PASS t.toString().equals(r.toString())");
    } else {
      System.out.println("FAIL t.toString().equals(r.toString())");
    }
*/

    t = new TestObjectMethods();

    if (t != r) {
      System.out.println("PASS t != r");
    } else {
      System.out.println("FAIL t != r");
    }


  }

}
