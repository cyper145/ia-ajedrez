import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;


/*
 * Autores: Esteban Cruz, Andres Garrido y Camilo Verdugo
 */
public class Server {

	public static ArrayList<Move> movimientos = new ArrayList<Move>();
	int timelimit=2;

	public static int NOBODY=-1;
	public static int AGENT0=0;	
	public static int AGENT1=1;		
	public Agent[] agent;
	public static IDSAgent[] agents_ids;
	public int TORNEOS;
	public static Board init_board;
	public static Board current_board;
	

	Process currentProcess;

	int currentagent;
	public int winner;
	public int loser;
	boolean gameover;
	public boolean draw;

	Server(int agentes) {
		winner=NOBODY;
		loser=NOBODY;
		currentagent=NOBODY;
		draw=false;
		agents_ids=new IDSAgent[agentes];
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
/*	public String runGame(Agent agent1, Agent agent2) {
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
				movimientos.add(move);
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
	}*/

	public Move getMove2(IDSAgent agent, int turn, Board b, int currentmove) {
		//write the current board to a file
		//String movefile="move"+currentmove+".tbl";
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
			//boardToFile(b, movefile);
			/*String execstring=agent.getCommand()+" "+movefile+" "+timelimit;
			System.out.println("---------");
			System.out.println("Turn:"+agent);
			System.out.println(execstring);
			currentProcess=r.exec(execstring);*/
			agent.play(b, 2);// 2 minutos
			
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

	/** Get a move from one of the players
	**/
	/*public Move getMove(Agent agent, int turn, Board b, int currentmove) {
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
*/
	
	public void boardToFile(Board currentboard, String filename) {
		try {
     		   	BufferedWriter out = new BufferedWriter(new FileWriter(filename));
        	  	out.write(currentboard.toString());
        		out.close();
    		} catch (IOException e) {
    		}
	}
	
	public void runTournament2(IDSAgent[] agents){
		Random r = new Random();	
		Runtime runtime =Runtime.getRuntime();
		
		for (int i=0; i<agents.length; i++) {
			for (int j=0; j<agents.length; j++) {
				if (i!=j && agents[i]!=null && agents[j]!=null) {
					System.out.println("---------------"+agents[i].name+ " vs. "+agents[j].name+"---------------");
					runGame3(agents[i], agents[j]);
				}
			}
		}
		
		
		for (int i = 0; i < agents.length; i++) {
			System.out.println(agents[i].name+" wins:"+agents[i].wins + " Draws:" +agents[i].draws +" Losses:"+agents[i].losses);
		}
			
			
	}
	
	 

	/*public void runTournament(Agent[] agents){
		Random r = new Random();	
		Runtime runtime =Runtime.getRuntime();
		
		if(TORNEOS>0){
			for (int i=0; i<agents.length; i++) {
				for (int j=0; j<agents.length; j++) {
					if (i!=j && agents[i]!=null && agents[j]!=null) {
						System.out.println("---------------"+agents[i]+ " vs. "+agents[j]+"---------------");
						System.out.println(runGame(agents[i], agents[j]));
					}
				}
			}
			
			
			 * Ordena los agentes descendentemente por victorias
			 
			Sort.insertionSort(agents);
			
			
			 * Imprime la primera batalla de todos contra todos ordenado por victorias.
			 
			
			for (int i = 0; i < agents.length; i++) {
				System.out.println(agents[i]+" wins:"+agents[i].wins + " Draws:" +agents[i].draws +" Losses:"+agents[i].losses);
			}
			
			
			 * Randoriza los n/2 agentes "mas malos" y lanza el torneo de nuevo por var <torneo> veces
			 
			
			for (int i = 0; i <agents.length ; i++) {	
				for (int j = agents.length/2; j < agents.length ; j++) {
					if(agents[i].command.compareTo(agents[j].command)==0){						
						String execstring=("java IDSAgent 1 "+"knowledge"+i+".gen"+" "+"move"+0+".tbl"+" 2 0");
						agents[i]=new Agent("knowledge"+i,execstring);
						System.out.println("Running: "+execstring);
						try {
							currentProcess=runtime.exec(execstring);
						} catch (IOException e) {e.printStackTrace();}	
					}
				}
			}
			
			
			 * Reinicia las variables del server
			 
			winner=NOBODY;
			loser=NOBODY;
			currentagent=NOBODY;
			draw=false;
			TORNEOS--;
			runTournament(agents);			
		}else{
			System.out.println("Fin del torneo!! El mejor del torneo es:");
			for (int i = 0; i < agents.length; i++) {
				System.out.println(agents[i]+" wins:"+agents[i].wins + " Draws:" +agents[i].draws +" Losses:"+agents[i].losses);
			}
		}
		
	}*/

	public void runGame3(IDSAgent agents2, IDSAgent agents3) {
		//choose randomly which agent goes first

		IDSAgent[] agents = new IDSAgent[2];
		agents[0]=agents2;
		agents[1]=agents3;
		
		
		gameover=false;
		currentagent=AGENT0;
		Move move;
		int currentmove=0;
		while (!gameover) {

			if (!current_board.isStalemate() && !current_board.isCheckMate()) {
				move=getMove2(agents[currentagent],currentagent, current_board, currentmove);
				System.out.println(current_board);
				System.out.println("Move: "+move);
				movimientos.add(move);
				if (!current_board.validMove(move)) {
					
					gameover=true;
					winner=nextAgent();
					agents[winner].addWin();
					agents[currentagent].addLoss();
					System.out.println(agents[currentagent] + " tried to play an invalid move.");
					System.out.println(agents[winner] + " wins the game.");
				}
				else current_board.makeMove(move);
			//	System.out.println(b);
			} else if (current_board.isStalemate()) {
				System.out.println(current_board);
				System.out.println("Stalemate");
				winner=NOBODY;
				loser=NOBODY;
				agents[nextAgent()].addDraw();
				agents[currentagent].addDraw();
				draw=true;
				gameover=true;
			} else if (current_board.isCheckMate()) {
				System.out.println(current_board);
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
		
	}
	
	/*public int runGame2(IDSAgent agent1, IDSAgent agent2) {
		//choose randomly which agent goes first

		//create a new board
		IDSAgent[] agents = new IDSAgent[2];
		agents[0]=agent1;
		agents[1]=agent2;
		
		gameover=false;
		currentagent=AGENT0;
		Move move;
		int currentmove=0;
		while (!gameover) {
			System.out.println(current_board);
			if (!current_board.isStalemate() && !current_board.isCheckMate()) {
				move=agents[currentagent].getBestMove(current_board, 3);
				//System.out.println(move);
				if (!current_board.validMove(move)) {	
					return nextAgent();
				}
				else current_board.makeMove(move);
			//	System.out.println(b);
			} else if (current_board.isStalemate()) {
				return NOBODY;// nobody wins
			} else if (current_board.isCheckMate()) {
				return nextAgent();
				//agents[currentagent].addLoss();
			}
			currentagent=nextAgent();
			currentmove++;
		};
		return -1;
	}*/
	
	public static void main(String[] args){
		
		IDSAgent a1=new IDSAgent(IDSAgent.MINIMAX,"moho");
		a1.utility = new MaterialValue(new double[]{0.6561000,2.1346,3.201,2.857,8.508});
		IDSAgent a2=new IDSAgent(IDSAgent.MINIMAX,"moho2");
		a2.utility = new MaterialValue(new double[]{1.6561000,2.7346,3.201,5.857,3.508});
		
		
		
		Server s=new Server(2);
		s.agents_ids[0] = a1;
		s.agents_ids[1] = a2;
		s.TORNEOS = 2;
		
		init_board = s.loadBoard();
		current_board = init_board;
		
		s.runTournament2(agents_ids);
		//System.out.println(s.runGame2(a1,a2));
		
		
		/*Usando IDS, carga <agentesPreconfigurados> agentes con genes  previamente definidos de la forma: knowledge{i}.gen
		 
		Runtime runtime =Runtime.getRuntime();
		int agentesPreconfigurados = 2;
		Server s=new Server(agentesPreconfigurados);
		s.TORNEOS = 2;
		
		for (int i = 0; i < agentesPreconfigurados; i++) {			
			s.agent[i]=new Agent( "knowledge"+i, "java IDSAgent 1 "+"knowledge"+i+".gen");			
			try {
				String execstring=(s.agent[i].getCommand()+" "+"move"+0+".tbl"+" "+2);
				System.out.println("Running: "+execstring);
				s.currentProcess=runtime.exec(execstring);		
			} catch (IOException e) {e.printStackTrace();}
		}*/
		
		
		
		/*
		 * Inicia el torneo con los 4 agentes previamente definidos y otros aleatorios
		 */
		//s.runTournament(s.agent);		
		
	}
	
	/*public static double[] readKnowldege(String filename) {
		String gen[];
		double params[] = null;
		try {
			
			BufferedReader input =   new BufferedReader(new FileReader(filename));
			String line=input.readLine();
			gen = line.split(" ");
			params = new double[gen.length];
			for (int i = 0; i < gen.length; i++) {
				params[i] = Double.parseDouble(gen[i]);
			}
		} catch (Exception e) {}
		
		return params;
	}
	
	public static void writeKnowledge(double values[], String filename) {
		try{
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			String s = "";
			for (int i = 0; i < values.length; i++) {
				s = s+values[i]+" ";
			}
			s = s.substring(0, s.length()-1);
			out.write(s);
			out.write("\n#Peón, Alfil, Caballo ,Torre ,Dama");
			out.close();
			}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}	
			
	}*/
	
}