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
#include <iostream>

#include "ptr.h"

// ==========================================================================

// To avoid the "static initialization order fiasco", we use functions
// instead of fields/variables for all pointer values that are statically
// initialized.

// See http://www.parashift.com/c++-faq-lite/ctors.html#faq-10.14.

// ==========================================================================

namespace java {
  namespace lang {

    // Forward declarations of data layout and vtables.
    struct __Object;
    struct __Object_VT;

    struct __String;
    struct __String_VT;

    struct __Class;
    struct __Class_VT;

    // Definition of type names, which are equivalent to Java semantics,
    // i.e., a smart pointer to a data layout.
    typedef __rt::Ptr<__Object> Object;
    typedef __rt::Ptr<__Class> Class;
    typedef __rt::Ptr<__String> String;
  }
}

// ==========================================================================

namespace __rt {

  // The function returning the canonical null value.
  java::lang::Object null();

  // The template function for the virtual destructor.
  template <typename T>
  void __delete(T* addr) {
    delete addr;
  }

}

// ==========================================================================

namespace java {
  namespace lang {

    // The data layout for java.lang.Object.
    struct __Object {
      __Object_VT* __vptr;

      // The constructor.
      __Object();

      // The methods implemented by java.lang.Object.
      static int32_t hashCode(Object);
      static bool equals(Object, Object);
      static Class getClass(Object);
      static String toString(Object);

      // The function returning the class object representing
      // java.lang.Object.
      static Class __class();

      // The vtable for java.lang.Object.
      static __Object_VT __vtable;
    };

    // The vtable layout for java.lang.Object.
    struct __Object_VT {
      Class __isa;
      void (*__delete)(__Object*);
      int32_t (*hashCode)(Object);
      bool (*equals)(Object, Object);
      Class (*getClass)(Object);
      String (*toString)(Object);

      __Object_VT()
      : __isa(__Object::__class()),
        __delete(&__rt::__delete<__Object>),
        hashCode(&__Object::hashCode),
        equals(&__Object::equals),
        getClass(&__Object::getClass),
        toString(&__Object::toString) {
      }
    };

    // ======================================================================

    // The data layout for java.lang.String.
    struct __String {
      __String_VT* __vptr;
      std::string data;

      // The constructor;
      __String(std::string data);

      // The methods implemented by java.lang.String.
      static int32_t hashCode(String);
      static bool equals(String, Object);
      static String toString(String);
      static int32_t length(String);
      static char charAt(String, int32_t);

      // The function returning the class object representing
      // java.lang.String.
      static Class __class();

      // The vtable for java.lang.String.
      static __String_VT __vtable;
    };

    std::ostream& operator<<(std::ostream& out, String);

    // The vtable layout for java.lang.String.
    struct __String_VT {
      Class __isa;
      void (*__delete)(__String*);
      int32_t (*hashCode)(String);
      bool (*equals)(String, Object);
      Class (*getClass)(String);
      String (*toString)(String);
      int32_t (*length)(String);
      char (*charAt)(String, int32_t);
      
      __String_VT()
      : __isa(__String::__class()),
        __delete(&__rt::__delete<__String>),
        hashCode(&__String::hashCode),
        equals(&__String::equals),
        getClass((Class(*)(String))&__Object::getClass),
        toString(&__String::toString),
        length(&__String::length),
        charAt(&__String::charAt) {
      }
    };

    // ======================================================================

    // The data layout for java.lang.Class.
    struct __Class {
      __Class_VT* __vptr;
      String name;
      Class parent;
      Class component;
      bool primitive;

      // The constructor.
      __Class(String name,
              Class parent,
              Class component = __rt::null(),
              bool primitive = false);

      // The instance methods of java.lang.Class.
      static String toString(Class);
      static String getName(Class);
      static Class getSuperclass(Class);
      static bool isPrimitive(Class);
      static bool isArray(Class);
      static Class getComponentType(Class);
      static bool isInstance(Class, Object);

      // The function returning the class object representing
      // java.lang.Class.
      static Class __class();

      // The vtable for java.lang.Class.
      static __Class_VT __vtable;
    };

    // The vtable layout for java.lang.Class.
    struct __Class_VT {
      Class __isa;
      void (*__delete)(__Class*);
      int32_t (*hashCode)(Class);
      bool (*equals)(Class, Object);
      Class (*getClass)(Class);
      String (*toString)(Class);
      String (*getName)(Class);
      Class (*getSuperclass)(Class);
      bool (*isPrimitive)(Class);
      bool (*isArray)(Class);
      Class (*getComponentType)(Class);
      bool (*isInstance)(Class, Object);

      __Class_VT()
      : __isa(__Class::__class()),
        __delete(&__rt::__delete<__Class>),
        hashCode((int32_t(*)(Class))&__Object::hashCode),
        equals((bool(*)(Class,Object))&__Object::equals),
        getClass((Class(*)(Class))&__Object::getClass),
        toString(&__Class::toString),
        getName(&__Class::getName),
        getSuperclass(&__Class::getSuperclass),
        isPrimitive(&__Class::isPrimitive),
        isArray(&__Class::isArray),
        getComponentType(&__Class::getComponentType),
        isInstance(&__Class::isInstance) {
      }
    };

    // ======================================================================

    // The completey incomplete data layout for java.lang.Integer.
    struct __Integer {

      // The class instance representing the primitive type int.
      static Class TYPE();

    };

    // ======================================================================

    // For simplicity, we use C++ inheritance for exceptions and throw
    // them by value.  In other words, the translator does not support
    // user-defined exceptions and simply relies on a few built-in
    // classes.
    class Throwable {
    };

    class Exception : public Throwable {
    };

    class RuntimeException : public Exception {
    };

    class NullPointerException : public RuntimeException {
    };

    class NegativeArraySizeException : public RuntimeException {
    };

    class ArrayStoreException : public RuntimeException {
    };

    class ClassCastException : public RuntimeException {
    };

    class IndexOutOfBoundsException : public RuntimeException {
    };

    class ArrayIndexOutOfBoundsException : public IndexOutOfBoundsException {
    };
    
  }
}

// ==========================================================================

namespace __rt {

  // Forward declarations of data layout and vtable.
  template <typename T>
  struct Array;

  template <typename T>
  struct Array_VT;

  // The data layout for arrays.
  template <typename T>
  struct Array {
    Array_VT<T>* __vptr;
    const int32_t length;
    T* __data;

    // The constructor (defined inline).
    Array(const int32_t length)
    : __vptr(&__vtable), length(length), __data(new T[length]()) {
    }

    // The destructor.
    static void __delete(Array* addr) {
      delete[] addr->__data;
      delete addr;
    }

    // Array access.
    T& operator[](int32_t index) {
      if (0 > index || index >= length) {
        throw java::lang::ArrayIndexOutOfBoundsException();
      }
      return __data[index];
    }

    const T& operator[](int32_t index) const {
      if (0 > index || index >= length) {
        throw java::lang::ArrayIndexOutOfBoundsException();
      }
      return __data[index];
    }

    // The function returning the class object representing the array.
    static java::lang::Class __class();

    // The vtable for the array.
    static Array_VT<T> __vtable;
  };

  // The vtable for arrays.
  template <typename T>
  struct Array_VT {
    typedef Ptr<Array<T> > Reference;

    java::lang::Class __isa;
    void (*__delete)(Array<T>*);
    int32_t (*hashCode)(Reference);
    bool (*equals)(Reference, java::lang::Object);
    java::lang::Class (*getClass)(Reference);
    java::lang::String (*toString)(Reference);
    
    Array_VT()
    : __isa(Array<T>::__class()),
      __delete(&Array<T>::__delete),
      hashCode((int32_t(*)(Reference))
               &java::lang::__Object::hashCode),
      equals((bool(*)(Reference,java::lang::Object))
             &java::lang::__Object::equals),
      getClass((java::lang::Class(*)(Reference))
               &java::lang::__Object::getClass),
      toString((java::lang::String(*)(Reference))
               &java::lang::__Object::toString) {
    }
  };

  // The vtable for arrays.  Note that this definition uses the default
  // no-arg constructor.
  template <typename T>
  Array_VT<T> Array<T>::__vtable;

  // But where is the definition of __class()???

  // ========================================================================

  // Function for converting a C string lieral to a translated
  // Java string.
  inline java::lang::String literal(const char * s) {
    // C++ implicitly converts the C string to a std::string.
    return new java::lang::__String(s);
  }

  // ========================================================================

  // Template function to check against null values.
  template <typename T>
  void checkNotNull(T o) {
    if (null() == o) {
      throw java::lang::NullPointerException();
    }
  }

  // Template function to check array stores.
  template <typename T, typename U>
  void checkStore(Ptr<Array<T> > array, U object) {
    if (null() != object) {
      java::lang::Class t1 = array->__vptr->getClass(array);
      java::lang::Class t2 = t1->__vptr->getComponentType(t1);

      if (! t2->__vptr->isInstance(t2, object)) {
        throw java::lang::ArrayStoreException();
      }
    }
  }

  // Template function for translated Java casts.
  template <typename T, typename U>
  T java_cast(U object) {
    java::lang::Class k = T::value_t::__class();
    
    if (! k->__vptr->isInstance(k, object)) {
      throw java::lang::ClassCastException();
    }

    return T(object);
  }

}
