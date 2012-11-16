

public class ArithmeticAssignmentOperator {
  public static void main(String[] args) {
    int i = 1;
    int j = 2;

    i += 10;
    j -= 2;

    System.out.println("i = " + i);   // Should be "i = 11"
    System.out.println("j = " + j);   // Should be "j = 0"
  }
}
