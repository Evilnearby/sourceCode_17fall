package labs;

import java.io.*;

/**Copyright by Group_C8_Shilun_Leyu_Yuhao
*Shilun LI 10431554
*Leyu LIU 10430967
*Yuhao LI 10430378
*/
public class CaesarCipherDecrytor {
	
	public static String decrytp() throws IOException{
		//Read the file
		String fileName =  "Code.txt";
		String line;
		File file = new File(fileName);
		System.out.println("File with name: "+fileName+" is going to be read.");
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		line = bufferedReader.readLine();
		bufferedReader.close();
		//Decryption
		String answer ="";
		int length = line.length();
		int key = 5;
		int shift = key;
		for(int i = 0; i <length; i++){
			Character currChar = line.charAt(i);
			shift = (key + ((i)/3)*2)%26;
			if(isLetter(currChar)==2){
				int index = currChar - 65;
				if((index-shift)<0)
					index += 26;
				currChar = (char)(65+index-shift);
			}else if(isLetter(currChar)==1){
				int index = currChar - 97;
				if((index-shift)<0)
					index += 26;
			currChar = (char)(97+index-shift);
			}
			answer += currChar;
		}
		return answer;
	}
	
	public static int isLetter(Character ch){
		int hsCode = ch.hashCode();
		if(((hsCode>=65)&&(hsCode<=90)))
			return 2;	//Upper case
		else if((hsCode>=97)&&(hsCode<=122))
			return 1;	//Lower case
		else
			return 0;	//Non letter

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//System.out.println('{' - 'a');
		String solution = decrytp();
		PrintWriter writer = new PrintWriter("solution.txt", "UTF-8");
	    writer.println(solution);
	    writer.close();

	}

}
