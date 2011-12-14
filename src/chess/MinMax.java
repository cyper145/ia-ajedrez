package chess;

import util.Node;

public class MinMax {

	
	public static int MinMax(Node n){
			
		if(n.value != Integer.MAX_VALUE || n.value !=0 || n.value !=10 || n.value !=-10){
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
