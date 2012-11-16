#include "out.h"
#include <iostream>
#include <sstream>

java::lang::Class __Foo::__class() {
  return new java::lang::__Class(__rt::literal("Foo"), java::lang::__Object::__class());
}

__Foo_VT __Foo::__vtable;

__Foo::__Foo() : __vptr(&__vtable)    {
    this->zebra = __rt::literal("In the room");
}


int32_t(Foo __this)java::lang::Objectbool(Foo __this, obj)java::lang::Objectjava::lang::Class(Foo __this)java::lang::Objectjava::lang::String(Foo __this) {
  return __rt::literal("FOO");
}

java::lang::String(Foo __this) {
  return __this->zebra;
}

(Foo __this, args) {
  std::cout << __rt::literal("hello");
}


