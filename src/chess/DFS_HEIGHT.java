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
				}//System.out.println("x:"+x);
			}
		}
	}
	public static int DFS_HEIGHT(Board b, int height){
		if(b.isCheckMate()){
			if(b.turn== Board.TURNWHITE){//System.out.println(b);
				return -10;}
			else
				return 10;
			//System.out.println("mate!"+b);
		}else if(b.isStalemate())
			return 0;
		if(height==0)
			return -1;
		Move[] moves=b.getValidMoves();
		for (Move m:moves) {
			Board b_child = b.clone();
			b_child.makeMove(m);
			//Node minmax = MinMax.MinMax_HEIGHT(b_child, height);
			int x = DFS_HEIGHT(b_child, height-1);
			if(x==10){
				System.out.println("m gano    ---:"+m);
				return x;
			}	
		}
		return 0;
	}
	
	
	
	
}
