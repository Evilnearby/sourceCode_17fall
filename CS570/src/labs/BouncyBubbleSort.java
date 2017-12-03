package labs;

import java.util.Arrays;

public abstract class BouncyBubbleSort {
	public static int[] bbSort(int[] array) {
		int length = array.length;
		boolean unChanged = true;
		int right= 0;
		int left = 0;
		int index = 0;
		while(right + left < length -1) {
			unChanged = true;
			//odd right, backward direction
			if(index%2 == 1) {
				for(int i = length - 1 - right; i > left; i--) {
					if(array[i] < array[i-1]) {
						int tmp = array[i];
						array[i] = array[i - 1];
						array[i - 1] = tmp;
						unChanged = false;
					}
				}
				System.out.print("\n");
				System.out.println(Arrays.toString(array));
				left ++;
			} else if(index % 2 == 0){
				//even right, forward direction
				for(int i = left; i < (length - 1) - right; i++) {
					if(array[i] > array[i+1]) {
						int tmp = array[i];
						array[i] = array[i + 1];
						array[i + 1] = tmp;
						unChanged = false;
					}
				}
				System.out.print("\n");
				System.out.println(Arrays.toString(array));
				right ++;
			}
			index++;
			if(unChanged) {
				return array;
			}
		}
		return array;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] array = {9,8,7,6,5,46,5,6,-11,-10,-12};
		System.out.println(Arrays.toString(bbSort(array)));
	}

}
