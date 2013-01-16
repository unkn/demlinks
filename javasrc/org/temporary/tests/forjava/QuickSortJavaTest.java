package org.temporary.tests.forjava;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class QuickSortJavaTest {
	
	//source: http://rosettacode.org/wiki/Sorting_algorithms/Quicksort#Java
	public static <E extends Comparable<? super E>> List<E> quickSort2(List<E> arr) {
	    if (arr.size() <= 1)
	        return arr;
	    E pivot = ((LinkedList<E>) arr).getFirst(); //This pivot can change to get faster results
	 
	    List<E> less = new LinkedList<E>();
	    List<E> pivotList = new LinkedList<E>();
	    List<E> more = new LinkedList<E>();
	 
	    // Partition
	    for (E i: arr) {
	        if (i.compareTo(pivot) < 0)
	            less.add(i);
	        else if (i.compareTo(pivot) > 0)
	            more.add(i);
	        else
	            pivotList.add(i);
	    }
	 
	    // Recursively sort sublists
	    less = quickSort2(less);
	    more = quickSort2(more);
	 
	    // Concatenate results
	    less.addAll(pivotList);
	    less.addAll(more);
	    return less;
	}
	
	//source: http://java.dzone.com/articles/benchmarking-scala-against
	public static void quickSort(int[] array, int left, int right) {
	    if (right <= left) {
	        return;
	    }
	    int pivot = array[right];
	    int p = left;
	    int i = left;
	    while (i < right) {
	        if (array[i] < pivot) {
	            if (p != i) {
	                int tmp = array[p];
	                array[p] = array[i];
	                array[i] = tmp;
	            }
	            p += 1;
	        }
	        i += 1;
	    }
	    array[right] = array[p];
	    array[p] = pivot;
	    quickSort(array, left, p - 1);
	    quickSort(array, p + 1, right);
	}
	
	public static void main(String[] args) {
		int howmany = 1000000;
		long lowrange=1;
		int[] arr=new int[howmany];
		Random r=new Random(12345678l);
		
		for (int i = 0; i < arr.length; i++) {
			arr[i]=(int) (lowrange+r.nextInt((int) (lowrange+20l*howmany)));
//			System.out.println(arr[i]);
		}
		long start=System.currentTimeMillis();
		quickSort(arr, 0, arr.length-1);
		long end=System.currentTimeMillis();
		System.out.println("elapsed shit in java: "+(end-start)+" ms");
//		for (int i = 0; i < arr.length; i++) {
//			if (i % 500 == 0 ) System.out.println(arr[i]);
//		}
		System.out.println("howmany is "+arr.length);
		//quickSort2(arr);
	}
}
