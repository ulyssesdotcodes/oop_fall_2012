package qimpp.tests;

public class TestArithmetic {

  public static void main ( String[] args ) {
    int i = 5;
    int j = 10;
    int addition = i + j;
    int subtraction = i - j;
    int multiplication = i * j;
    int division = j / i;
    int modulo = (j-1)%i;
    System.out.println(addition + ", " + subtraction + ", " + multiplication + ", " + division + ", " + modulo);
  }
  
}
