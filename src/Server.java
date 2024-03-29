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

	boolean DEBUG = true; // true: print moves and board for each turn
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
	
	public Board loadBoard(String board_filename){
		Board b = new Board();
		int[][] board = new int[8][8];
		try {
			BufferedReader input =   new BufferedReader(new FileReader(board_filename));
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


	

	public Move getMove2(IDSAgent agent, int turn, Board b, int currentmove) {
		Move move=new Move();
		Timer t=new Timer();
		Runtime r=Runtime.getRuntime();
		ScheduleRunner timeout=new ScheduleRunner();
		String movestring="";;
		//convert timelimit to milliseconds;
		long limit=timelimit*60000+10000;
		//schedule the timeout for the agent
		
		try {
			agent.play(b, 2);// 2 minutos
			
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
	
	public IDSAgent[] runTournament(IDSAgent[] agents){
		
			/** reiniciar marcadores wins, draws, losses **/
			for (int i = 0; i < agents.length; i++) {
				System.out.println(agents[i].name);
				agents[i].wins 		= 0;
				agents[i].losses 	= 0;
				agents[i].draws 	= 0;
			}
			
			/** Todos contra todos y reinicio del tablero **/
			for (int i=0; i<agents.length; i++) {
				for (int j=0; j<agents.length; j++) {
					if (i!=j && agents[i]!=null && agents[j]!=null) {
						System.out.println("---------------"+agents[i].name+ " vs. "+agents[j].name+"---------------");
						runGame(agents[i], agents[j]);					
						current_board = init_board.clone();
					}
				}
			}				
			Sort.insertionSort(agents);				
			return agents;
	}
	
	
	public void runGame(IDSAgent agents2, IDSAgent agents3) {

		IDSAgent[] agents = new IDSAgent[2];
		agents[0]=agents2;
		agents[1]=agents3;		
		
		gameover=false;
		currentagent=AGENT0;
		Move move;
		int currentmove=0;
		while (!gameover) {

			if (!current_board.isStalemate() && !current_board.isCheckMate()) {
				move=agents[currentagent].getBestMove(current_board, IDSAgent.MINIMAX);
				if(DEBUG){
					//System.out.println(current_board);
					//System.out.println("Move: "+move);
				}
				
				movimientos.add(move);
				if (!current_board.validMove(move)) {
					
					gameover=true;
					winner=nextAgent();
					agents[winner].addWin();
					agents[currentagent].addLoss();
					System.out.println(agents[currentagent].name + " tried to play an invalid move.");
					System.out.println(agents[winner].name + " wins the game.");
				}
				else current_board.makeMove(move);
			//	System.out.println(b);
			} else if (current_board.isStalemate()) {
				//System.out.println(current_board);
				System.out.println("Stalemate");
				winner=NOBODY;
				loser=NOBODY;
				agents[nextAgent()].addDraw();
				agents[currentagent].addDraw();
				draw=true;
				gameover=true;
			} else if (current_board.isCheckMate()) {
				//System.out.println(current_board);
				System.out.println("Checkmate");
				System.out.println(agents[nextAgent()].name + " has won!");
				agents[nextAgent()].addWin();
				agents[currentagent].addLoss();
				draw=false;
				gameover=true;
			}
			currentagent=nextAgent();
			currentmove++;
		};
		
	}
		
	/*
	 * A partir de 10 cromosomas, cada uno tiene sus genes inicialmente aleatorio, se crean {num_agentes-10} agentes adicionales con cromosomas aleatorios, 
	 * todo ellos constituyen la poblacion inicial del torneo. 
	 * En el torneo juegan todos contra todos y en base al numero de wins, se rankean los top10. De los 1° 5 tops 10 se mezclan en un nuevo gen (considerando el 1° cromosoma
	 * del gen 1, el 2° cromosoma del gen 2... hasta completar los 5 cromosomas que corresponden a los valores materiales de los tipos de piezas.
	 * Con los 2° 5 top 10, se crea un 2° nuevo agente de la misma forma que el anterior. 
	 * Al finalizar un torneo, se guardan los genes en un archivo para hacer futuras comparaciones.
	 * Cada torneo nuevo (a partir del inicial), se comienza agregando dos nuevos agentes aleatorios para dar la posibilidad de potenciales nuevos ganadores.
	 */
	public static void main(String[] args){
		
		int num_agents = 12; // Debe ser mayor a 12	
		Server s=new Server(num_agents);
		s.TORNEOS = 20;
		int k = 0;
		String file_name = "top10gens.txt";
		int num_lines = 10;
		
		while(k<s.TORNEOS){						
			String gen[];
			double params[] = null;
			int i;
			try {
				BufferedReader input =   new BufferedReader(new FileReader(file_name));
				for (i = 0; i < num_lines; i++) {	
					IDSAgent topAgent=new IDSAgent(IDSAgent.MINIMAX,"moho"+i);					
					
					String line=input.readLine();
					System.out.println("agente leido: "+line);
					gen = line.split(" ");
					params = new double[gen.length];
					for (int j = 0; j < gen.length; j++) {
						params[j] = Double.parseDouble(gen[j]);
					}
					topAgent.utility = new MaterialValue(params);
					agents_ids[i] = topAgent;
				}
			}catch (Exception e) {
				System.out.println("Error al leer el archivo: "+e.getMessage());
			}
			
			/***
			 * Genera los {num_agents-10} agentes restantes con valores randomizados en sus cromosomas
			 */
			for (i=10; i < num_agents; i++) {
				Random r = new Random();
				IDSAgent randomAgent=new IDSAgent(IDSAgent.MINIMAX,"moho"+i);
				double g1 = r.nextInt(801)+100;
				double g2 = r.nextInt(801)+100;
				double g3 = r.nextInt(801)+100;
				double g4 = r.nextInt(801)+100;
				double g5 = r.nextInt(801)+100;
				randomAgent.utility = new MaterialValue(new double[]{g1/100.0, g2/100.0, g3/100.0, g4/100.0, g5/100.0});
				agents_ids[i] = randomAgent;
			}
			
			init_board = s.loadBoard("board.tbl");
			current_board = init_board.clone();

			/*
			 * Todos contra todos y los rankea por numero de wins
			 */
			IDSAgent[] agents_ids_aux = s.runTournament(agents_ids);
			
			/*
			 * Guarda el mejor en disco 
			 */
			writeBestAgent(agents_ids_aux[0]);
			System.out.println("mejor agente guardado");
			
			
			
			agents_ids_aux[10] = new IDSAgent(IDSAgent.MINIMAX,"mohoMUTADO_1");
			agents_ids_aux[10].utility = new MaterialValue(new double[]{agents_ids_aux[0].utility.values[0],
																		agents_ids_aux[1].utility.values[1],
																		agents_ids_aux[2].utility.values[2],
																		agents_ids_aux[3].utility.values[3],
																		agents_ids_aux[4].utility.values[4]});
			
			agents_ids_aux[11] = new IDSAgent(IDSAgent.MINIMAX,"mohoMUTADO_2");
			agents_ids_aux[11].utility = new MaterialValue(new double[]{agents_ids_aux[5].utility.values[0],
																		agents_ids_aux[6].utility.values[1],
																		agents_ids_aux[7].utility.values[2],
																		agents_ids_aux[8].utility.values[3],
																		agents_ids_aux[9].utility.values[4]});					
				
			try{
				FileWriter fstream = new FileWriter("top12.txt");
				BufferedWriter out = new BufferedWriter(fstream);
				String st = "";
				for (i = 0; i < 12; i++) {
					for (int j = 0; j < agents_ids_aux[i].utility.values.length; j++) {
						st = st+agents_ids_aux[i].utility.values[j]+" ";						
					}
					st = st.substring(0, st.length()-1);
					st = st+"\n";
				}
				out.write(st);
				out.close();
				
				file_name = "top12.txt";
				num_lines = 12;
				num_agents = num_agents + 2;
				System.out.println("top 12 agentes guardados");
				s = new Server(num_agents);
				}catch (Exception e){
					System.err.println("Error: " + e.getMessage());				
				}			
			System.out.println("Fin torneo: "+(k+1));
			k++;
		}
	}
	
	
	
	public static void writeBestAgent(IDSAgent agente) {
		try{
			BufferedReader input =   new BufferedReader(new FileReader("bestAgent.txt"));
			
			String s = "";
			String line = input.readLine();
			while(line!=null){
				s = s.concat(line+"\n");
				line = input.readLine();				
			}
			

			FileWriter fstream = new FileWriter("bestAgent.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			for (int i = 0; i < agente.utility.values.length; i++) {
				s = s+agente.utility.values[i]+" ";
			}
			s = s.substring(0, s.length()-1);
			out.write(s);
			out.close();
			input.close();
			}catch (Exception e){
				System.err.println("Error: " + e.getMessage());
			}
	}

}