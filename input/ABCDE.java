package qimpp;

/**
 * Inheritance tree:             Scope tree:
 *
 *      A                           ::
 *     / \                   /   /   |   \   \ 
 *    B   C                 A   B    C    D   E 
 *       / \               /             /     \
 *      D   E            foo           bar     baz
 *       \
 *        F
 *         \
 *          G
 *           \
 *            H
 *
 * FGH.java in input/
 *
 * However, all classes should be visible at the global namespace.
 */

public class A {
  int fieldOne;
  String fieldTwo;

  public void f1()                   { }
  public void f2(int i)              { }
  public void f3(int i, String str)  { }

  public String f4()                 { return ""; }
  public int f5(int i)               { return 0;  }
  public int f6(int i, String str)   { return 1;  }
}

class B extends A {}

class C extends A {}

class D extends C {}

class E extends C {}
