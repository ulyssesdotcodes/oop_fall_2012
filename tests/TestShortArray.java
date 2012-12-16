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
    
    short[] a1 = new short[1];

    if (a1.length == 1) {
      System.out.println("PASS short[1].length == 1");
    } else {
      System.out.println("FAIL short[1].length == 1");
    }
    
    short[] a2 = new short[2];

    if (a2.length == 2) {
      System.out.println("PASS short[2].length == 2");
    } else {
      System.out.println("FAIL short[2].length == 2");
    }


   if (a1[0] == 0 && a2[0] == 0 && a2[1] == 0) {
      System.out.println("PASS short[i] == 0");
    } else {
      System.out.println("FAIL short[i] == 0");
    }

    a1[0] = 1;
    if (a1[0] == 1) {
      System.out.println("PASS short[0] = 1");
    } else {
      System.out.println("FAIL short[0] = 1");
    }
  
    a1[0] = -1;
    if (a1[0] == -1) {
      System.out.println("PASS short[0] = -1");
    } else {
      System.out.println("FAIL short[0] = -1");
    }

    a1[0] = (short)32768;
    if (a1[0] == -32768) {
      System.out.println("PASS short[0] = (short)32768");
    } else {
      System.out.println("FAIL short[0] = (short)32768");
    }

  }

}
