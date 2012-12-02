package qimpp.tests;

public class TestInheritance{

  public static class SimpleClass{
    public String greeting;
    
    public SimpleClass(){
    greeting = "hello!";
    }
    public String getGreeting(){
    return greeting;
    }
  }
  public static class InheritedClass extends SimpleClass{
    public InheritedClass(){
    super();
    }
    
    
  public static void main( String[] args ){
    InheritedClass ic = new InheritedClass();
    System.out.println(ic.getGreeting());
  }
  }
}
