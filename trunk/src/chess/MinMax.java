package chess;

import util.Node;

public class MinMax {

	/*
	 * http://en.wikipedia.org/wiki/Minimax
	 * function integer minimax(node, depth)
		    if node is a terminal node or depth <= 0:
		        return the heuristic value of node
		    α = -∞
		    for child in node:                       # evaluation is identical for both players 
		        α = max(α, -minimax(child, depth-1))
		    return α
	 */
	public static int MinMax(Node n){
			
		if(n.isTerminal){
			return n.value;
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