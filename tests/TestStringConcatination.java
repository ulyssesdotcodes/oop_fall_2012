
package qimpp.tests;


public class TestStringConcatination {
  public static void main(String[] args) {
    String s;

    s = "hello";
    s = s + ' ';
    s = s + 2;
    s = s + " ";
    s = s + "you!";

    s = s + s + " dude";

    System.out.println(s);
  }
}
