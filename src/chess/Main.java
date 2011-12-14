package chess;
/*
 * Author: Matthew Bardeen <me@mbardeen.net>, (C) 2008
 *
 * Copyright: See COPYING file that comes with this distribution
 *
*/

/**
 * Eclipse run configuration: ${workspace_loc:ia-ajedrez/src/chess/problem10.tbl}
 */

import java.io.*;
import java.util.Random;
import util.Node;
import util.Tree;

public class Main { 
	Board b;
	private static int LEVEL_TREE = 4;
	private static int num_nodes = 0;
	private static long startTime ;
	private static long endTime ;
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
			b.fromArray(board);
			/*System.out.println(turn);
			if (turn.equals("N")) b.setTurn(b.TURNBLACK);
			else b.setTurn(b.TURNWHITE);*/
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
	
		} catch (Exception e) {System.out.println("error: "+e.getMessage());}
		
		 startTime = System.currentTimeMillis();
		/** Setting root node from args[] **/
		Tree t = new Tree();
		Node root =  new Node (b);
		t.setRootElement(root);
		makeTree(root, 0);
		endTime = System.currentTimeMillis();
		System.out.println("--------------------------\n");
		//DFS.DFS(root);
		//System.out.println(t.toString());
		System.out.println("Numero de nodos: "+num_nodes);	
		System.out.println("Tiempo de ejecucion: "+(endTime-startTime)/1000.0+"segundos");
		//System.out.println(MinMax.MinMax(root));
		//System.out.println(t.toString());
	}
	
	private static int makeTree(Node<Board> n, int level){
		level++;
		Board b = (Board) n.data;
		Move[] moves=b.getValidMoves();
	
		for (Move m:moves) {
			Board b_child = b.clone();
			b_child.makeMove(m);
			Node n_new =  new Node (b_child);			
			n_new.father = n;			
			n.addChild(n_new);
			//System.out.println("Movimiento: "+moves[0].toString());
			//System.out.println("Stalemate:"+b_child.isStalemate());
			//System.out.println("Checkmate:"+b_child.isCheckMate());
			//System.out.println(b_child.toString());
			if(b_child.isCheckMate()){
				System.out.println("Gano por jaquemate!");
				if(b_child.turn== Board.TURNWHITE){
					n_new.value = 10;
				}else{
					n_new.value = -10;
				}
				return 1;
			}
			else if(b_child.isStalemate()){
				n_new.value = 0;
				System.out.println("Ocurrio un empate :o !");
				return 2;
			}
			if(level<Main.LEVEL_TREE){
				num_nodes++;
				int value = makeTree(n_new, level);				
			}
			else if (level == Main.LEVEL_TREE){
				n_new.value = Integer.MAX_VALUE;
			}
		}
		return 0;
	}

}
