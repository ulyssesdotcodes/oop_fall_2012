
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
  }
}


