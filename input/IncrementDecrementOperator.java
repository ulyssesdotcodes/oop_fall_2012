

public class IncrementDecrementOperator {
  public static void main(String[] args) {
    int i = 10;
    int j = 10;

    i++;
    j++;

    System.out.println("i = " + i); // "i = 11"
    System.out.println("j = " + j); // "j = 11"

    int k = i++;
    int l = ++j;

    System.out.println("k = " + k); // "k = 11"
    System.out.println("l = " + l); // "l = 12"
  }
}


