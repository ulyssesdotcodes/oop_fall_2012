package qimpp.tests;

public class TestMultidimensionalArrays{

  public static void main ( String[] args ) {
  //array with even rows
  int[][] evenarray = new int[2][3];
  evenarray[0][0] = 0;
  evenarray[0][1] = 1;
  evenarray[0][2] = 2;
  evenarray[1][0] = 3;
  evenarray[1][1] = 4;
  evenarray[1][2] = 5;
  //should print "2, 4"
  System.out.println(evenarray[0][2] + ", " + evenarray[1][1]);
  //array with ragged edges
  char[][] raggedarray = new char[2][];
  raggedarray[0] = new char[2];
  raggedarray[0][0] = 'h';
  raggedarray[0][1] = 'i';
  raggedarray[1] = new char[3];
  raggedarray[1][0] = 'h';
  raggedarray[1][1] = 'e';
  raggedarray[1][2] = 'y';
  //should print "hi, hey"
  System.out.println(raggedarray[0][0] + raggedarray[0][1] + ", " + raggedarray[1][0] + raggedarray[1][1] + raggedarray[1][2]);
  }
  
 }
