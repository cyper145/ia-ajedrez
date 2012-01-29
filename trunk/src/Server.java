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

		
	public void boardToFile(Board currentboard, String filename) {
		try {
     		   	BufferedWriter out = new BufferedWriter(new FileWriter(filename));
        	  	out.write(currentboard.toString());
        		out.close();
    		} catch (IOException e) {
    		}
	}
	
	public IDSAgent[] runTournament(IDSAgent[] agents){
		
//		if(TORNEOS>0){
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
//			System.out.println("== Results ==");
//			for (int i = 0; i < agents.length; i++) {
//				System.out.println(agents[i].name+" wins:"+agents[i].wins + " Draws:" +agents[i].draws +" Losses:"+agents[i].losses+" "+Arrays.toString(agents[i].utility.values));
//			}
//			TORNEOS--;	
//			
//			/** Creacion de agentes mutados 2°(n/4) y nuevos agentes n/2 **/
//			if(TORNEOS>0){
//				for (int i = 0; i < agents.length/2; i++) {
//					/**
//					 * HACER LA MEZCLA DE CROMOSOMAS Y CREAR LOS NUEVOS AGENTES 
//					 * DICHOS AGENTES DEBEN REMPLAZAR LOS 2° n/4 PEORES, SIENDO LOS 
//					 * 1° N/4 LOS GANADORES ORIGINALES 
//					 */
//				}				
//				
//				for (int i = agents.length/2; i < agents.length; i++) {
//					Random r = new Random();
//					IDSAgent randomAgent=new IDSAgent(IDSAgent.MINIMAX,"moho"+(i+agents.length/2));
//					double g1 = r.nextInt(801)+100;
//					double g2 = r.nextInt(801)+100;
//					double g3 = r.nextInt(801)+100;
//					double g4 = r.nextInt(801)+100;
//					double g5 = r.nextInt(801)+100;
//					randomAgent.utility = new MaterialValue(new double[]{g1/100.0, g2/100.0, g3/100.0, g4/100.0, g5/100.0});
//					agents[i] = randomAgent;
//				}
//			}
//			
//			runTournament(agents);
//		}			
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
				//move=getMove2(agents[currentagent],currentagent, current_board, currentmove);
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
	 * num_agents   = Num. poblacion inicial
	 * TORNEO		= Num. de campeanatos, por cada uno se agregan nuevos participantes (n/4 ganadores, n/4 mezclas y n/2 nuevos agentes)
	 */
	public static void main(String[] args){
		int num_agents = 100;
//		IDSAgent a1=new IDSAgent(IDSAgent.MINIMAX,"moho");
//		a1.utility = new MaterialValue(new double[]{0.6561000,2.1346,3.201,2.857,8.508});
//		IDSAgent a2=new IDSAgent(IDSAgent.MINIMAX,"moho2");
//		a2.utility = new MaterialValue(new double[]{1.6561000,2.7346,3.201,5.857,3.508});
		while(true){
			Server s=new Server(num_agents);
			s.TORNEOS = 4;
			
			String gen[];
			double params[] = null;
			//lee los top 10 de los genes del archivo top10gens.txt
			try {
				BufferedReader input =   new BufferedReader(new FileReader("top10gens.txt"));
				for (int i = 0; i < 10; i++) {	
					IDSAgent topAgent=new IDSAgent(IDSAgent.MINIMAX,"moho"+i);					
					
					String line=input.readLine();
					System.out.println("linea leida: "+line);
					gen = line.split(" ");
					params = new double[gen.length];
					for (int j = 0; j < gen.length; j++) {
						params[j] = Double.parseDouble(gen[j]);
					}
					topAgent.utility = new MaterialValue(params);
					agents_ids[num_agents-1-i] = topAgent;
				}
			}catch (Exception e) {
				System.out.println("Error al leer el archivo: "+e.getMessage());
			}
			
			/***
			 * Genera los 90 agentes restantes con valores randomizados en sus cromosomas
			 * (suponemos que num_agents = 100)
			 */
			for (int i = 0; i < num_agents-10; i++) {
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
			
			init_board = s.loadBoard();
			current_board = init_board.clone();
			//hace el torneo con los 100 agentes
			IDSAgent[] agents_ids_aux = agents_ids;
			//IDSAgent[] agents_ids_aux = s.runTournament(agents_ids);
			
			//guarda al mejor del torneo
			writeBestAgent(agents_ids_aux[0]);
			System.out.println("mejor agente guardado");
			IDSAgent[] mutados = new IDSAgent[10];
			
			//genera 10 nuevos agentes a partir de los 50 primeros
			for (int i = 0; i < 10; i++) {
				Random r = new Random();
				IDSAgent [] seleccionados = new IDSAgent[5];
				int pos = 0;
				//selecciona a los 5 agentes de manera aleatoria
				while(pos <5){
					int indice = r.nextInt(50);
					if(agents_ids_aux[indice]!=null){
						seleccionados[pos] = agents_ids_aux[indice];
						pos++;
						agents_ids_aux[indice]=null;
					}
				}

				System.out.println("agentes generados");
				IDSAgent newAgent=new IDSAgent(IDSAgent.MINIMAX,"mohoV2_"+i);
				pos = 0;
				r = new Random();
				double [] newValues = new double[5];
				//a partir del los seleccionados, elije los cromosomas para el nuevo agente
				while(pos <5){
					int indice = r.nextInt(5);
					if(seleccionados[indice]!=null){
						newValues[pos] = seleccionados[indice].utility.values[pos];
						pos++;
						seleccionados[indice]=null;
					}
				}
				newAgent.utility = new MaterialValue(newValues);
				mutados[i] = newAgent;	
				
			}
			System.out.println("guardando top 10 agentes");
			try{
				FileWriter fstream = new FileWriter("top10gens.txt");
				BufferedWriter out = new BufferedWriter(fstream);
				String st = "";
				for (int i = 0; i < mutados.length; i++) {
					for (int j = 0; j < mutados[i].utility.values.length; j++) {
						st = st+mutados[i].utility.values[j]+" ";						
					}
					st = st.substring(0, st.length()-1);
					st = st+"\n";
				}
				out.write(st);
				out.close();
				}catch (Exception e){
				System.err.println("Error: " + e.getMessage());
			}
			
			System.out.println("top 10 agentes guardados");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/***
			 * Al finalizar el torneo se tiene un ranking de todos
			 * De todos ellos se selecciona a los 50 mejores
			 * Se crean 10 nuevos agentes en donde cada uno de los cromosomas de estos agentes corresponde a un valor de un ganador,
			 * asi 1 nuevo agente es generado a partir de 5 ganadores (elejidos de manera aleatoria)
			 * Ej: cromosomaNuevoAgente = {cromGanador1[2], cromGanador2[5], cromGanador3[1], cromGanador4[1], cromGanador5[3] }
			 * Estos 10 nuevos agentes deben jugar nuevamente con una poblacion de 90 nuevos agentes randomizados, and so on...
			 */
		}
	}
	public static void writeBestAgent(IDSAgent agente) {
		// TODO Auto-generated method stub
		try{
			FileWriter fstream = new FileWriter("bestAgent.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			String s = "";
			for (int i = 0; i < agente.utility.values.length; i++) {
				s = s+agente.utility.values[i]+" ";
			}
			s = s.substring(0, s.length()-1);
			out.write(s);
			out.close();
			}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			}
		}

}