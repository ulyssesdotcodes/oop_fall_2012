
#include "FooBar.h"
#include <iostream>
#include <sstream>

//////////////
// Foo
//////////////
__Foo::__Foo() : __vptr(&__vtable) {
  zebra = __rt::literal("In the room");
}

//Foo.toString
java::lang::String __Foo::toString(Foo __this) {
  return __rt::literal("FOO");
}

//Foo.AllTheAnimals
java::lang::String __Foo::allTheAnimals(Foo __this) {
  return __this->zebra;
}

java::lang::Class __Foo::__class() {
  return new java::lang::__Class(__rt::literal("Foo"), java::lang::__Object::__class());	
}

__Foo_VT __Foo::__vtable;

/////////////////////
// Bar
/////////////////////
__Bar::__Bar() : __vptr(&__vtable) {
  elephant = __rt::literal("In the room");
  zebra = __rt::literal("Away");
}

//Bar.AllTheAnimals
java::lang::String __Bar::allTheAnimals(Bar __this) {
  return new java::lang::__String(__this->zebra->data + __this->elephant->data);
}

java::lang::Class __Bar::__class() {
  return new java::lang::__Class(__rt::literal("Bar"), __Foo::__class());	
}

__Bar_VT __Bar::__vtable;

int main() {
  
  Bar bar = new __Bar();
  std::cout << bar->__vptr->toString(bar)->data << std::endl;
  std::cout << bar->__vptr->allTheAnimals(bar)->data << std::endl;

  // Foo foo = new __Foo();
  //   std::cout << foo->__vptr->toString(foo)->data << std::endl;
  //   std::cout << foo->__vptr->allTheAnimals(foo)->data << std::endl;
}
