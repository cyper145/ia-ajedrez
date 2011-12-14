package chess;

import util.Node;

public class MinMax {

	/*
	 * http://en.wikipedia.org/wiki/Minimax
	 */
	public static int MinMax(Node n){
			
		if(n.isTerminal){
			return (Integer)n.value;
		}
		int alfa = Integer.MIN_VALUE;
		for (int i = 0; i < n.getChildren().size(); i++) {
			alfa = MinMax.max(alfa, -MinMax.MinMax((Node)n.getChildren().get(i)));
		}
		return alfa;
	}
	
	
	private static int max(int a, int b){
		if(a>b)
			return a;
		else
			return b;
	}
}
