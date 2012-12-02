package qimpp.tests;

public class TestWhileLoops {

  public static void main ( String[] args ) {
  
    int i = 0;
    while(i < 5){
      System.out.println("Print this 4 times.");
      i++;
     }
     boolean bool = true;
     while(bool){
      System.out.println("Print this once.");
      bool = false;
      }
     while(bool){
      System.out.println("This shouldn't ever print.");
      }
  }
  
}
