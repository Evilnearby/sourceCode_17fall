package main;

import java.io.*;
import java.util.Scanner;

import javax.xml.stream.events.Characters;

public class ResumedGame extends Game{
	
	public ResumedGame(String fileName) throws Exception {
		// TODO Auto-generated constructor stub
		super(1);
		String strBoard = "";
		int nbOfPlayers = 0;
		int size = 0;
		int winSequence = 0;
		int firstPlayer = 0;
		File file = new File(fileName);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		strBoard = bufferedReader.readLine();
		nbOfPlayers = Integer.parseInt(bufferedReader.readLine());
		size = Integer.parseInt(bufferedReader.readLine());
		winSequence = Integer.parseInt(bufferedReader.readLine());
		firstPlayer = Integer.parseInt(bufferedReader.readLine());
		bufferedReader.close();
		char[][] board = new char[size][size];
		int index = 0;
		for(int i = 0; i< size; i ++) {
			for(int j = 0; j < size; j++) {
				if(strBoard.charAt(index) != '0')
					board[i][j] = strBoard.charAt(index);
				index ++;
			}
		}
		//Constructing the attributes
		this.board = board;
		this.numberOfPlayers = nbOfPlayers;
		this.size = size;
		this.winSequence = winSequence;
		this.firstPlayer = firstPlayer;
		this.myView = new View(this);
		this.players.clear();
		for(int i = 0; i < this.numberOfPlayers; i++) {
			this.players.add(new Player(i,this));
		}
	}
	
	/**public ResumedGame loadGame() throws IOException {
		String fileName =  "text2.txt";
		String line;
		File file = new File(fileName);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		line = bufferedReader.readLine();
		bufferedReader.close();
	}*/
	
	public static void saveGame(int firstPlayer, Game game) throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("Please input the name of the text file you want to load.");
		System.out.println("For e.g., if you input 'text', the file to be loaded would be text.txt");
		Scanner in = new Scanner(System.in);
		String fileName = in.nextLine();
		fileName += ".txt";
		//Retrieve the parameters
		char[][] board = game.board;
		String strBoard = "";
		int size = game.size;
		int nbOfPlayers = game.numberOfPlayers;
		int winSequence = game.winSequence;
		for(int i = 0; i<size; i++) {
			for(int j = 0; j < size; j++) {
				if(board[i][j]==0)
					strBoard += "0";
				else
					strBoard += board[i][j];
			}
		}
		
		//game.savedFileName = fileName;
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
	    writer.println(strBoard);
	    writer.println(nbOfPlayers);
	    writer.println(size);
	    writer.println(winSequence);
	    writer.println(firstPlayer);
	    writer.close();
	    
	    System.out.println("Your game has been successfully saved in file with name: "+fileName+".");
	}
}
