//this code is from here: https://groups.google.com/d/msg/golang-nuts/ZknonZHL6CU/Yz0NGZij7IcJ
//to run:
//javac j.java
//java J

public class J {

  static final int sz = 100000;
  static int[] arr1 = new int[sz];
  static int[] arr2 = new int[sz];
  // Run with -XX:CompileThreshold=numWarmUpTimes
  static final int numWarmupTimes = 1;
  static final int numRunTimes = 2;

  public static long operation() {
    long num = 0L;
    for (int i=0; i < sz; i++) {
      int n = arr1[i];
      for (int j=0; j < sz; j++) {
        num += n * arr2[j];
      }
    }
    return num;
  }

  public static void main (String[] args) {

    for (int i=0; i < sz; i++) {
      arr1[i] = i;
      arr2[i] = i*2;
    }


    long num = 0L;
    long total = 0L;

    System.out.println("\nWarming up:\n----------------------\n");
    for (int i = 0; i < numWarmupTimes ; i++) {
        num = operation();
        System.out.println("\n Run num " + i + " Num is " + num);
    }

    num = 0;

    System.out.println("\nReal runs:\n----------------------\n");
    for (int i=0; i < numRunTimes; i++) {
      long startTime = System.nanoTime();
      num = operation();
      long duration = (System.nanoTime() - startTime);
      total += duration;
      System.out.println("\nRun " + (i+1) + " took : " + duration + " nanos.");
      System.out.println(num);
    }

    System.out.println("Average run time is " + (total/numRunTimes));
  }
}