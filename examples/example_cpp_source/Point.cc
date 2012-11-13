/*
 * Object-Oriented Programming
 * Copyright (C) 2012 Robert Grimm
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

#include <stdexcept>
#include <math.h>
#include <sstream>

#include "Point.h"

namespace oop {

  const Point Point::ORIGIN /*= Point()*/;

  Point::Point(double c1, double c2, double c3, double c4) {
    coordinates[0] = c1;
    coordinates[1] = c2;
    coordinates[2] = c3;
    coordinates[3] = c4;
  }

  double Point::getCoordinate(int i) const {
    if (i < 0 || DIMENSIONS <= i) throw std::out_of_range("invalid index");
    return coordinates[i];
  }

  void Point::setCoordinate(int i, double c) {
    if (i < 0 || DIMENSIONS <= i) throw std::out_of_range("invalid index");
    coordinates[i] = c;
  }

  double Point::getDistanceFrom(const Point& p) const {
    double distanceSquared = 0;

    for (int i=0; i<DIMENSIONS; i++) {
      double diff = this->getCoordinate(i) - p.getCoordinate(i);
      distanceSquared += diff * diff;
    }

    return sqrt(distanceSquared);
  }

  string Point::toString() const {
    std::ostringstream sout;
    sout << "Point("
         << getCoordinate(0) << ", "
         << getCoordinate(1) << ", "
         << getCoordinate(2) << ", "
         << getCoordinate(3) << ")";
    return sout.str();
  }

  ColorPoint::ColorPoint(Color color, double c1, double c2, double c3, double c4)
    : Point(c1, c2, c3, c4), color(color) {
  }

  Color ColorPoint::getColor() const {
    return color;
  }

  string ColorPoint::toString() const {
    std::ostringstream sout;
    sout << "ColorPoint("
         << getColor().toString() << ", "
         << getCoordinate(0) << ", "
         << getCoordinate(1) << ", "
         << getCoordinate(2) << ", "
         << getCoordinate(3) << ")";
    return sout.str();
  }

}
