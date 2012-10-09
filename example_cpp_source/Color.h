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

#pragma once

#include <string>

using std::string;

namespace oop {

  class Color {
    unsigned char r, g, b;
    
  public:
    static const Color WHITE;
    static const Color BLACK;
    static const Color RED;
    static const Color GREEN;
    static const Color BLUE;
    
    Color(unsigned char r, unsigned char g, unsigned char b)
      : r(r), g(g), b(b) {
    }

    unsigned char red() const {
      return r;
    }
    
    unsigned char green() const {
      return g;
    }
    
    unsigned char blue() const {
      return b;
    }
    
    string toString() const;
    
  };

}
