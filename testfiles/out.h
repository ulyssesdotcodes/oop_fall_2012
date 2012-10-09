#pragma once
#include <stdint.h>

struct __HelloWorld;
struct __HelloWorld_VT;

typedef __HelloWorld* HelloWorld;

struct __HelloWorld {
  __HelloWorld_VT* __vptr;


  __HelloWorld();

  static Class __class();

  static HelloWorld_VT __vtable;
};

struct __HelloWorld_VT {
  Class __isa;
};
