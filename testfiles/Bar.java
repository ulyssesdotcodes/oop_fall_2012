public class Bar extends Foo {
  public String elephant;
          
  public Bar(){
    super();
    elephant = "In the room";
    this.zebra = "Away";
  }
            
  public String AllTheAnimals(){
    return zebra + elephant;
  }

  public static void main (String[] args) {
    Bar bar = new Bar();
    System.out.println(bar.toString());
    System.out.println(bar.AllTheAnimals());
  }
}
