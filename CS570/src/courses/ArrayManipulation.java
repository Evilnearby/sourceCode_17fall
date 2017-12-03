package courses;

import java.util.Arrays;

public class ArrayManipulation {
	public static void function(int n) {
		if(n > 0) {
			int[] array = new int[n];
			int numEven = 0;
			int numOdd = 0;
			for(int i = 0; i < n; i++) {
				array[i] = (int)(Math.random()*100) + 20;
				if (array[i]%2 == 0)
					numEven ++;
			}
			System.out.println(Arrays.toString(array));
			numOdd = n - numEven;
			for(int i = 0; i < n; i++) {
				if(array[i]%2==1) {
					int j = i + 1;
					while(j<n) {
						if((array[j]%2 == 0)) {
							int temp = array[i];
							array[i] = array[j];
							array[j] = temp;
						}
						j++;
					}
				}
			}
			System.out.println(Arrays.toString(array));
			//int[] sortedArray = new int[n];
			System.out.println("numEven = "+numEven+"  numOdd = "+numOdd);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		function(11);
	}

}
