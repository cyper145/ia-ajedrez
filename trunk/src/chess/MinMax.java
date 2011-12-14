package chess;

import util.Node;

public class MinMax {

	/*
	 * http://en.wikipedia.org/wiki/Minimax
	 */
	public static int MinMax(Node n){
			
		if(n.value == Integer.MAX_VALUE || n.value ==10 || n.value ==5 || n.value ==-10 || n.value ==7 || n.value ==Integer.MIN_VALUE || n.value ==-7 || n.value ==-5){
		//if(n.value != Integer.MAX_VALUE || n.value !=0 || n.value !=10 || n.value !=-10){
			return (Integer)n.value;
		}
		int alfa = Integer.MIN_VALUE;
		int r = 0;
		for (int i = 0; i < n.getChildren().size(); i++) {
			r = -MinMax.MinMax((Node)n.getChildren().get(i));
			if(alfa>r){
				alfa = r;				
			}
		}
		return alfa;
	}
}
