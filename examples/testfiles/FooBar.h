#pragma once
#include "java_lang.h"
#include <stdint.h>

struct __Foo;
struct __Foo_VT;

struct __Bar;
struct __Bar_VT;

typedef __Foo* Foo;
typedef __Bar* Bar;

////////////////
// FOO
////////////////

struct __Foo {
  // Declare the Foo vtable pointer
  __Foo_VT* __vptr;
  java::lang::String zebra;
 
  // Call the constructor for the Foo object
  __Foo();
  
  // The methods implemented by Foo
  static java::lang::String toString(Foo);
  static java::lang::String allTheAnimals(Foo);

  // getClass method
  static java::lang::Class __class();

  // method returning Foo object's vtable
  static __Foo_VT __vtable;
};

struct __Foo_VT {
  // vtable method declarations
  java::lang::Class __isa;
  int32_t (*hashCode)(Foo);
  bool (*equals)(Foo, java::lang::Object);
  java::lang::Class (*getClass)(Foo);
  java::lang::String (*toString)(Foo);
  java::lang::String (*allTheAnimals)(Foo);

  // vtable constructor
  __Foo_VT()
    : __isa(__Foo::__class()),
    hashCode((int32_t(*)(Foo))&java::lang::__Object::hashCode),
    equals((bool(*)(Foo,java::lang::Object))&java::lang::__Object::equals),
    getClass((java::lang::Class(*)(Foo))&java::lang::__Object::getClass),
    toString(&__Foo::toString),
    allTheAnimals(&__Foo::allTheAnimals)
    {
    }
};

////////////////
// BAR
////////////////

struct __Bar {
  // Declare the Bar vtable pointer.
  __Bar_VT* __vptr;
  java::lang::String zebra;
  java::lang::String elephant;
  
  // Call the constructor for the Bar object.
  __Bar();
    
  // The methods implemented by Bar.

  static java::lang::String allTheAnimals(Bar);

  // getClass method returning the class of the object
  static java::lang::Class __class();

  // method returning Bar object's vtable
  static __Bar_VT __vtable;
};

struct __Bar_VT {
  // vtable method declarations
  java::lang::Class __isa;
  int32_t (*hashCode)(Bar);
  bool (*equals)(Bar, java::lang::Object);
  java::lang::Class (*getClass)(Bar);
  java::lang::String (*toString)(Bar);
  java::lang::String (*allTheAnimals)(Bar);

  // vtable constructor
  __Bar_VT()
    : __isa(__Bar::__class()),
    hashCode((int32_t(*)(Bar))&java::lang::__Object::hashCode),
    equals((bool(*)(Bar,java::lang::Object))&java::lang::__Object::equals),
    getClass((java::lang::Class(*)(Bar))&java::lang::__Object::getClass),
    toString((java::lang::String(*)(Bar))&__Foo::toString),
    allTheAnimals(&__Bar::allTheAnimals)
    {
    }
};