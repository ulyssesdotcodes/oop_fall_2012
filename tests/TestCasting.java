
package qimpp.tests;

public class TestCasting {
  public String toString() {
    return "Hello";
  }

  public static void main(String[] args) {
    TestCasting tc = new TestCasting();
    Object o = new Object();
    String s = "test string";

    System.out.println(o instanceof Object);       // true
    System.out.println(s instanceof Object);       // true
    System.out.println(s instanceof String);       // true
    System.out.println(o instanceof String);      // false
    System.out.println(tc instanceof TestCasting); // true
    System.out.println(tc instanceof Object);      // true

    Object obj = s;
    String str = (String)obj;
    System.out.println("str: \"" + str + "\"");

    TestCasting t = new TestCasting();
    
    o = t;

    if (o instanceof TestCasting) {
      System.out.println("PASS o instanceof Test");
    } else {
      System.out.println("FAIL o instanceof Test");
    }

    if (o instanceof Object) {
      System.out.println("PASS o instanceof Object");
    } else {
      System.out.println("FAIL o instanceof Object");
    }

    if (! (o instanceof String)) {
      System.out.println("PASS ! (o instanceof String)");
    } else {
      System.out.println("FAIL ! (o instanceof String)");
    }



  }
}


