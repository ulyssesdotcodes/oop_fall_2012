#pragma once
#include "java_lang.h"
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
  int32_t (*hashCode)(HelloWorld);
  bool (*equals)(HelloWorld, Object);
  Class (*getClass)(HelloWorld);
  String (*toString)(HelloWorld);



  __HelloWorld_VT()
    : __isa(__HelloWorld::__class()),
    hashCode((int32_t(*)(HelloWorld))&__Object::hashCode),
    equals((bool(*)(HelloWorld,Object))&__Object::equals),
    getClass((Class(*)(HelloWorld))&__Object::getClass),
    toString((String(*)(HelloWorld))&__Object::toString)


 {
  }
};

