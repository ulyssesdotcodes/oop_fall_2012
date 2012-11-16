

public class Break {
  public static void main(String[] args) {
    for (int i = 0; i < 5; i++) {
      System.out.println("i = " + i); // i goes to 3, once
      if (i == 3) break;
    }

    for (int j = 0; j < 5; j++) {
      for (int k = 0; k < 5; k++) {
        System.out.println("k = " + k); // k goes to 3, 5 times
        if (k == 3) break;
      }
    }
  }
}
