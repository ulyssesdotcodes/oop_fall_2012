package qimpp.tests;

/** Test basic number types and arithmetic */
public class TestConcatenation {
  
  public static void main ( String[] args ) {

    String s1 = "Hello Kitty #1";
    String s2 = "";
    s2 = "Hel" + "lo Kitty #1";
    System.out.println(s1);
    System.out.println(s2);

    s2 = "He" + "ll" + "o Kitty #1";
    System.out.println(s1);
    System.out.println(s2);


    s2 = "Hello Kitty #" + 1;
    System.out.println(s1);
    System.out.println(s2);

    s2 = "Hello Kitty #" + '1';
    System.out.println(s1);
    System.out.println(s2);

    s2 = (char)72 + "ello Kitty #1";
    System.out.println(s1);
    System.out.println(s2);

    char c = 72;
    s2 = c + "ello Kitty #1";
    System.out.println(s1);
    System.out.println(s2);

    s2 = 'H' + "ello Kitty #1";
    System.out.println(s1);
    System.out.println(s2);

    byte b = 1;
    s1 = "Hello Kitty #1";
    s2 = "";
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
