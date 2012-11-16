

public class OperationOrder {
  public static void main(String[] args) {
    int year = 2012;
    System.out.println(year % 4 == 0 && year % 100 != 0 || year % 400 == 0);
  }
}
