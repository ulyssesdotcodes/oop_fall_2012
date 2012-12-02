package qimpp.tests;

public class TestMethodOverloadingWithInheritance{
  public static class Foo{
    public String toString(){
      return "FOO";
    }
  }
  public static class Bar extends Foo{
    public String toString(){
      return "BAR";
    }
  }
  public void m(){
    System.out.println("Nothing entered");
  }
  public void m(Foo f){
    System.out.println("You entered a Foo");
   }
  public void m(Foo f, Foo g){
    System.out.println("You entered two Foos");
  }
  public void m(Bar b){
    System.out.println("You entered a Bar");
  }
  public static void main (String[] args){
    Foo f = new Foo();
    Bar b = new Bar();
    TestMethodOverloadingWithInheritance t = new TestMethodOverloadingWithInheritance();
    t.m();
    t.m(f);
    t.m(b);
    t.m(f, b);
  }
 }
