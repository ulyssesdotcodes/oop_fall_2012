
package qimpp;

// TODO: Each variable should have a scope field
// TODO: Code duplication. Is there a better way?

/**
 * A generic variable.
 * 
 *
 * You can declare variables as such:
 * <code>Variable.Instance instanceVariable;</code>
 * <code>Variable.Static classVariable;</code>
 * <code>Variable.Local localVariable;</code>
 * <code>Variable.Parameter parameterVariable;</code>
 *
 * @author Qimpp
 */
public abstract class Variable {
  String name;
  Type type;
  boolean isPointer;

  public Variable(Type type, String name, boolean isPointer) {
    this.type       = type;
    this.name       = name;
    this.isPointer  = isPointer;
  }

  public String getName() {
    return this.name;
  }

  public Type getType() {
    return this.type;
  }

  public boolean isPointer() {
    return this.isPointer;
  }
}
