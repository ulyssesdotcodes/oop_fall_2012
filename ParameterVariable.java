package qimpp;

public class ParameterVariable extends Variable {
  public ParameterVariable(Type type, String name) {
    this(type, name, false);
  }

  public ParameterVariable(Type type, String name, boolean isPointer) {
    super(type, name, isPointer);
  }
}
