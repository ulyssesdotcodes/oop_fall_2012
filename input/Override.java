

public class Override {
  public static void main(String[] args) {

  }

  public String toString() {
    System.out.println("toString() called!" + "whoo");
    System.out.println("toString() called!" + 5);
    System.out.println(3 + 2.3);
    return "Overriden!";
  }
}
