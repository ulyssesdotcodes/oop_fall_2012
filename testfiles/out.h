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


  static java::lang::Class __class();

  static __Foo_VT __vtable;
};

struct __Foo_VT {

  java::lang::Class __isa;

  __Foo_VT()
  : __isa(__Foo::__class()),
{
  }
};

