package qimpp.tests;

/** Test basic number types and arithmetic */
public class TestShortArray {
  
  public static void main ( String[] args ) {
  
    short[] a0 = new short[0];

    if (a0.length == 0) {
      System.out.println("PASS short[0].length == 0");
    } else {
      System.out.println("FAIL short[0].length == 0");
    }
     

  }

}
