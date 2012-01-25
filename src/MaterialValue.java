/***************************************************************************
 *   Copyright (C) 2009 by Matthew Bardeen   *
 *   mbardeen@utalca.cl   *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MaterialValue extends Heuristic
{
	
	public MaterialValue(String filename)
	{
		this.filename = filename;
		values = this.readKnowldege();
	}
	
	public MaterialValue(double peso_fichas[])
	{
		//this.filename = filename;
		//values = this.readKnowldege();
		this.values = peso_fichas;
	}
	
	/**
	    Takes a board and returns the heuristic value of the board
	**/
	public double evaluate(Board inb) 
	{
	   //System.out.println(Arrays.toString(values));
		
	   ArrayList<Coord> blackpieces=inb.getBlackPieces();
	   ArrayList<Coord> whitepieces=inb.getWhitePieces();
	   
	   double blacksum=0;
	   double whitesum=0;
	   
	   for (int i=0; i<blackpieces.size(); i++) {
		   Coord current=(Coord)blackpieces.get(i);
		   switch (inb.getPiece(current)){
		   	case Board.BLACK_QUEEN:
				blacksum+=values[4];
				break;
			case Board.BLACK_ROOK:
				blacksum+=values[3];
				break;
			case Board.BLACK_PAWN:
				blacksum+=values[0];
				break;
			case Board.BLACK_KNIGHT:
				blacksum+=values[2];
				break;
			case Board.BLACK_BISHOP:
				blacksum+=values[1];
				break;  
		   }
		 //si el peon esta adelantado ese peon vale doble
			if(inb.getPiece(current)== Board.BLACK_PAWN && current.getY() <= 5){
				blacksum+=values[0];
			}
			blacksum = evaluarCaballoNegro(current, inb, blacksum);
			
			
	   }
	   
	   for (int i=0; i<whitepieces.size(); i++) 
	   {
		   Coord current=(Coord)whitepieces.get(i);
		   switch (inb.getPiece(current)){
				case Board.WHITE_QUEEN:
					whitesum+=values[4];
				    break;
				case Board.WHITE_ROOK:
					whitesum+=values[3];
					break;
				case Board.WHITE_PAWN:
					whitesum+=values[0];
					break;
				case Board.WHITE_KNIGHT:
					whitesum+=values[2];
					break;
				case Board.WHITE_BISHOP:
					whitesum+=values[1];
					break;  
		   }
		   //si el peon esta adelantado ese peon vale doble
			if(inb.getPiece(current)== Board.WHITE_PAWN && current.getY() >= 5){
				whitesum+=values[0];
			}
			whitesum = evaluarCaballoBlanco(current, inb, whitesum);
	   }
	   
	  
	   //control del centro
	   for (int i = 0; i < 8; i++) {
		   for (int j = 2; j < 6; j++) {
			   if(inb.getPiece(new Coord(i, j)) > 1){
				   whitesum++;
			   }
			   else if(inb.getPiece(new Coord(i, j)) < 1){
				   blacksum++;
			   }
		   }
	   }
	   //torres en esquinas
	   
	   return (whitesum-blacksum);
	}

	/***
	 * Evalua si en la posicion hay un caballo blanco y calcula su valor material
	 * tomando en cuenta la posicion y los peones que tiene alrededor
	 * @param current coordenada actual a evaluar
	 * @param inb tablero que se esta jugando
	 * @param whitesum suma material de piezas blancas
	 * @return el nuevo whitesum 
	 */
	private double evaluarCaballoBlanco(Coord current, Board inb, double whitesum) {
		//tipos de posicion: caballo cerrado
		if(inb.getPiece(current)== Board.WHITE_KNIGHT ){
			//esta en un rincon
			if(current.getX()==0 && current.getY()==0){
				int numPieces = 0;
				if(inb.getPiece(new Coord(0,1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1,1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1,0)) == Board.WHITE_PAWN){
					numPieces++;
				}
				
				if(numPieces>0){
					whitesum+=values[2]*0.3;
				}
			}
			else if(current.getX()==0 && current.getY()==7){
				int numPieces = 0;
				if(inb.getPiece(new Coord(0,6)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1,6)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1,7)) == Board.WHITE_PAWN){
					numPieces++;
				}
				
				if(numPieces>0){
					whitesum+=values[2]*0.3;
				}
			}
			else if(current.getX()==7 && current.getY()==7){
				int numPieces = 0;
				if(inb.getPiece(new Coord(6,7)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(6,6)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(7,6)) == Board.WHITE_PAWN){
					numPieces++;
				}
				
				if(numPieces>0){
					whitesum+=values[2]*0.3;
				}
			}
			else if(current.getX()==7 && current.getY()==0){
				int numPieces = 0;
				if(inb.getPiece(new Coord(7,1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1,6)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(6,0)) == Board.WHITE_PAWN){
					numPieces++;
				}
				
				if(numPieces>0){
					whitesum+=values[2]*0.3;
				}
			}
			//esta en al menos uno de los bordes
			else if(current.getX()==7){
				int posY = current.getY();
				int numPieces = 0;
				
				if(inb.getPiece(new Coord(7, posY+1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(6, posY+1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(6, posY)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(6, posY-1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(7, posY-1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(numPieces>1){
					whitesum+=values[2]*0.3;
				}
			}
			else if(current.getX()==0){
				int posY = current.getY();
				int numPieces = 0;
				
				if(inb.getPiece(new Coord(0, posY+1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1, posY+1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1, posY)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1, posY-1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(0, posY-1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(numPieces>1){
					whitesum+=values[2]*0.3;
				}
			}
			else if(current.getY()==7){
				int posX = current.getX();
				int numPieces = 0;
				
				if(inb.getPiece(new Coord(posX+1, 7)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX+1, 6)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX, 6)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX-1, 6)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX-1, 7)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(numPieces>1){
					whitesum+=values[2]*0.3;
				}
			}
			else if(current.getY()==0){
				int posX = current.getX();
				int numPieces = 0;
				
				if(inb.getPiece(new Coord(posX+1, 0)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX+1, 1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX, 1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX-1, 1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX-1, 0)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(numPieces>1){
					whitesum+=values[2]*0.3;
				}
			}
			//no esta en ningun borde
			else{
				int posX = current.getX();
				int posY = current.getY();
				int numPieces = 0;
				
				if(inb.getPiece(new Coord(posX-1, posY-1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX-1, posY+1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX, posY+1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX, posY-1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				
				if(inb.getPiece(new Coord(posX+1, posY+1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX+1, posY-1)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX+1, posY)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX-1, posY)) == Board.WHITE_PAWN){
					numPieces++;
				}
				if(numPieces>3){
					whitesum+=values[2]*0.3;
				}
			}
		
		}	
		return whitesum;
	}

	/***
	 * Evalua si en la posicion hay un caballo negro y calcula su valor material
	 * tomando en cuenta la posicion y los peones que tiene alrededor
	 * @param current coordenada actual a evaluar
	 * @param inb tablero que se esta jugando
	 * @param blacksum suma material de piezas negras
	 * @return el nuevo blacksum 
	 */
	private double evaluarCaballoNegro(Coord current, Board inb, double blacksum) {
		// TODO Auto-generated method stub
		//tipos de posicion: caballo cerrado
		if(inb.getPiece(current)== Board.BLACK_KNIGHT ){
			//esta en un rincon
			if(current.getX()==0 && current.getY()==0){
				int numPieces = 0;
				if(inb.getPiece(new Coord(0,1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1,1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1,0)) == Board.BLACK_PAWN){
					numPieces++;
				}
				
				if(numPieces>0){
					blacksum+=values[2]*0.3;
				}
			}
			else if(current.getX()==0 && current.getY()==7){
				int numPieces = 0;
				if(inb.getPiece(new Coord(0,6)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1,6)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1,7)) == Board.BLACK_PAWN){
					numPieces++;
				}
				
				if(numPieces>0){
					blacksum+=values[2]*0.3;
				}
			}
			else if(current.getX()==7 && current.getY()==7){
				int numPieces = 0;
				if(inb.getPiece(new Coord(6,7)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(6,6)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(7,6)) == Board.BLACK_PAWN){
					numPieces++;
				}
				
				if(numPieces>0){
					blacksum+=values[2]*0.3;
				}
			}
			else if(current.getX()==7 && current.getY()==0){
				int numPieces = 0;
				if(inb.getPiece(new Coord(7,1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1,6)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(6,0)) == Board.BLACK_PAWN){
					numPieces++;
				}
				
				if(numPieces>0){
					blacksum+=values[2]*0.3;
				}
			}
			//esta en al menos uno de los bordes
			else if(current.getX()==7){
				int posY = current.getY();
				int numPieces = 0;
				
				if(inb.getPiece(new Coord(7, posY+1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(6, posY+1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(6, posY)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(6, posY-1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(7, posY-1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(numPieces>1){
					blacksum+=values[2]*0.3;
				}
			}
			else if(current.getX()==0){
				int posY = current.getY();
				int numPieces = 0;
				
				if(inb.getPiece(new Coord(0, posY+1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1, posY+1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1, posY)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(1, posY-1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(0, posY-1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(numPieces>1){
					blacksum+=values[2]*0.3;
				}
			}
			else if(current.getY()==7){
				int posX = current.getX();
				int numPieces = 0;
				
				if(inb.getPiece(new Coord(posX+1, 7)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX+1, 6)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX, 6)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX-1, 6)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX-1, 7)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(numPieces>1){
					blacksum+=values[2]*0.3;
				}
			}
			else if(current.getY()==0){
				int posX = current.getX();
				int numPieces = 0;
				
				if(inb.getPiece(new Coord(posX+1, 0)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX+1, 1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX, 1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX-1, 1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX-1, 0)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(numPieces>1){
					blacksum+=values[2]*0.3;
				}
			}
			//no esta en ningun borde
			else{
				int posX = current.getX();
				int posY = current.getY();
				int numPieces = 0;
				
				if(inb.getPiece(new Coord(posX-1, posY-1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX-1, posY+1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX, posY+1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX, posY-1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				
				if(inb.getPiece(new Coord(posX+1, posY+1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX+1, posY-1)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX+1, posY)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(inb.getPiece(new Coord(posX-1, posY)) == Board.BLACK_PAWN){
					numPieces++;
				}
				if(numPieces>3){
					blacksum+=values[2]*0.3;
				}
			}
	
		}
		return blacksum;
	}

	@Override
	public void randomizeGen(){
		Random r = new Random();
		int genPos = r.nextInt(values.length);
		int genSign = r.nextInt(2);
		if(genSign==0)
			values[genPos] += values[genPos]*0.1;
		else{
			values[genPos] -= values[genPos]*0.1;			
		}
	}
	@Override
	public double[] readKnowldege() {
		String gen[];
		double params[] = null;
		try {
			
			BufferedReader input =   new BufferedReader(new FileReader(this.filename));
			String line=input.readLine();
			gen = line.split(" ");
			params = new double[gen.length];
			for (int i = 0; i < gen.length; i++) {
				params[i] = Double.parseDouble(gen[i]);
			}
		} catch (Exception e) {}
		
		return params;
	}

	@Override 
	public void writeKnowledge(double values[]) {
		try{
			FileWriter fstream = new FileWriter(this.filename);
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

	@Override
	public void writeKnowledgeBackup(double[] values) {
		// TODO Auto-generated method stub
		try{
			FileWriter fstream = new FileWriter(this.filename+"Backup");
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

	@Override
	public void restoreKnowledgeBackup(double[] values2) {
		// TODO Auto-generated method stub
		String gen[];
		double params[] = null;
		try {
			
			BufferedReader input =   new BufferedReader(new FileReader(this.filename+"Backup"));
			String line=input.readLine();
			gen = line.split(" ");
			params = new double[gen.length];
			for (int i = 0; i < gen.length; i++) {
				params[i] = Double.parseDouble(gen[i]);
			}
		} catch (Exception e) {}
		
		this.writeKnowledge(params);
	}

}