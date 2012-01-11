package chess;
/*
 * Authors: Andres Garrido, Camilo Verdugo, Esteban Cruz
 *
*/

/**
 * Eclipse run configuration: ${workspace_loc:ia-ajedrez/src/chess/problem10.tbl}
 * 
 * Problem2.tbl
 * A4-A3
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
	
	/**
	 * @param args board (*.tbl)
	 */
	public static void main(String[] args) {
		/**
		 * Reading input board
		 */
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
			System.out.println("In:");
			System.out.println(b);
	
		} catch (Exception e) {System.out.println("error: "+e.getMessage());}
		
		
		
		 
		 
		/** Setting root node from input **/
		Tree t = new Tree();
		Node root =  new Node (b);
		t.setRootElement(root);
		
		/**
		 * Making Tree by depth
		 */
		//System.out.println("Using depth makeTree:");
		//System.out.println(makeTree(root, 0));
		//System.out.println("---------------------");
		
			
		startTime = System.currentTimeMillis();
		
		System.out.println("Checkmate:");
		num_nodes = DFS_HEIGHT.IDS(b,4);
		endTime = System.currentTimeMillis();		
		
		//DFS.DFS(root);
		//System.out.println("MinMax:"+MinMax.MinMax(root));
		
		System.out.println();
		System.out.println("Numero de nodos: "+num_nodes);	
		System.out.println("Tiempo de ejecucion: "+(endTime-startTime)/1000.0+"segundos");
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
			
			if(b_child.isCheckMate()){
				System.out.println("Checkmate Move:"+m);
				if(b_child.turn== Board.TURNWHITE){
					n_new.value = 10;
					n_new.isTerminal = true;
					//return 1;
				}else{
					n_new.value = -10;
					n_new.isTerminal = true;					
					//return 0;
				} 
			}
			else if(b_child.isStalemate()){
				n_new.value = 0;
				n_new.isTerminal = true;
				System.out.println("Ocurrio un empate :o !");
				//return 2;
			}
			if(level<Main.LEVEL_TREE){
				num_nodes++;
				int value = makeTree(n_new, level);	
				if(value !=0){
					return value;
				}
			}
			else if (level == Main.LEVEL_TREE){
				n_new.value = Integer.MAX_VALUE;
			}
		}
		return 0;
	}
	
}