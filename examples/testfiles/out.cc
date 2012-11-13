#include "out.h"
#include <iostream>
#include <sstream>

java::lang::Class __Foo::__class() {
  return new java::lang::__Class(__rt::literal("Foo"), java::lang::Object::__class());
}

__Foo_VT __Foo::__vtable;

__Foo::__Foo() : __vptr(&__vtable) {
   {
    zebra = __rt::literal("In the room");
}

}

java::lang::String __Foo::toString(Foo __this) {
  return __rt::literal("FOO");
}

java::lang::String __Foo::allTheAnimals(Foo __this) {
  return zebra;
}

 main(int argc, char** argv) {
  System__rt::literal("hello")}


