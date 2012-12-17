package qimpp.demo;

public class Bad {
  public static void main(String[] args) {
    Object o = null;
    o.toString();     // should throw NullPointerException
  }
}
