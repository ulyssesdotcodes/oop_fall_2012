/** A class to test inheritance */
public class PointChild extends Point {

  /** A simple override, no signature change */
  public double getCoordinate(int idx){
    return 0;
  }

  /** This one tests an override with a signature with an argument of inheriting type. We need to figure out how to handle this */
  public double distance(PointChild p){
    return 1;
  }
}
