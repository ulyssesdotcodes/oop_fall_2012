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

  public void foo() { }
}

class B extends A {}

class C extends A {}

class D extends C {}

class E extends C {}
