/*
 * Author: Matthew Bardeen <me@mbardeen.net>, (C) 2008
 *
 * Copyright: See COPYING file that comes with this distribution
 *
*/

import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.util.Random;

public class IDSAgent {
	Board b; 			/// a copy of the current board.


	public String name;
	Move bestoverallmove;  		/// the best move we have overall
	public ScheduleRunner timeout; 	/// this holds the method that we will execute once our time limit is up.
	public int nodesexpanded;	/// Number of nodes exanded
	public int maxdepthreached;	/// The max depth reached by the search

	public int algorithm; 
	public Heuristic utility;	/// A function to determine the utility of the current board
	static final int MAXDEPTH=5; 	/// How deep we will search without limits
	static final int MINIMAX=1;	/// Do a minimax search
	static final int ALPHABETA=2; 	/// Do a minimax search with alpha-beta pruning
	static final int INFINITY=99999999;
	static final int WINVALUE=999999;
	static final int LOSSVALUE=-WINVALUE;
	public int wins=0;
	public int draws = 0;
	public int losses=0;
	
	
	Random r;
	
	public IDSAgent() 
	{
	  timeout=new ScheduleRunner();
	  nodesexpanded=0;
	  maxdepthreached=0;
	  r=new Random();
	}
	
	
	public IDSAgent(int algorithm,String name) 
	{
	  timeout=new ScheduleRunner();
	  nodesexpanded=0;
	  maxdepthreached=0;
	  r=new Random();
	  this.algorithm=algorithm;
	  this.name = name;
	}
	  
	
	public void addWin() {
		wins++;
	}

	public void addDraw() {
		draws++;
	}

	public void addLoss() {
		losses++;
	}
	
	class MoveValue
	{
	    public Move move;
	    public double value;
	    
	    MoveValue() 
	    {
		move=null;
		value=0;
	    }
	    
	    MoveValue(Move m) 
	    {
	      this.move=m;
	      value=0;
	    }
	    
	    MoveValue(double v)
	    {
	      this.value=v;
	      this.move=null;
	    }
	    
	    
	    MoveValue(Move m, double v)
	    {
	      this.value=v;
	      this.move=m;
	    }
	}
	
	//inner class
	class ScheduleRunner extends TimerTask
   	{
   		/**
    		* executed when time is up.
    		*/
		public void run()
		{
			//time's up. Print out the best move and exit.
			System.out.println(bestoverallmove);
			System.out.println("Nodes Expanded:" +nodesexpanded);
			System.out.println("Max Depth Reached:" + maxdepthreached);
			System.exit(0);
		}
	} // end inner class ScheduleRunner
	


	/** Return the best move based on the current board 
	**/
        public Move getBestMove(Board inb, int algorithm) 
	{
	    MoveValue bestmove=new MoveValue();
	    for (int depth=1; depth<MAXDEPTH; depth++) 
	    {
		  if (algorithm==MINIMAX)
		    bestmove=minimax(inb, 1,depth);  // save the best move for this depth
		  else if (algorithm==ALPHABETA)
		    bestmove=alphabeta(inb, 1,depth,-INFINITY, INFINITY);  // save the best move for this depth
		  
		  //record the best move for this level in case we need to exit early
		  bestoverallmove=bestmove.move;
		  //System.out.println(depth+":"+bestmove.move+":"+bestmove.value);
		  //sure win, return this move as the best
		  if (bestmove.value==WINVALUE)
		    return bestmove.move;
		  //no hope, should resign here
		  if (bestmove.value==LOSSVALUE)
		  {
		    Move m=new Move();
		    m.setResign(true);
		    return m;
		  }
		  //Record some statistics about the search
		  maxdepthreached=depth;		  
	    }
	    return bestoverallmove;
	}

        public MoveValue minimax(Board b, int currentdepth, int maxdepth) 
	{
	   nodesexpanded++; 
	  /*	      
	      if node is a terminal node or depth == 0:
	      return the heuristic value of node
	  */
	    if (b.isCheckMate()) {
		return new MoveValue(LOSSVALUE);
	    }
	    if (b.isStalemate()) return new MoveValue(0); 
	    if (currentdepth==maxdepth){
	    	return new MoveValue(utility.evaluate(b)*b.turn);
	    }
      
	  /*else:
	    a = -infinity
	    for child in node:              # evaluation is identical for both players 
		a = max(a, -minimax(child, depth-1))
	    return a
	  */
	    MoveValue best= new MoveValue(-INFINITY);
	    Move[] moves=b.getValidMoves();
	    for (Move currentmove : moves) 
	    {
		Board child=b.clone();
		child.makeMove(currentmove);
		//reverse the sign since we're really evaluating our opponent's board state
		MoveValue childmove = minimax(child, currentdepth+1,maxdepth);

		childmove.move=currentmove;
		childmove.value=-childmove.value;

		if (childmove.value>best.value) {
		    best=childmove;
		   // System.out.println(best.value+":"+best.move);
		}

		
	    }
	   return best;
	}

        public MoveValue alphabeta(Board b, int currentdepth, int maxdepth, double alpha, double beta) 
	{
	    nodesexpanded++;
	  /*	      
	      if node is a terminal node or depth == 0:
	      return the heuristic value of node
	  */
	    if (b.isCheckMate()) return new MoveValue(LOSSVALUE);
	    if (b.isStalemate()) return new MoveValue(0); 
	    if (currentdepth==maxdepth) return new MoveValue(0); //new MoveValue(utility.evaluate(b)*b.turn);
	    Move m=new Move();
	    MoveValue best= new MoveValue(-INFINITY);
	    Move[] moves=b.getValidMoves();
	    //Arrays.sort(moves, m);
	    for (Move currentmove : moves) 
	    {
		Board child=b.clone(); 
		child.makeMove(currentmove);
		
		MoveValue childmove = alphabeta(child, currentdepth+1,maxdepth,-beta, -alpha);
		childmove.move=currentmove;
		//reverse the sign since we're really evaluating our opponent's board state
		childmove.value=-childmove.value;

		if (childmove.value>best.value) {
		    best=childmove;
		    alpha=childmove.value;
		} else if (childmove.value==best.value) 
		// if we have a tie, replace the best move at random (to avoid the problem of loops)
		  if (r.nextDouble()>0.5)
		    best=childmove;
		

		if (beta<=alpha) break; 
	    }

	    return best;
	}
	
    public void play(Board current_board,int time){
    	int[][] board = new int[8][8];
		Board b=current_board;
		IDSAgent ids=new IDSAgent(IDSAgent.MINIMAX,"moho2");
		ids.utility = new MaterialValue(new double[]{1.6561000,2.7346,3.201,5.857,3.508});
		
		Timer t = new Timer();
		//System.out.println("arg0: "+args[0]+", arg1: "+args[1]+", arg2: "+args[2]);
		//convert the numeric value given as parameter 3 into minutes, and then give 10 second leeway to return a response
		long limit=time*60000-10000;
		//schedule the timeout.. if we pass the timeout, the program will exit.
		t.schedule(ids.timeout, limit);	
			
	
		Move move=ids.getBestMove(b,1);	
		System.out.println(move);
		System.out.println("Nodes Expanded:" +ids.nodesexpanded);
		System.out.println("Max Depth Reached:" + ids.maxdepthreached);
		//System.exit(0);
    }
        

	public static void main(String[] args) {

		
		int[][] board = new int[8][8];
		Board b=new Board();
		IDSAgent ids=new IDSAgent();
		//System.out.println(Arrays.toString(args));
		ids.utility=new MaterialValue(args[1]);
		
		Timer t = new Timer();
		//System.out.println("arg0: "+args[0]+", arg1: "+args[1]+", arg2: "+args[2]);
		//convert the numeric value given as parameter 3 into minutes, and then give 10 second leeway to return a response
		long limit=Integer.parseInt(args[3])*60000-10000;
		//schedule the timeout.. if we pass the timeout, the program will exit.
		t.schedule(ids.timeout, limit);
	
		//significa que queremos modificar el gen llamando a random
		//crea un backup y luego los cambios hechos por randomo los guarda en su archivo
		//para utilizarlo durante el juego
		if(args.length==5){
			//randomiza
			if(Integer.parseInt(args[4])==0){
				ids.utility.writeKnowledgeBackup(ids.utility.values);
				ids.utility.randomizeGen();
				ids.utility.writeKnowledge(ids.utility.values);
			}
			//restaura el backup
			else if(Integer.parseInt(args[4])==	1){
				ids.utility.restoreKnowledgeBackup(ids.utility.values);
			}
			System.exit(0);	
		}
			
		try {
			BufferedReader input =   new BufferedReader(new FileReader(args[2]));
			for (int i=0; i<8; i++) {
				String line=input.readLine();
				String[] pieces=line.split("\\s");
				for (int j=0; j<8; j++) {
					board[i][j]=Integer.parseInt(pieces[j]);
				}
			}
			String turn=input.readLine();
			b.fromArray(board);
			if (turn.equals("N")) b.setTurn(b.TURNBLACK);
			else b.setTurn(b.TURNWHITE);
			b.setShortCastle(b.TURNWHITE,false);
			b.setLongCastle(b.TURNWHITE,false);
			b.setShortCastle(b.TURNBLACK,false);
			b.setLongCastle(b.TURNBLACK,false);
		
			String st=input.readLine();
			while (st!=null) {
				if (st.equals("EnroqueC_B")) b.setShortCastle(b.TURNWHITE,true);
				if (st.equals("EnroqueL_B")) b.setLongCastle(b.TURNWHITE,true);
				if (st.equals("EnroqueC_N")) b.setShortCastle(b.TURNBLACK,true);
				if (st.equals("EnroqueL_N")) b.setLongCastle(b.TURNBLACK,true);
				st=input.readLine();
			}
		} catch (Exception e) {}	
		Move move=ids.getBestMove(b,Integer.parseInt(args[0]));	
		System.out.println(move);
		System.out.println("Nodes Expanded:" +ids.nodesexpanded);
		System.out.println("Max Depth Reached:" + ids.maxdepthreached);
		System.exit(0);
	}
}
