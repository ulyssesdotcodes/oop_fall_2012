


public class Overload {

  public int two(int i) {
    return i + i;
  }

  public int two(String s) {
    return s + s;
  }

  public static void main(String[] args) {
    two(2);
    two("2");
  }
}


