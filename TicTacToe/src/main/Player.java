package main;

import java.util.Scanner;

public class Player {
	public char symblol;
	public Game myGame;
	public int index;
	private static String alphabetSymbol = "XOABCDEFGHIJKLMNPQRSTUVWYZ";
	//Constructor
	public Player(int index, Game g) {
		this.index = index;
		this.symblol = alphabetSymbol.charAt(index);
		this.myGame = g;
	}
	
	public int[] promptPosition() {
		int[] position = new int[2];
		System.out.println("Turn of player: "+this.index+ " with symbol: "
				+this.symblol+"\nPlease type your choice. Option 1: 'row# column#', Option 2: 'Save' to save, Option 3: 'Q' to quit directly.");
		Scanner in = new Scanner(System.in);
		String str = in.nextLine();
		if(str.equals("Save")){
			position[0] = -1;
			return position;
		}
		if(str.equals("Q")){
			position[0] = -2;
			return position;
		}
		int indexSp = str.indexOf(" ");
		int row = Integer.parseInt(str.substring(0, indexSp));
		int col = Integer.parseInt(str.substring(indexSp+1));
		position[0] = row;
		position[1] = col;
		return position;
	}

}
