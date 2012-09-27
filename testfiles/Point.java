/*
 * Object-Oriented Programming
 * Copyright (C) 2011 Robert Grimm
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
 */

/**
 * An immutable four-dimensional point.
 *
 * @author Robert Grimm
 */
public class Point {

  /** The origin. */
  public static final Point ORIGIN = new Point(0,0,0,0); // Really: 0.0d
  
  private final double[] coordinates;

  /**
   * Create a new point.
   *
   * @param c1 The first coordinate.
   * @param c2 The second coordinate.
   * @param c3 The third coordinate.
   * @param c4 The fourth coordinate.
   */
  public Point(double c1, double c2, double c3, double c4) {
    coordinates = new double[] { c1, c2, c3, c4 };
  }

  /**
   * Get the specified coordinate.
   *
   * @param idx The dimension.
   * @return The coordinate.
   * @throws ArrayIndexOutOfBoundsException Signals an invalid index.
   */
  public double getCoordinate(int idx) {
    try {
      return coordinates[idx];
    } catch (ArrayIndexOutOfBoundsException x) {
      throw new IndexOutOfBoundsException("Index: " + idx);
    }
  }

  /**
   * Determine the distance from the specified point.
   *
   * @param p The other point.
   * @return The distance.
   */
  public double distance(Point p) {
    double distance = 0;

    for (int i=0; i<=3; i++) {
      double diff = this.getCoordinate(i) - p.getCoordinate(i);
      distance += diff * diff;
    }

    return Math.sqrt(distance);
  }

  /**
   * Get a string representation for this point.
   *
   * @return The string representation.
   */
  public String toString() {
    return "Point(" +
      getCoordinate(0) + ", " +
      getCoordinate(1) + ", " +
      getCoordinate(2) + ", " +
      getCoordinate(3) + ")";
  }

  public static void main(String[] args) {
    Point p1 = new Point(1, 2, 3, 4);

    System.out.println(p1);
    System.out.println("Distance from origin: " + p1.distance(Point.ORIGIN));
  }

}

