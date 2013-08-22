//compile and run with:
//javac Driver.java
//java Driver

import java.util.Date;

public class Driver {

public static void main (String[] args) {
int sz = 100000;
int[] arr1 = new int[sz];
int[] arr2 = new int[sz];
for (int i=0; i<sz; i++) {
arr1[i] = i;
arr2[i] = i*2;
}
Date start = new Date();
long num = 0;
for (int i=0; i<sz; i++) {
int n = arr1[i];
for (int j=0; j<sz; j++) {
num += n * arr2[j];
}
}
Date end = new Date();
long l = end.getTime() - start.getTime();
System.out.println(l + " ms.");
System.out.println(num);
}
}