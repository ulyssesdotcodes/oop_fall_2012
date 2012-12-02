package qimpp.tests;

public class TestMethodChaining{
  int x;
  public TestMethodChaining(){
    x = 1;
  }
  public TestMethodChaining addTwo(){
    x = x+2;
    return this;
  }
  public void printout(){
    System.out.println("Value x is" + x);
  }
  public static void main (String[] args){
    TestMethodChaining t = new TestMethodChaining();
    t.addTwo().addTwo().printout();
  }
}
