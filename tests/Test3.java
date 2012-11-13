package qimpp.tests;

/** Test if statements and loops */
public class Test3 {
          
  public static void main (String[] args) {
    int age = 0;
    boolean underage = true;
    System.out.println("age: " + age);
    System.out.println("underage: " + underage);
    for (int i = 0; i < 10; i++) {
      age++;
    }
    System.out.println("age: " + age);
    System.out.println("underage: " + underage);
    while (underage) {
      age++;
      if (age == 18) {
        underage = false;
      }
    }
    System.out.println("age: " + age);
    System.out.println("underage: " + underage);
  }
}
