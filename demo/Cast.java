package qimpp.demo;

public class Cast {
  public static void main(String[] args) {
    Cast tc = new Cast();
    Object o = new Object();
    String s = "test string";

    if (o instanceof Object) System.out.print("h");       // true
    if (s instanceof Object) System.out.print("u");       // true
    if (s instanceof String) System.out.print("z");       // true
    if (o instanceof String) System.out.print("z");       // true
    if (tc instanceof Cast) System.out.print("a");        // true
    if (tc instanceof Object) System.out.print("h");      // true

    Object obj = s;
    String str = (String)obj;
    System.out.println("str: \"" + str + "\"");
  }
}


