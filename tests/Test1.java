package qimpp.tests;
import qimpp.tests.Foo;


/** Test inheritance */
public class Test1 extends Foo {
  public String elephant;
          
  public Test1(){
    //super();
    elephant = "In the room";
    this.zebra = "Away";
  }
            
  public String allTheAnimals(){
    return zebra + elephant;
  }

  public static void main (String[] args) {
    Test1 bar = new Test1();
    System.out.println(bar.toString());
    System.out.println(bar.allTheAnimals());
  }
}
