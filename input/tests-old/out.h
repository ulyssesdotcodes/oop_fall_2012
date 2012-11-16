#pragma once
#include "java_lang.h"
#include <stdint.h>

struct __Test2;
struct __Test2_VT;

typedef __Test2* Test2;

struct __Test2 {

  __Test2_VT* __vptr;

  __Test2();


  static Class __class();

  static Test2_VT __vtable;
};

struct __Test2_VT {

  Class __isa;

  __Test2_VT()
  : __isa(__Test2::__class()),
{
  }
};

