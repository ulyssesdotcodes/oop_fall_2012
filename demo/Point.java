package qimpp.demo;

public class Point {

  public String toString() {
    return "Point";
  }

  public static void main(String[] args) {
    Point p = new Point();
    System.out.println(p.toString());

    RedPoint r = new RedPoint();
    System.out.println(r.toString());
    System.out.println("My color is: " + r.getColor());
  }
}
