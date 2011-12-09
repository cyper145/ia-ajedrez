package example;
/*
 * Author: Matthew Bardeen <me@mbardeen.net>, (C) 2008
 *
 * Copyright: See COPYING file that comes with this distribution
 *
*/

import java.io.*;
import java.util.Arrays;
import java.util.Random;

import util.Node;
import util.Tree;
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
//			if (turn.equals("N")) b.setTurn(b.TURNBLACK);
//			else b.setTurn(b.TURNWHITE);
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
			System.out.println(b);
	
		} catch (Exception e) {}
		/*
		System.out.println(b);
		Move[] moves=b.getValidMoves();
		System.out.println("Stalemate:"+b.isStalemate());
		System.out.println("Checkmate:"+b.isCheckMate());
		for (Move m:moves) {
			System.out.println(m);
		}
		*/
		
		Tree t = new Tree();
		Node root =  new Node (b);
		t.setRootElement(root);
		makeTree(root, 0);
		/*Move[] moves=b.getValidMoves();
		for (Move m:moves) {
			Board b_child = b.clone();
			b_child.makeMove(m);
			Node n =  new Node (b_child);
			 root.addChild(n);
		}*/
		//System.out.println(root.toString());
		
	}
	
	private static int makeTree(Node n, int vuelta){
		vuelta++;
		Board b = (Board) n.data;
		Move[] moves=b.getValidMoves();
		
		/*
		if(moves.length>0){
			Board b_child = b.clone();
			b_child.makeMove(moves[0]);
			System.out.println("Movimiento: "+moves[0].toString());
			System.out.println("Stalemate:"+b_child.isStalemate());
			System.out.println("Checkmate:"+b_child.isCheckMate());
			Node n_new =  new Node (b_child);
			
			n.addChild(n_new);
			System.out.println(b_child.toString());
			makeTree(n_new, vuelta);
		}
		*/
		for (Move m:moves) {
			Board b_child = b.clone();
			b_child.makeMove(m);
			Node n_new =  new Node (b_child);
			n.addChild(n_new);
			System.out.println("Movimiento: "+moves[0].toString());
			System.out.println("Stalemate:"+b_child.isStalemate());
			System.out.println("Checkmate:"+b_child.isCheckMate());
			System.out.println(b_child.toString());
			if(b_child.isCheckMate()){
				System.out.println("Gano por jaquemate!");
				return 1;
			}
			if(b_child.isStalemate()){
				System.out.println("Ocurrio un empate :o !");
				return 2;
			}
			if(vuelta<4){
				int value = makeTree(n_new, vuelta);
				if(value!=0){
					return value;
				}
				
			}
		}
		return 0;
	}

}
