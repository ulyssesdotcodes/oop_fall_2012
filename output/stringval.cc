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

#include "stringval.h"

#define TRACE() \
  std::cout << __FUNCTION__ << ":" << __LINE__ << ":" << std::endl

stringval::stringval(const size_t len)
  : len(len), data(new char[len]) {
  TRACE();
}

stringval::stringval(const char* s)
  : len(std::strlen(s)), data(new char[len]) {
  TRACE();
  std::memcpy(data, s, len);
}

stringval::stringval(const stringval& other) 
  : len(other.len), data(new char[len]) {
  TRACE();
  std::memcpy(data, other.data, len);
}

stringval::~stringval() {
  TRACE();
  delete[] data;
}

stringval& stringval::operator=(const stringval& other) {
  TRACE();
  if (data != other.data) {
    delete[] data;
    len = other.len;
    data = new char[len];
    std::memcpy(data, other.data, len);
  }
  return *this;
}

size_t stringval::length() const {
  return len;
}

const char& stringval::operator[](const size_t idx) const {
  return data[idx];
}

char& stringval::operator[](const size_t idx) {
  return data[idx];
}

stringval stringval::operator+(const stringval& other) const {
  TRACE();
  stringval result(len + other.len);
  std::memcpy(result.data, data, len);
  std::memcpy(result.data + len, other.data, other.len);
  return result;
}

std::ostream& operator<<(std::ostream& out, const stringval& s) {
  const size_t length = s.length();
  for (size_t i=0; i<length; i++) {
    out << s[i];
  }
  return out;
}

int main() {
  TRACE(); stringval s1("Ribert");
  TRACE(); std::cout << "s1: " << s1 << std::endl;

  TRACE(); s1[1] = 'o';
  TRACE(); std::cout << "s1: " << s1 << std::endl;

  TRACE(); stringval s2(" Grimm");
  TRACE(); std::cout << "s2: " << s2 << std::endl;

  TRACE(); stringval s3("");
  TRACE(); std::cout << "s3: " << s3 << std::endl;

  TRACE(); s3 = s2;
  TRACE(); std::cout << "s3: " << s3 << std::endl;

  TRACE(); s2[0] = '_';
  TRACE(); std::cout << "s2: " << s2 << std::endl;
  TRACE(); std::cout << "s3: " << s3 << std::endl;

  TRACE(); stringval s4(s1 + s3);
  TRACE(); std::cout << "s4: " << s4 << std::endl;

  TRACE(); return 0;
}
