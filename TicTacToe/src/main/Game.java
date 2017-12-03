package main;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Game {
	public char[][] board;
	public ArrayList<Player> players = new ArrayList<Player>();
	int numberOfPlayers;
	int size;
	int winSequence;
	public View myView;
	public Game mySaveSystem;
	public int firstPlayer;
	public boolean ifLoad = false;
	//Interface for resumed game
	public Game(int i) {
		
	}
	//Constructor
	public Game() throws Exception {
		//Prompt if load a saved game
		System.out.println("Do you want to load a saved game? Please type Y/N");
		Scanner in = new Scanner(System.in);
		String get = in.nextLine();
		if(get.equals("Y")) {
			System.out.println("Please input the saved file's name, without suffix: '.txt'");
			String fileName = in.nextLine();
			fileName += ".txt";
			this.mySaveSystem = new ResumedGame(fileName);
			this.ifLoad = true;
			//mySaveSystem.startGame();
		} else {
			//Prompt to ask the number of players， size of the board and the win sequence
			//TODO: need to check if the input values are validated
			System.out.println("Please input the number of players. Max 26 players");
			this.numberOfPlayers = Integer.parseInt(in.nextLine());
			System.out.println("Please input the size of the board's length. Max 999 cells");
			this.size = Integer.parseInt(in.nextLine());
			System.out.println("Please input the winning sequence. Max 999 cells");
			this.winSequence = Integer.parseInt(in.nextLine());
			//Initialization of the game
			for(int i = 0; i < this.numberOfPlayers; i++) {
				this.players.add(new Player(i,this));
			}
			board = new char[size][size];
			this.ifGameValid();
			this.firstPlayer = 0;
			this.myView = new View(this);
			this.mySaveSystem = null;
		}
	}
	
	//Test if the newly created game is valid
	public void ifGameValid() throws Exception {
		Exception thisExcept = new Exception();
		if(this.winSequence>this.size) {
			throw new Exception("The definition of the game is invalid, win sequence bigger than size");
		}else if(this.numberOfPlayers<2) {
			throw new Exception("You should have at least 2 players.");
		}else if(this.numberOfPlayers>(this.size*this.size - this.winSequence + 1) || this.numberOfPlayers > 26) {
			throw new Exception("The definition is invalid. Too much players.");
		}else if(this.winSequence > 999) {
			throw new Exception("The definition is invalid. winSequence is bigger than 999.");
		}else if(this.size > 999) {
			throw new Exception("The definition is invalid. Size is too big.");
		}
	}

	//Test if the game is won
	public boolean ifWon(int row, int col, char sym) {
		int wLine = this.winSequence - 1;
		int currRow1 = row - wLine;
		int currCol = col - wLine;
		int currRow2= row + wLine;
		int count1 = 0; int count2 = 0; int count3 = 0; int count4 = 0;
		for(int i = 0; i < 2*this.winSequence-1; i++) {
			//line1: diagonal commence by left-top
			int result1 = this.ifMet(currRow1 + i, currCol + i, sym);
			if(result1 == 1) {
				count1 ++;
				if(count1 >= this.winSequence) {
					//System.out.println("Count1");
					return true;
				}
			}else if(result1 == 0){
				count1 = 0;
			}
			//line2: horizonal commencing by left
			int result2 = this.ifMet(row, currCol + i, sym);
			if(result2 == 1) {
				count2 ++;
				if(count2 >= this.winSequence) {
					//System.out.println("Count2");
					return true;
				}
			}else if(result2 == 0){
				count2 = 0;
			}
			//line3: diagonal commencing by left-bottom
			int result3 = this.ifMet(currRow2 - i, currCol + i, sym);
			if(result3 == 1) {
				count3 ++;
				if(count3 >= this.winSequence) {
					//System.out.println("Count3");
					return true;
				}
			}else if(result3 == 0){
				count3 = 0;
			}
			//line4: vertical commencing by top
			int result4 = this.ifMet(currRow1 + i, col, sym);
			if(result4 == 1) {
				count4 ++;
				if(count4 >= this.winSequence) {
					//System.out.println("Count4");
					return true;
				}
			}else if(result4 == 0){
				count4 = 0;
			}
		}
		return false;
	}
	//Check if the current chess met is equal to the sym
	public int ifMet(int r, int c, char sym) {
		int limit = this.size - 1;
		if( r>=0 && r<=limit && c >= 0 && c<=limit) {
			if(this.board[r][c] == sym){
				//System.out.println("met: "+this.board[r][c]+" "+sym+ "coord: "+r+c);
				return 1;	//met
			}else {
				return 0;	//not met
			}
		}else 
			return -1;		//out of bound
	}
	//Start the game
	public void startGame() throws FileNotFoundException, UnsupportedEncodingException{
		int countChess = 0;
		int total = (this.size)^2;
		Player currPlayer;
		int index = this.firstPlayer;
		System.out.println("During every player's turn, "
				+ "Please input the row & colulm number to draw a chess, separated by space.");
		System.out.println("If you want to save and quit the game, input 'Save' instead of the 2 numbers in your turn.");
		System.out.println("If you want to quit without saving, input 'Q'.\n");
		while (countChess <= total) {
			currPlayer = this.players.get(index);
			this.myView.printTheBoard();
			int[] cood = currPlayer.promptPosition();
			if(cood[0]==-1){
				ResumedGame.saveGame(index, this);
				return;
			} else if(cood[0]==-2) {
				System.out.println("You chose to quit the game without saving.");
				return;
			}
			char tmpCh = this.board[cood[0]][cood[1]];
			while(tmpCh != 0) {
				System.out.println("There is already one chess at this spot, please retry another one.");
				cood = currPlayer.promptPosition();
				if(cood[0]==-1){
					ResumedGame.saveGame(index, this);
					return;
				}
				tmpCh = this.board[cood[0]][cood[1]];
			}
			this.board[cood[0]][cood[1]] = currPlayer.symblol;
			if(this.ifWon(cood[0], cood[1], currPlayer.symblol)){
				this.myView.printTheBoard();
				System.out.println("WinnerWinner ChickenDinner!\nPlayer: "
						+currPlayer.index+" with symbol "+currPlayer.symblol+" has won!");
				return;
			}
			index = (index + 1)%this.numberOfPlayers;
		}
		if(countChess == total)
			System.out.println("Draw");
	}
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Game newGame = new Game();
		if(newGame.ifLoad)
			newGame.mySaveSystem.startGame();
		else 
			newGame.startGame();
	}
	
}
