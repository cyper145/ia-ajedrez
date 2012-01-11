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
		agent=new Agent[agentes];
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

	public Agent[] runTournament(Agent[] agents, int numAgentes) {
		
		if(numAgentes>1){
			for (int i=0; i<numAgentes; i++) {
				for (int j=0; j<numAgentes; j++) {
					if (i!=j && agents[i]!=null && agents[j]!=null) {
						System.out.println("---------------"+agents[i]+ " vs. "+agents[j]+"---------------");
						System.out.println(runGame(agents[i], agents[j]));
					}
				}
			}
			
			/*
			 * Ordena los agentes descendentemente por victorias
			 */
			Sort.insertionSort(agents);
			
			/*
			 * Imprime la primera batalla de todos contra todos ordenado por victorias.
			 */
			for (int i = 0; i < numAgentes; i++) {
				System.out.println(agents[i]+" wins:"+agents[i].wins + " Draws:" +agents[i].draws +" Losses:"+agents[i].losses);
			}
			
			
			
			int cupos = numAgentes/2;
			Agent[] clasificados = new Agent[cupos];		
			for (int i = 0; i <cupos ; i++) {			
				clasificados[i]=agents[i];
			}
			
			/*
			 * Reinicia las variables del server
			 */
			winner=NOBODY;
			loser=NOBODY;
			currentagent=NOBODY;
			draw=false;
			return runTournament(clasificados, cupos);			
		}else{
			System.out.println("Fin del torneo!! El mejor del torneo es:");
			for (int i = 0; i < numAgentes; i++) {
				System.out.println(agents[i]+" wins:"+agents[i].wins + " Draws:" +agents[i].draws +" Losses:"+agents[i].losses);
			}
			
			return agents;
		}
		
	}

	
	public static void main(String[] args){
		
		Runtime runtime =Runtime.getRuntime();
		Random r = new Random();			
		int agentesPreconfigurados = 4;
		int agentesAleatorios = 0;
		Server s=new Server(agentesPreconfigurados+agentesAleatorios);
		/*
		 * Usando IDS, carga <agentesPreconfigurados> agentes con genes  previamente definidos de la forma: knowledge{i}.gen
		 */
		for (int i = 0; i < agentesPreconfigurados; i++) {			
			s.agent[i]=new Agent( "knowledge"+i, "java IDSAgent 1 "+"knowledge"+i+".gen");			
			try {
				String execstring=(s.agent[i].getCommand()+" "+"move"+0+".tbl"+" "+2 +" "+0);
				System.out.println("Running: "+execstring);
				s.currentProcess=runtime.exec(execstring);		
			} catch (IOException e) {e.printStackTrace();}
		}
		
		
		/*
		 * Genera genes aleatorios (escribiendo en disco los archivos) los que correrá luego bajo IDS
		 */
		for (int i = agentesPreconfigurados; i < agentesPreconfigurados+agentesAleatorios; i++) {
			double[] gen = new double[]{r.nextInt(9)+1, r.nextInt(9)+1, r.nextInt(9)+1, r.nextInt(9)+1, r.nextInt(9)+1};
			writeKnowledge(gen, "random"+i+".gen");
			s.agent[i]=new Agent( "random"+i, "java IDSAgent 1 "+"random"+i+".gen");
		}
		
		/*
		 * Inicia el torneo con los 4 agentes previamente definidos y otros aleatorios
		 */
		Agent[] ganadores = s.runTournament(s.agent, s.agent.length);
		
		
		//luego de obtener los mejores jugadores
		//restaura aquellos que no salieron clasificados(ultimo parametro con 1)
		//esto significa que la mutacion no sirvio
		/*
		boolean encontrado;
		for (int i = 0; i < 4; i++) {
			encontrado = false;
			for (int j = 0; j < ganadores.length; j++) {
				if(s.agent[i].getCommand().compareTo(ganadores[i].getCommand())==0){
					encontrado=true;
				}
			}
			//si no se encontro el agente en los ganadores, se restauran los valores del gen original
			//si se encuentra, el gen modificado se mantiene
			if(!encontrado){
				try {
					String execstring=(s.agent[i].getCommand()+" "+"move"+0+".tbl"+" "+2 +" "+1);
					System.out.println("Running: "+execstring);
					s.currentProcess=runtime.exec(execstring);
			
				} catch (IOException e) {e.printStackTrace();}
			}
		}*/
		
	}
	
	public static double[] readKnowldege(String filename) {
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
			
	}
	
}