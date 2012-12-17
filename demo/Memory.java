package qimpp.demo;

/**
 * Shows off memory management, by *not* exploding from 
 * running out of memory allocating Object pointers.
 */
public class Memory {
  public static void createObject() {
    Object o = new Object();
  }

  public static void main(String[] args) {
    int i = 100000000;
    while (i-- > 0) { createObject(); }
  }
}
