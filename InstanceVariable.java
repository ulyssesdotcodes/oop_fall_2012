package qimpp;

public class InstanceVariable extends Variable {
  public InstanceVariable(Type type, String name) {
    this(type, name, false);
  }

  public InstanceVariable(Type type, String name, boolean isPointer) {
    super(type, name, isPointer);
  }
}
