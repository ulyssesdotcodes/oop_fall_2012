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

#include "java_lang.h"

using namespace java::lang;

int main(void) {
  // Let's get started.
  std::cout << "--------------------------------------------------------------"
            << "----------------"
            << std::endl;

  // Object o = new Object();
  Object o = new __Object();

  std::cout << "o.toString() : "
            << o->__vptr->toString(o)->data // o.toString()
            << std::endl;

  // Class k = o.getClass();
  Class k = o->__vptr->getClass(o);

  std::cout << "k.getName()  : "
            << k->__vptr->getName(k)->data // k.getName()
            << std::endl
            << "k.toString() : "
            << k->__vptr->toString(k)->data // k.toString()
            << std::endl;

  // Class l = k.getClass();
  Class l = k->__vptr->getClass(k);

  std::cout << "l.getName()  : "
            << l->__vptr->getName(l)->data // l.getName()
            << std::endl
            << "l.toString() : "
            << l->__vptr->toString(l)->data // l.toString()
            << std::endl;

  // if (k.equals(l)) { ... } else { ... }
  if (k->__vptr->equals(k, (Object)l)) {
    std::cout << "k.equals(l)" << std::endl;
  } else {
    std::cout << "! k.equals(l)" << std::endl;
  }

  // if (k.equals(l.getSuperclass())) { ... } else { ... }
  if (k->__vptr->equals(k, (Object)l->__vptr->getSuperclass(l))) {
    std::cout << "k.equals(l.getSuperclass())" << std::endl;
  } else {
    std::cout << "! k.equals(l.getSuperclass())" << std::endl;
  }

  // if (k.isInstance(o)) { ... } else { ... }
  if (k->__vptr->isInstance(k, o)) {
    std::cout << "o instanceof k" << std::endl;
  } else {
    std::cout << "! (o instanceof k)" << std::endl;
  }

  // if (l.isInstance(o)) { ... } else { ... }
  if (l->__vptr->isInstance(l, o)) {
    std::cout << "o instanceof l" << std::endl;
  } else {
    std::cout << "! (o instanceof l)" << std::endl;
  }

  // HACK: Calling java.lang.Object.toString on k
  std::cout << o->__vptr->toString((Object)k)->data << std::endl;

  // Done.
  std::cout << "--------------------------------------------------------------"
            << "----------------"
            << std::endl;
  return 0;
}
