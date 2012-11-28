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
  public void foo() {
    for (int i = 0; i < 4; i++) {
      int j = 0;
    }
  }
}

class B extends A {}

class C extends A {}

class D extends C {
  public void bar() {
    // Do nothing.
  }
}

class E extends C {
  public void baz() {
    // Do nothing.
  }
}
