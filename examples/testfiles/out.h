#pragma once
#include "java_lang.h"
#include <stdint.h>

struct __Foo;
struct __Foo_VT;

typedef __Foo* Foo;

struct __Foo {

  __Foo_VT* __vptr;
  java::lang::String zebra;

  __Foo();

  static java::lang::String toString(Foo);
  static java::lang::String allTheAnimals(Foo);

  static java::lang::Class __class();

  static __Foo_VT __vtable;
};

struct __Foo_VT {

  java::lang::Class __isa;
  int32_t (*hashCode)(Foo);
  bool (*equals)(Foo, java::lang::Object);
  java::lang::Class (*getClass)(Foo);
  java::lang::String (*toString)(Foo);
  java::lang::String (*allTheAnimals)(Foo);

  __Foo_VT()
  : __isa(__Foo::__class()),
    hashCode((int32_t(*)(Foo))&java::lang::__Object::hashCode),
    equals((bool(*)(Foo, java::lang::Object))&java::lang::__Object::equals),
    getClass((java::lang::Class(*)(Foo))&java::lang::__Object::getClass),
    toString(&__Foo::toString),
    allTheAnimals(&__Foo::allTheAnimals){
  }
};

