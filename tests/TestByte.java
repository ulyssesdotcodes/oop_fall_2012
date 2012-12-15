package qimpp.tests;

/** Test basic number types and arithmetic */
public class TestByte {
  
  public static void main ( String[] args ) {
  
    byte b = 1;
    String s1 = "Hello Kitty #1";
    String s2 = "";
    s2 = "Hello Kitty #" + b;
    System.out.println(s1);
    System.out.println(s2);

    b = 0;
    s1 = "Hello Kitty #0";
    s2 = "Hello Kitty #" + b;
    System.out.println(s1);
    System.out.println(s2);

    b = 127;
    s1 = "Hello Kitty #127";
    s2 = "Hello Kitty #" + b;
    System.out.println(s1);
    System.out.println(s2);
    
    b = -128;
    s1 = "Hello Kitty #-128";
    s2 = "Hello Kitty #" + b;
    System.out.println(s1);
    System.out.println(s2);
    

  }

}
