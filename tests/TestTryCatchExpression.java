package qimpp.tests;

/** Test Try/Catch expression 2 */
public class TestTryCatchExpression {
  public String writeString() {
    return "Hey there!";
  }

  public static void main (String[] args) {
    TestTryCatchExpression tc = null;

    try {
      System.out.println(tc.writeString());
    } catch (Exception e) {
      System.out.println("Null Pointer Exception.");
    }
  }

}
