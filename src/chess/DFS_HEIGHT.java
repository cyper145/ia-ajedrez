package chess;

import java.util.Stack;

import util.Node;

public class DFS_HEIGHT {
	public static void IDS(Board b, int max_height){
		for (int i = 0; i < max_height; i++) {
			Move[] moves=b.getValidMoves();
			for (Move m:moves) {
				Board b_child = b.clone();
				b_child.makeMove(m);
				int x = DFS_HEIGHT(b_child, i);
				if(x==10){
					System.out.println("m gano2    ---:"+m);		
					i=max_height;
					break;
				}System.out.println("x:"+x);
			}
		}
	}
	public static int DFS_HEIGHT(Board b, int height){
		if(b.isTerminal){
			return -10;
		}

		
//		if(height==0 || b.isTerminal)
//			return alfa;
		int alfa = Integer.MIN_VALUE;
		Move[] moves=b.getValidMoves();
		for (Move m:moves) {
			Board b_child = b.clone();
			b_child.makeMove(m);
			
			if(b_child.isCheckMate()){
//				if(b.turn== Board.TURNWHITE){//System.out.println(b);
//					return 10;}
//				else
				b.isTerminal = true;
				//System.out.println("mate!"+b);
			}
			
			
			//Node minmax = MinMax.MinMax_HEIGHT(b_child, height);
			alfa = max(alfa, -DFS_HEIGHT(b_child, height-1));
//			if(alfa==10){
//				System.out.println("m gano    ---:"+m);
//				return alfa;
//			}	
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
