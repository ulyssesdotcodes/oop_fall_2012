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

#include <sstream>

#include "Color.h"

using std::ostringstream;
using std::hex;

namespace oop {

  const Color Color::WHITE = Color(255, 255, 255);
  const Color Color::BLACK = Color(0, 0, 0);
  const Color Color::RED = Color(255, 0, 0);
  const Color Color::GREEN = Color(0, 255, 0);
  const Color Color::BLUE = Color(0, 0, 255);
  
  string Color::toString() const {
    ostringstream sout;
    sout << '#' << hex;
    if (r < 16) sout << '0';
    sout << (unsigned int)r;
    if (g < 16) sout << '0';
    sout << (unsigned int)g;
    if (b < 16) sout << '0';
    sout << (unsigned int)b;
    return sout.str();
  }

}

