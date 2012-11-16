

public class StaticMethod {
  public static void main(String[] args) {
    int result = MathUtility.add(1,4);
    System.out.println("(1 + 4) is: " + result); // "(1 + 4) is 5"

    ObjectCounter o1 = new ObjectCounter();
    System.out.println(o1.getNumberOfObjects());
    ObjectCounter o2 = new ObjectCounter();
    System.out.println(o2,getNumberOfObjects());
  }
}

class MathUtility {
  public static int add(int first, int second) {
    return first + second;
  }
}

class ObjectCounter {
  static int counter = 0;

  public ObjectCounter() {
    counter = counter + 1;
  }

  public int getNumberOfObjects() {
    return counter;
  }


