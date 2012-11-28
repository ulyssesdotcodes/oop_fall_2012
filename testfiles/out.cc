#include "out.h"
#include <iostream>
#include <sstream>

java::lang::Class __Bar::__class() {
  return new java::lang::__Class(__rt::literal("Bar"), __Foo::__class());
}

__Bar_VT __Bar::__vtable;

__Bar::__Bar() : __vptr(&__vtable)    {
    ();
this->elephant = __rt::literal("In the room");
 = __rt::literal("Away");
}


java::lang::String __Bar::allTheAnimals(Bar __this) {
  return __this->zebra + __this->elephant;
}

 main(int argc, char** argv) {
    __Bar bar = __Bar();
std::cout << (()) << std::endl ;
std::cout << (()) << std::endl ;
}


java::lang::Class __Foo::__class() {
  return new java::lang::__Class(__rt::literal("Foo"), java::lang::__Object::__class());
}

__Foo_VT __Foo::__vtable;

__Foo::__Foo() : __vptr(&__vtable)    {
    this->zebra = __rt::literal("In the room");
}


java::lang::String __Foo::toString(Foo __this) {
  return __rt::literal("FOO");
}

java::lang::String __Foo::allTheAnimals(Foo __this) {
  return __this->zebra;
}

 main(int argc, char** argv) {
  std::cout << (__rt::literal("hello"));
}


