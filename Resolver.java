
package qimpp;

import java.util.ArrayList;

/**
 * Resolves a 
 */
public class Resolver {
  /** Fresh name generator count. */
  public static int freshId = 0;

  /** The invocation. */
  Invocation invocation;

  /** The resolved identifier. */
  String resolvedIdentifier;

  /** Current class. */
  Klass currentClass;

  /** Methods list. */
  ArrayList<Klass.Method> matchedMethods;

  /** Chosen method. */
  Klass.Method chosenMethod;

  // ===========================================================================
  
  public boolean isQualifiedTypeName(String selector) {
    return Character.isUpperCase(selector.charAt(0));
  }

  public boolean isApplicable(Klass.Method matched) {
    return (this.invocation.arguments().size() == matched.parameters().size())
        && (matched.areParametersConvertible(invocation.arguments()));
  }

  public boolean isAccessible(Klass.Method matched) {
    return (matched.of() == currentClass) ||
      (/* matched.access() keeps things visible */ true);
  }

  public static String freshId(String identifier) {
    String fresh = identifier + "_" + freshId++;
    return fresh;
  }

  // ===========================================================================

  public Resolver(Invocation invocation, Klass.Member member) {
    this.invocation         = invocation;
    this.resolvedIdentifier = invocation.identifier;
    this.currentClass       = member.of();
    this.matchedMethods     = new ArrayList<Klass.Method>();
    this.chosenMethod       = null;
  }

  /**
   * Match names.
   *
   * @return this.
   */
  public Resolver match() {
    for (String selector : invocation.selectors()) {
      if (selector.equals("this")) {
        // Do nothing.
      } else if (selector.equals("super")) {
        currentClass = currentClass.parent();
      } else if (isQualifiedTypeName(selector)) {
        currentClass = Store.getQualifiedType(selector);
      } else {
        // dat ass. primary identifier.
        // TODO
      }
    }
    
    for (Klass.Method method : currentClass.methods()) {
      if (method.identifier().equals(invocation.identifier())) {
        matchedMethods.add(method);
      }
    }

    return this;
  }

  /**
   * Filter the methods by accessibility and applicability.
   * 
   * @return this.
   */
  public Resolver filter() {
    for (int i = 0; i < matchedMethods.size(); i++) {
      Klass.Method match = matchedMethods.get(i);
      if (!isApplicable(match) || !isAccessible(match)) {
        this.matchedMethods.remove(i);
      } 
    }

    return this;
  }

  /**
   * Choose the most specific method.
   *
   * @return this.
   */
  public Resolver choose() {
    Klass.Method best = matchedMethods.get(0);
   
    for (Klass.Method m : matchedMethods) {
      if (!best.isMoreSpecificThan(m)) {
        best = m;
      }
    }

    this.chosenMethod = best;

    return this;
  }

  /**
   * Rename chosen method and invocation.
   *
   * @return this.
   */
  public String rename() {
    if (this.chosenMethod.identifier()
        .equals(this.chosenMethod.resolvedIdentifier())) {
      resolvedIdentifier = freshId(chosenMethod.identifier());
      this.chosenMethod.resolvedIdentifier(resolvedIdentifier);
    }
    return resolvedIdentifier;
  }
}
