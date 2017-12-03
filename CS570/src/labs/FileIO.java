package labs;

import java.io.*;

public class FileIO {
	//public static int countSubstring(String subStr, String text){
		
	//}
	
	public static void main(String args[]) throws IOException{
		//Read the file
		String fileName =  "text2.txt";
		String line;
		File file = new File(fileName);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		line = bufferedReader.readLine();
		bufferedReader.close();
		file.exists();
		//Count the numbers
		int len = line.length();
		int numOfWords = 0;
		int numOfSentences = 1;
		for(int i = 0; i < len; i++){
			if(line.charAt(i)==' ')
				numOfWords ++;
			if(line.charAt(i)=='.')
				numOfSentences ++;
		}
		//Create text file
		PrintWriter writer = new PrintWriter("answers.txt", "UTF-8");
	    writer.println("The number of words is: "+numOfWords);
	    writer.println("The number of sentences is: "+numOfSentences);
	    writer.close();
	}
}
