package qimpp.tests;

public class Foo {

  public String zebra;

  private static void print(Test1 t){
    System.out.println("It's a Test1");
  }

  public static void print(Foo f){
    System.out.println("It's a Foo");
  }
          
  public Foo() {
    zebra = "In the room";
  }

  public void instanceprint(Test1 t){
    print(t);
  }
              
  public String toString() {
    return "FOO";
  }

  public String allTheAnimals(){
    return zebra;
  }
/*
  public static void main(String[] args) {
    System.out.print("hello");
  }
*/
}
