package qimpp.demo;

public class Addition {
  public int    add(int i, int j)       { return i + j; }
  public String add(String s, String r) { return s + r; }
  public String add(int i,    String r) { return "i " + i + r; }
  public String add(String r, int    i) { return r + i; }

  public static void main(String[] args) {
    Addition a = new Addition();
    System.out.println(a.add(3,2));               // 5
    System.out.println(a.add("hello ", "world")); // "hello world"
    System.out.println(a.add("am ", 5));       // "hello 5"
    System.out.println(a.add(2, " fish"));        // "2 fish"
  }
}
