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

#include <iostream>

#include "Color.h"
#include "Point.h"

using oop::Color;
using oop::Point;
using oop::ColorPoint;

using std::cout;
using std::endl;

int main() {
  Point* p = new ColorPoint(Color::BLUE, 3, 0, 4, 0);

  cout << "The point          : " << p->toString() << endl;
  cout << "The distance       : " << Point::ORIGIN.getDistanceFrom(*p) << endl;

  cout << "sizeof(Point)      : " << sizeof(Point) << endl;
  cout << "sizoef(ColorPoint) : " << sizeof(ColorPoint) << endl;

  delete p;

  return 0;
}
