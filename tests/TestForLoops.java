package qimpp.tests;

public class TestForLoops {

  public static void main ( String[] args ) {
  //print out "0, 1, 2, 3, 4, "
    for(int i = 0; i<5; i++){
      System.out.print(i + ", ");
    }
    System.out.print("\n");
    int j;
    for(j = 1; j<=2; j++){
      System.out.print("Print this line twice.");
    }
  }
  
}
