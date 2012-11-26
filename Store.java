
package qimpp;



import java.util.ArrayList;


/**
 * Decompose and store Java AST as classes.
 *
 * @author Qimpp
 */
class Store {

  /** Classes in store. */
  private ArrayList<Klass> classes;

  public Store() {
    classes = new ArrayList<Klass>();
  }



  /**
   * Get classes.
   *
   * @return classes.
   */
  public ArrayList<Klass> getClasses() {
    return classes;
  }

  /**
   * Add class to classes list.
   *
   * @param klass Class to append.
   */
  public void add(Klass klass) {
    classes.add(klass);
  }

}
