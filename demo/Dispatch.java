package qimpp.demo;

public class Dispatch {
  public String toString() {
    return "We override toString()";
  }

  public int hashCode() {
    return 38289; 
  }

  public boolean equals(Object o) {
    return true;
  }

  public static void main(String[] args) {
    Dispatch d = new Dispatch();
    Object o = new Object();
    System.out.println(d.toString());  // We override toString()
    System.out.println(d.hashCode());  // 38289
    if (d.equals(o)) {
      System.out.println("pass");
    }
  }
}
