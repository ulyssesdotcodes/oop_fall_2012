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

#include <stdint.h>
#include <string>

namespace java {
  namespace lang {

    // Forward declarations of data layout and vtables.
    struct __Object;
    struct __Object_VT;

    struct __String;
    struct __String_VT;

    struct __Class;
    struct __Class_VT;

    // Definition of type names, which are equivalent to Java semantics.
    typedef __Object* Object;
    typedef __String* String;
    typedef __Class* Class;

    // ======================================================================

    // The data layout for java.lang.Object.
    struct __Object {
      __Object_VT* __vptr;
    };

    // The vtable layout for java.lang.Object.
    struct __Object_VT {
      Class __isa;
      int32_t (*hashCode)(Object);
      bool (*equals)(Object, Object);
      Class (*getClass)(Object);
      String (*toString)(Object);
    };

    // ======================================================================

    // The data layout for java.lang.String.
    struct __String {
      __String_VT* __vptr;
      std::string data;
    };

    // The vtable layout for java.lang.String.
    struct __String_VT {
      Class __isa;
      int32_t (*hashCode)(String);
      bool (*equals)(String, Object);
      Class (*getClass)(String);
      String (*toString)(String);
      int32_t (*length)(String);
      char (*charAt)(String, int32_t);
    };

    // ======================================================================

    // The data layout for java.lang.Class.
    struct __Class {
      __Class_VT* __vptr;
      String name;
      Class parent;
    };

    // The vtable layout for java.lang.Class.
    struct __Class_VT {
      Class __isa;
      int32_t (*hashCode)(Class);
      bool (*equals)(Class, Object);
      Class (*getClass)(Class);
      String (*toString)(Class);
      String (*getName)(Class);
      Class (*getSuperclass)(Class);
      bool (*isInstance)(Class, Object);
    };

  }
}
