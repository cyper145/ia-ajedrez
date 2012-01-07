/*
 * Author: Matthew Bardeen <me@mbardeen.net>, (C) 2008
 *
 * Copyright: See COPYING file that comes with this distribution
 *
*/

import java.io.*;
import java.util.Random;
public class RandomAgent {
	Board b;
	
	public static void main(String[] args) {

		Random r=new Random();
		int[][] board = new int[8][8];
		Board b=new Board();
		try {
			BufferedReader input =   new BufferedReader(new FileReader(args[0]));
			for (int i=0; i<8; i++) {
				String line=input.readLine();
				String[] pieces=line.split("\\s");
				for (int j=0; j<8; j++) {
					board[i][j]=Integer.parseInt(pieces[j]);
				}
			}
			String turn=input.readLine();
			//System.out.println(turn);
			b.fromArray(board);
			if (turn.equals("N")) b.setTurn(b.TURNBLACK);
			else b.setTurn(b.TURNWHITE);
			b.setShortCastle(b.turn,false);
			b.setLongCastle(b.turn,false);
			String st=input.readLine();
			while (st!=null) {
				if (st.equals("EnroqueC")) b.setShortCastle(b.turn,true);
				if (st.equals("EnroqueL")) b.setLongCastle(b.turn,true);
				st=input.readLine();
				
			}
			//System.out.println(b);
	
		} catch (Exception e) {}
		
		Move[] moves=b.getValidMoves();
		System.out.println(moves[r.nextInt(moves.length)]);
		
	}
	
	

}