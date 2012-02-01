import java.io.*;
import java.util.Random;


public class TestAgent {
	
	
	/*
	 * Ejecución: java TestAgent board.tbl "3.69 5.15 1.16 6.91 7.2"
	 */
	public static void main(String[] args) {
		Server s = new Server(12);
		IDSAgent[] agents = new IDSAgent[2];
		s.current_board = s.loadBoard(args[0]);
		System.out.println("Playing board");
		System.out.println(s.current_board);
		
		
		IDSAgent idsagent1=new IDSAgent(IDSAgent.MINIMAX,"Yaoming");					
		idsagent1.utility = new MaterialValue(new double[]{6.27,4.485,1.935,7.22,7.835});
		agents[0] = idsagent1;
		
		
		IDSAgent idsagent2=new IDSAgent(IDSAgent.MINIMAX,"Random");		
		double cromosomas[] = new double[args[1].split(" ").length];
		for (int i = 0; i < args[1].split(" ").length; i++) {
			cromosomas[i]= Double.parseDouble(args[1].split(" ")[i]);
		}
		idsagent2.utility = new MaterialValue(cromosomas);
		agents[1] = idsagent2;
		
		
		s.runGame(agents[0], agents[1]);
		System.out.println("Resultado");
		for (int i = 0; i < agents.length; i++) {
			System.out.println(agents[i].name+" wins:"+agents[i].wins);
		}
		
	}
}
