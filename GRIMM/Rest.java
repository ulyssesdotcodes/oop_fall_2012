/*
 * Copyright (C) 2006-2012 Robert Grimm. All Rights Reserved.
 */

package xtc.oop;

public class Rest extends Test {

  static {
    step = 3;
  }

  public int round;

  // -------------------------------------------------------------------------

  public Rest() {
    round = 0;
  }

  // -------------------------------------------------------------------------

  public Object m1() {
    return Test.R2;
  }

  // -------------------------------------------------------------------------

  public static Object m2() {
    return Test.R4;
  }

  // -------------------------------------------------------------------------

  public Test m4() {
    round++;
    return this;
  }

  // -------------------------------------------------------------------------

  public Object m7(Test t) {
    return R3;
  }

  // -------------------------------------------------------------------------

  public Object m11(char n) {
    return R1;
  }

  public Object m11(short n) {
    return R2;
  }

  public Object m11(int n) {
    return R3;
  }

  public Object m11(long n) {
    return R4;
  }

  // -------------------------------------------------------------------------

  public Object m12(int n) {
    return R2;
  }

  public Object m12(long n) {
    return R3;
  }

  // -------------------------------------------------------------------------

  public int hashCode() {
    return 7353;
  }

  // -------------------------------------------------------------------------

  public static void main(String[] args) {
    System.out.println("FAIL Test.main()");
    System.out.println();
    System.out.println("0 out of n tests have passed.");
  }

}
