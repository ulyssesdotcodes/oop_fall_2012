


public class Overload {

  public static int two(int i, Object o) {
    return 0;
  }

  public static int two(int i, String s) {
    return 1;
  }

  public static void main(String[] args) {
    two(0, "hey");
  }
}


