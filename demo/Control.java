package qimpp.demo;

public class Control {
  public static void main(String[] args) {
    if (1 > 2) {
      System.out.println("This shouldn't print."); 
    } else if (3 > 2) {
      System.out.println("This should print.");
    } else {
      System.out.println("This shouldn't print.");
    }

    int i = 10;
    while (i-- > 0) { System.out.println(i); }
    System.out.println("Blast off!");
  }
}
