package main;

public class View {
	private Game game;
	public View(Game game) {
		this.game = game;
	}
	
	public void printTheBoard() {
		//char[][] toPrint = this.game.board;
		int bound = this.game.size;
		for(int i= 0;i<2*bound;i++){
			for(int j= 0;j<2*bound;j++) {
				System.out.print(this.generateCellString(i,j));
			}
		}
	}
	
	private String generateCellString(int i, int j) {
		char[][] toPrint = this.game.board;
		String cell = "";
		//Bound
		if(i==0 || j==0){
			//1. j==0&&i==odd --> j/2
			//2. j==0&&i==even --> "   "
			if(j==0) {	//j is in bound
				if(i%2==1)	//i is odd
					cell = digitInCenter(i/2);
				else if(i == 0)
					cell = "   ";
				else		//i is even
					cell = "   ";
			}
			//3. i==0&&j==odd --> i/2
			//4. i==0&&j==even --> " "
			else if(i==0) {
				if(j%2==1)
					cell = digitInCenter(j/2);
				else
					cell = " ";
			}
		//Out bound
		}else {	// i!=0&&j!=0, non-bounded cells
			if(i%2 == 0){
				if(j%2 !=0)
					cell = "---";
				else
					cell ="+";
			}else {
				if(j%2==0)
					cell = "|";
				else {
					char chCell = toPrint[i/2][j/2];
					if(chCell == 0)
						cell = "   ";
					else
						cell = " " + String.valueOf(chCell) + " ";
				}
			}	
		}
		if(j==2*this.game.size-1)
			cell = cell +"\n";
		return cell;
	}
	
	private static String digitInCenter(int num) {
		String answer = "";
		if(num>=100) {	//three digits
			answer = String.valueOf(num);
			return answer;
		}
		else if(num>=10) {
			answer = " "+String.valueOf(num);
			return answer;
		}else {
			answer = " " +String.valueOf(num) + " ";
			return answer;
		}
	}
}
