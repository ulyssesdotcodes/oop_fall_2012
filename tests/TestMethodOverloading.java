package qimpp.tests;

public class TestMethodOverloading{

  public void m(){
    System.out.println("Nothing entered");
  }
  public void m(int i){
    System.out.println("You entered one integer" + i);
   }
  public void m(int i, int j){
    System.out.println("You entered two integers" + i + j);
  }
  public void m(String s){
    System.out.println("You entered a string" + s);
  }
  public static void main (String[] args){
    TestMethodOverloading t = new TestMethodOverloading();
    t.m();
    t.m(3);
    t.m(3, 4);
    t.m("string");
  }
 }
