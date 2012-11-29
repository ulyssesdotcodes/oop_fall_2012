package qimpp.tests;

/** Test basic number types and arithmetic */
public class Test2 {
  
  public static void main ( String[] args ) {
    
    int num1 = 97;
    int num2 = -23;
    int num3 = 0;

    long lng1 = 930202587;
    long lng2 = 3298423;

    double dbl1 = 10.38754357823902374;
    double dbl2 = 67.38966324238472389;
    double dbl3 = 0;

    float flt1 = 87.01f;
    float flt2 = 970.24f;

    // Even though we don't have Integer or Double, we can rely on the magic that cout
    // will print a number as it is, and System.out.println() will do the same

    System.out.println( num1 + num2 );

    // Note that rounding negative integers works differently in C++, we may need to
    // make a library call for arithmetic... We can not worry about it for now, but
    // let's keep it in here
    System.out.println( num1 / num2 ); 
    System.out.println( num1 % num2 );

    // Basic variable assignment;
    num3 = num1 + num2;
    System.out.println( num3 );

    System.out.println( dbl1 * dbl2 );

    System.out.println( dbl1 / dbl2 );

    // Order of operations
    System.out.println( flt1 * flt2 / dbl1 * dbl2 + lng1 - lng2 );

  }

}
