

public class Klass {
  private String name;

  public void setName(String n) {
    name = n;
  }

  public String getName() {
    return name;
  }

  public static void main(String[] args) {
    Klass klass = new Klass();
    klass.setName("Visitor");
    System.out.println("Hello " + klass.getName()); // "Hello Visitor"
  }
}
