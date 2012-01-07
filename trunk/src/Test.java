/*
 * Author: Matthew Bardeen <me@mbardeen.net>, (C) 2008
 *
 * Copyright: See COPYING file that comes with this distribution
 *
*/

import java.io.*;
import java.util.Random;
public class Test {
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
			b.setShortCastle(b.TURNBLACK,false);
			b.setLongCastle(b.TURNBLACK,false);
			b.setShortCastle(b.TURNWHITE,false);
			b.setLongCastle(b.TURNWHITE,false);
			String st=input.readLine();
			while (st!=null) {
				if (st.equals("EnroqueC_N")) b.setShortCastle(b.TURNBLACK,true);
				if (st.equals("EnroqueL_N")) b.setLongCastle(b.TURNBLACK,true);
				if (st.equals("EnroqueC_B")) b.setShortCastle(b.TURNWHITE,true);
				if (st.equals("EnroqueL_B")) b.setLongCastle(b.TURNWHITE,true);
				if (st.contains("AlPaso"))
				{
				    String[] split=st.split("\\s"); //split at whitespace
				    b.setEnPassent(new Coord(split[1].trim()));
				}
				
				
				st=input.readLine();
				
			}
			//System.out.println(b);
	
		} catch (Exception e) {}
		System.out.println(b);
		Move[] moves=b.getValidMoves();
		System.out.println("Stalemate:"+b.isStalemate());
		System.out.println("Checkmate:"+b.isCheckMate());
		System.out.println("Check:"+b.isCheck(b.turn));
		for (Move m:moves) {
			System.out.println(m);
		}
	}
	
	

}
