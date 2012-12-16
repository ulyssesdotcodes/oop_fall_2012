package qimpp.tests;

/** Test basic number types and arithmetic */
public class TestMethodCall {
  
 public static Object R1 = new Object(); 
 public static Object R2 = new Object();
 public static Object R3 = new Object();
 public static Object R4 = new Object();

 static{
   R1 = new Object();
 }

  public TestMethodCall() {
  /*  R1 = new Object();
    R2 = new Object();
    R3 = new Object();
    R4 = new Object();*/
  }
 /* 
  public Object m1() {
    return R1;
  }

  public Object m2() {
    return R3;
  }

*/
  public static void main ( String[] args ) {
  
    String s2;
    s2 = "hello";
    String s1 = "hello";

    TestMethodCall t = new TestMethodCall();
    //Child2 r = new Child2();
    Object o = new Object();

    if (s1.equals(s2)) System.out.println("Equals");
    else System.out.println("Not Equals");   

    Class k1 = t.getClass();
    Class k2 = s1.getClass();

    if (k1 != k2) {
      System.out.println("PASS k1 != k2");
    } else {
      System.out.println("FAIL K1 != k2");
    }

    if (k1.getName().equals("xtc.oop.Test")) {
      System.out.println("PASS k1.getName().equals(\"xtc.oop.Test\")");
    } else {
      System.out.println("FAIL k1.getName().equals(\"xtc.oop.Test\")");
    }

    if ("xtc.oop.Test".equals(k1.getName())) {
      System.out.println("PASS \"xtc.oop.Test\".equals(k1.getName())");
    } else {
      System.out.println("FAIL \"xtc.oop.Test\".equals(k1.getName())");
    }

    if (k1.toString().equals("class xtc.oop.Test")) {
      System.out.println("PASS k1.toString().equals(\"class xtc.oop.Test\")");
    } else {
      System.out.println("FAIL k1.toString().equals(\"class xtc.oop.Test\")");
    }


    if (! k1.equals(k2)) {
      System.out.println("PASS ! k1.equals(k2)");
    } else {
      System.out.println("FAIL ! k1.equals(k2)");
    }

    k2 = k1;
    k1 = k1.getSuperclass();
    k2 = k2.getSuperclass();

    if (k1 == k2) {
      System.out.println("PASS k1.super() == k2.super().super()");
    } else {
      System.out.println("FAIL K1.super() == k2.super().super()");
    }

    if (k1.equals(k2)) {
      System.out.println("PASS k1.super().equals(k2.super().super())");
    } else {
      System.out.println("FAIL k1.super().equals(k2.super().super())");
    }

     k1 = k1.getSuperclass();

    if (null == k1) {
      System.out.println("PASS null == k1.super().super()");
    } else {
      System.out.println("FAIL null == k1.super().super()");
    }




    /*
    o = r.m1();

    if (R2 == o) {
      System.out.println("PASS r.m1()");
    } else {
      System.out.println("FAIL r.m1()");
    }
*/

  }

}
