
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

    String s2;
    s2 = 'd' + " hey";

    String s3 = "hey " + "there " + 'D';

    System.out.println(s);
    System.out.println(s2);
    System.out.println(s3);
  }
}
