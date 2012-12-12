package qimpp.tests;

public class TestOverloadedConstructors{
  public TestOverloadedConstructors(){
    System.out.println("Noarg constructor");
  }

  public TestOverloadedConstructors(TestOverloadedConstructors t){
    System.out.println("Copy constructor :) ");
  }

  public TestOverloadedConstructors(String print){
    System.out.println("String constructor");
  }

  public TestOverloadedConstructors(Object o){
    System.out.println("Object constructor");
  }
  
  public static void main(String[] args){
    TestOverloadedConstructors t = new TestOverloadedConstructors();
    t = new TestOverloadedConstructors("Hello!");
    t = new TestOverloadedConstructors(new Object());
    t = new qimpp.tests.TestOverloadedConstructors(t); 
  }
}
