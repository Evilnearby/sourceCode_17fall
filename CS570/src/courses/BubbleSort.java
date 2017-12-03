package courses;

import java.util.Arrays;

public class BubbleSort {
	public static int[] bbSort(int[] array) {
		int length = array.length;
		boolean unChanged = true;
		int round = 0;
		while(round < length - 1) {
			unChanged = true;
			for(int i = 0; i < (length - 1) - round; i ++) {
				if(array[i] > array[i+1]) {
					System.out.println("i:" + 1 + "round: "+round);
					int tmp = array[i];
					array[i] = array[i + 1];
					array[i + 1] = tmp;
					unChanged = false;
				}
			}
			round ++;
			if(unChanged) {
				return array;
			}
		}
		return array;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] array = {9,1,5,8,3,6,7,0,-2,11};
		System.out.println(Arrays.toString(bbSort(array)));
	}

}
