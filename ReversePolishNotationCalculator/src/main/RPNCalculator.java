package main;

import java.util.*;

public class RPNCalculator {
	
	private static int comparePrecedence(char c1, char c2) {
		Character[] precedences = {'^', '%', '*', '/', '+', '-',}; 
		List<Character> list = Arrays.asList(precedences);
		int index1 = list.indexOf(c1);
		int index2 = list.indexOf(c2);
		if(index1>=index2) {
			return 0;
		} else {
			return 1;
		}
	}
	
	public static int postFixCalculator(Queue<Object> postQ) {
		Stack<Integer> stack = new Stack<>();
		Object t;
		int prevN, nextN, answer;
		while(!postQ.isEmpty()) {
			t = postQ.poll();
			if(t instanceof Integer) {
				int tmpI = (Integer)t;
				stack.push(tmpI);
			} else if(t instanceof Character) {
				char tmpC = (Character)t;
				prevN = stack.pop();
				nextN = stack.pop();
				switch(tmpC) {
					case '+': answer = nextN + prevN; break;
					case '-': answer = nextN - prevN; break;
					case '*': answer = nextN * prevN; break;
					case '/': answer = nextN / prevN; break;
					case '%': answer = nextN % prevN; break;
					case '^': answer = (int)Math.pow((double)nextN, (double)prevN); break;
					default: answer = 0;
							System.out.println("Invalid Operations"); break;
				}
				stack.push(answer);
			}
		}
		return stack.peek();
	}
	
	public static Queue<Object> infixToPostfix(Queue<Object> infixQ) {
		Stack<Character> opStack = new Stack<>();
		Queue<Object> postfixQ = new LinkedList<>();
		Object t;
		while(!infixQ.isEmpty()) {
			t = infixQ.poll();
			if(t instanceof Integer) {
				postfixQ.offer(t);	//Integer
			} else if(opStack.isEmpty()) {
				opStack.push((Character)t);
			} else if(t instanceof Character && (Character)t == '(') {
				opStack.push((Character)t);
			} else if(t instanceof Character && (Character)t == ')') {
				while(opStack.peek() != '(') {
					char tmpC = opStack.pop();
					postfixQ.offer(tmpC);	//Character
				}
				opStack.pop();	//discard a left paren from stack
			} else {
				while(!opStack.isEmpty() && opStack.peek() != '(' && 
						comparePrecedence((Character)t, opStack.peek()) == 0) {
					postfixQ.offer(opStack.pop());	//Character
				}
				opStack.push((char)t);
			}
		}
		// Now there are no tokens left in infixQ, so transfer remaining operators.
		while(!opStack.isEmpty()) {
			postfixQ.offer(opStack.pop());
		}
		return postfixQ;
	}
	
	private static Queue<Object> queueMaker(String dataSource) {
		String data = dataSource.trim();
		Queue<Object> results = new LinkedList<>();
		int length = data.length();
		int index = 0;
		String num = "";
		while(index < length) {
			char curr = data.charAt(index);
			if(curr == ' ') {
				index ++;
				continue;
			}
			if(Character.isDigit(curr)) {
				num = num + curr;
			} else {
				if(!num.equals("")) {
					results.offer((Object)(Integer.parseInt(num)));	//offer number as integer
					num = "";
				}
				if(curr == 'P') {
					if(data.charAt(index + 1) == 'O' && data.charAt(index + 2) == 'W') {
						results.offer('^');
						index += 2;
					} else {
						System.out.println("You have typed a wrong operand, here should throw an exception. It doesn't cuz I'm lazy");
					}
				} else {
					results.offer((Object)curr);		//offer char
				}
			}
			index ++;
		}
		if(!num.equals("")) {
			results.offer((Object)(Integer.parseInt(num)));
		}
		return results;
	}
	
	public static String console() throws Exception {
		System.out.println("Please input an infix expression. Type in 'quit' to quit the program. You may separate them with or without spaces.");
		Scanner sc = new Scanner(System.in);
		String infix = sc.nextLine();
		isValid(infix);
		if(infix.equals("quit")) {
			return "quit";
		}
		Queue<Object> infixQueue = queueMaker(infix);
		System.out.print("The infix expression you have input is:   ");
		printQueue(infixQueue);
		Queue<Object> postfix = infixToPostfix(infixQueue);
		System.out.print("\nThe equivalent postfix expression is:  ");
		printQueue(postfix);
		int answer = postFixCalculator(postfix);
		System.out.println("\nThe answer is: " + answer +"\n");
		return "loop";
	}
	
	public static void printQueue(Queue<Object> queue) {
		for(Object obj:queue) {
			if(obj instanceof Integer)
				System.out.print((Integer)obj+" ");
			else if(obj instanceof Character) {
				if(obj.equals('^')) {
					System.out.print("POW ");
				} else {
					System.out.print((Character)obj + " ");
				}
			}
		}
		System.out.println();
	}
	private static void isValid(String data) throws Exception {
		int length = data.length();
		int paren = 0;
		for(int i = 0; i < length; i ++) {
			char curr = data.charAt(i);
			if(!Character.isDigit(curr) && curr != 'P' && curr != 'O' && curr != 'W' && curr != 'q' && curr != 'u' 
					&& curr != 'i' && curr != 't' && curr != '(' && curr != ')' && curr!=' ' && curr != '+' 
					&& curr != '-' && curr !='*' && curr != '/' && curr!= '%') {
				throw new Exception("Invalid character:" + curr);
			}
			if(curr == '(') paren ++;
			if(curr == ')') paren --;
		}
		if(paren != 0) {
			throw new Exception("Parenthesis are equivalent.");
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		while(true) {
			String ans = console();
			if(ans.equals("quit")) {
				System.out.println("You chose to quit!");
				break;
			}
		}
	}

}
