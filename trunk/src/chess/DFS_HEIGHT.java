package chess;

import java.util.Stack;

import util.Node;

public class DFS_HEIGHT {
	private static int numnodes=0;
	
	public static int IDS(Board b, int max_height){
		for (int i = 0; i < max_height; i++) {
			Move[] moves=b.getValidMoves();
			for (Move m:moves) {
				Board b_child = b.clone();
				b_child.makeMove(m);
				int x = DFS_HEIGHT(b_child, i);
				if(x==10){
					System.out.println("Move White:"+m);		
					i=max_height;
					break;
				}
			}
		}
		return numnodes;
	}
	public static int DFS_HEIGHT(Board b, int height){
		if(b.isCheckMate()){
			if(b.turn== Board.TURNWHITE)
				return -10;
			else
				return 10;
		}else if(b.isStalemate())
			return 0;
		if(height==0)
			return -1;
		Move[] moves=b.getValidMoves();
		for (Move m:moves) {
			Board b_child = b.clone();
			b_child.makeMove(m);
			numnodes++;
			int x = DFS_HEIGHT(b_child, height-1);
			if(x==10){
				System.out.println("Move Black:"+m);
				return x;
			}	
		}
		return 0;
	}
	
	
}
