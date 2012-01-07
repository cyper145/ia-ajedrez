import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

public class Server {

	/********* CHANGE THIS TO THE COMMAND TO RUN YOUR AGENT ********/
	//public static String AGENT1COMMAND="java IDSAgent 2";
	public static String AGENT1COMMAND="java IDSAgent 1";



	//public static String RANDOMAGENTCOMMAND="java -cp CHESS IDSChess ";
	public static String RANDOMAGENTCOMMAND="java IDSAgent 1";
	int timelimit=2; // TIME LIMIT IN MINUTES




	public static int NOBODY=-1;
	public static int AGENT0=0;	
	public static int AGENT1=1;	

	
	public Agent[] agent;


	Process currentProcess;

	int currentagent;
	public int winner;
	public int loser;
	boolean gameover;
	public boolean draw;

	Server() {
		winner=NOBODY;
		loser=NOBODY;
		currentagent=NOBODY;
		draw=false;
		agent=new Agent[10];
	}
	public Board loadBoard(){
		Board b = new Board();
		int[][] board = new int[8][8];
		try {
			BufferedReader input =   new BufferedReader(new FileReader("board.tbl"));
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
		return b;
	}
	public int nextAgent() {
		if (currentagent==AGENT0) return AGENT1;
		else return AGENT0;
	}	

	//inner class
	class ScheduleRunner extends TimerTask
   	{
   		/**
    		* executed when time is up.
    		*/
		public void run()
		{
			currentProcess.destroy();
			gameover=true;
			winner=nextAgent();
			System.out.println("Time out");
			
		}

	} // end inner class ScheduleRunner


	
	/** Run a game between two agents.
	**/
	public String runGame(Agent agent1, Agent agent2) {
		//choose randomly which agent goes first

		//create a new board
		String record="";
		Board b=new Board();
		b = loadBoard();
		Agent[] agents = new Agent[2];
		agents[0]=agent1;
		agents[1]=agent2;
		
		gameover=false;
		currentagent=AGENT0;
		Move move;
		int currentmove=0;
		while (!gameover) {
			
			if (!b.isStalemate() && !b.isCheckMate()) {
				move=getMove(agents[currentagent],currentagent, b, currentmove);
				System.out.println(b);
				System.out.println("Move: "+move);
				if (!b.validMove(move)) {
					
					gameover=true;
					winner=nextAgent();
					agents[winner].addWin();
					agents[currentagent].addLoss();
					System.out.println(agents[currentagent] + " tried to play an invalid move.");
					System.out.println(agents[winner] + " wins the game.");
				}
				else b.makeMove(move);
			//	System.out.println(b);
			} else if (b.isStalemate()) {
				System.out.println(b);
				System.out.println("Stalemate");
				winner=NOBODY;
				loser=NOBODY;
				agents[nextAgent()].addDraw();
				agents[currentagent].addDraw();
				draw=true;
				gameover=true;
			} else if (b.isCheckMate()) {
				System.out.println(b);
				System.out.println("Checkmate");
				System.out.println(agents[nextAgent()] + " has won!");
				agents[nextAgent()].addWin();
				agents[currentagent].addLoss();
				draw=false;
				gameover=true;
			}
			currentagent=nextAgent();
			currentmove++;
		};
		return record;
	}



	/** Get a move from one of the players
	**/
	public Move getMove(Agent agent, int turn, Board b, int currentmove) {
		//write the current board to a file
		String movefile="move"+currentmove+".tbl";
		Move move=new Move();
		Timer t=new Timer();
		Runtime r=Runtime.getRuntime();
		ScheduleRunner timeout=new ScheduleRunner();
		String movestring="";;
		//convert timelimit to milliseconds;
		long limit=timelimit*60000+10000;
		//schedule the timeout for the agent
		
		try {
			//run the agent
			boardToFile(b, movefile);
			String execstring=agent.getCommand()+" "+movefile+" "+timelimit;
			System.out.println("---------");
			System.out.println("Turn:"+agent);
			System.out.println(execstring);
			currentProcess=r.exec(execstring);
			
			//wait for the execution to complete or cancel. If it ends prior, then cancel the timer.
			t.schedule(timeout, limit);
			BufferedReader input =   new BufferedReader(new InputStreamReader(currentProcess.getInputStream()));
      			currentProcess.waitFor();
			int turnmod;
			if (turn==AGENT0) turnmod=1;
			else turnmod=-1;
			movestring=input.readLine();
			if (!move.fromString(movestring,turnmod));
			   //System.out.println("Invalid Move. Agent said:" + movestring);
			input.close();
    		} catch (NullPointerException name) {
			System.out.println("Invalid Move: " + movestring);
		} catch (Exception name) {
		}
		t.cancel();
		
		return move;
	}

	
	public void boardToFile(Board currentboard, String filename) {
		try {
     		   	BufferedWriter out = new BufferedWriter(new FileWriter(filename));
        	  	out.write(currentboard.toString());
        		out.close();
    		} catch (IOException e) {
    		}
	}

	public void runTournament() {
		for (int i=0; i<agent.length; i++) {
			for (int j=0; j<agent.length; j++) {
				if (i!=j) {
					System.out.println("---------------"+agent[i]+ " vs. "+agent[j]+"---------------");
					System.out.println(runGame(agent[i], agent[j]));
					
				}
			}
		}
	}


	public static void main(String[] args){
		Server s=new Server();
		s.agent[0]=new Agent("7844", RANDOMAGENTCOMMAND);
		s.agent[1]=new Agent("AlphaBeta", AGENT1COMMAND);
		System.out.println("enter game!");
		
		System.out.println(s.runGame(s.agent[0], s.agent[1]));
		//s.runTournament();

		//for (int i=0; i< s.agent.length; i++)
		//	System.out.println(s.agent[i] + " --- Wins:"+s.agent[i].wins + " Draws:" +s.agent[i].draws +" Losses:"+s.agent[i].losses);

	}
}