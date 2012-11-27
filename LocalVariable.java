package qimpp;

public class LocalVariable extends Variable {
   public LocalVariable(Type type, String name) {
     this(type, name, false);
   }

   public LocalVariable(Type type, String name, boolean isPointer) {
    super(type, name, isPointer);
   }
}

