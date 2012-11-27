package qimpp;

public class StaticVariable extends Variable {
  public StaticVariable(Type type, String name) {
    this(type, name, false);
  }

  public StaticVariable(Type type, String name, boolean isPointer) {
    super(type, name, isPointer);
  }
}
