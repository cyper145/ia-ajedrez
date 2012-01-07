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
import java.util.ArrayList;

public class MaterialValue extends Heuristic
{
	public MaterialValue()
	{
	}
	
	/**
	    Takes a board and returns the heuristic value of the board
	**/
	public int evaluate(Board inb) 
	{
	   ArrayList<Coord> blackpieces=inb.getBlackPieces();
	   ArrayList<Coord> whitepieces=inb.getWhitePieces();
	   
	   int blacksum=0;
	   int whitesum=0;
	   
	   for (int i=0; i<blackpieces.size(); i++) 
	   {
		Coord current=(Coord)blackpieces.get(i);
		switch (inb.getPiece(current))
		{
		  case Board.BLACK_QUEEN:
		    blacksum+=9;
		    break;
		  case Board.BLACK_ROOK:
		    blacksum+=5;
		    break;
		  case Board.BLACK_PAWN:
		    blacksum+=1;
		    break;
		  case Board.BLACK_KNIGHT:
		    blacksum+=3;
		    break;
		  case Board.BLACK_BISHOP:
		    blacksum+=3;
		    break;  
		}
	   }
	   
	   for (int i=0; i<whitepieces.size(); i++) 
	   {
		Coord current=(Coord)whitepieces.get(i);
		switch (inb.getPiece(current))
		{
		  case Board.WHITE_QUEEN:
		    whitesum+=9;
		    break;
		  case Board.WHITE_ROOK:
		    whitesum+=5;
		    break;
		  case Board.WHITE_PAWN:
		    whitesum+=1;
		    break;
		  case Board.WHITE_KNIGHT:
		    whitesum+=3;
		    break;
		  case Board.WHITE_BISHOP:
		    whitesum+=3;
		    break;  
		}
	   }
	   return (whitesum-blacksum);
	}

}