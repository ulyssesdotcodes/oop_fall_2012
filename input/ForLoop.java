

public class ForLoop {
  public static void main(String[] args) {
    // simple
    for (int i = 0; i < 5; i++) {
      System.out.println("i = " + i); // goes up to 4
    }

    // two declarations
    for (int i = 0, j = 5; i < 5; i++) {
      System.out.println("i = " + i); // goes up to 4
      System.out.println("j = " + j); // stays at 5
    }

    // incrementing and decrementing
    for (int i = 0, j = 5; i < 5; i++, j--) {
      System.out.println("i = " + i); // goes up to 4
      System.out.println("j = " + j); // goes down to 1
    }
  }
}
