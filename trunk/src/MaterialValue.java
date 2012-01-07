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

public class MaterialValue extends Heuristic
{
	double values[];
	
	public MaterialValue()
	{
		values = this.readKnowldege("knowledge.gen");
	}
	
	/**
	    Takes a board and returns the heuristic value of the board
	**/
	public double evaluate(Board inb) 
	{
	   ArrayList<Coord> blackpieces=inb.getBlackPieces();
	   ArrayList<Coord> whitepieces=inb.getWhitePieces();
	   
	   double blacksum=0;
	   double whitesum=0;
	   
	   for (int i=0; i<blackpieces.size(); i++) 
	   {
		Coord current=(Coord)blackpieces.get(i);
		switch (inb.getPiece(current))
		{
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
	   }
	   
	   for (int i=0; i<whitepieces.size(); i++) 
	   {
		Coord current=(Coord)whitepieces.get(i);
		switch (inb.getPiece(current))
		{
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
	   }
	   return (whitesum-blacksum);
	}

	@Override
	public double[] readKnowldege(String filename) {
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

	@Override
	public void writeKnowledge(String filename, double values[]) {
		try{
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			String s = "";
			for (int i = 0; i < values.length; i++) {
				s = s+values[i]+" ";
			}
			s = s.substring(0, s.length()-2);
			out.write(s);
			out.close();
			}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}	
			
	}

}