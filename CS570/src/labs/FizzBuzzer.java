package labs;

public class FizzBuzzer {
	public static void FizzBuzzer(int[] array) {
		int i = 0;
		while(i<array.length) {
			if(array[i]%15 == 0) {
				System.out.println("FizzBuzz");
			}
			else if(array[i]%5 == 0) {
				System.out.println("Fizz");
			}
			else if(array[i]%3 == 0) {
				System.out.println("Buzz");
			}
			else {
				System.out.println(array[i]);
			}
			i++;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] myArray = new int[241];
		for(int i = 10; i <= 250; i ++) {
			myArray[i-10] = i;
		}
		FizzBuzzer(myArray);
	}
}
