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
public abstract class Heuristic
{
	public double values[];
	public String filename;
	/**
	    Takes a board and returns the heuristic value of the board
	**/
	public abstract double evaluate(Board b);
	
	/***
	 * Lee el archivo que contiene el gen del agente
	 * @return un arreglo que representa el valor material del gen
	 */
	public abstract double[] readKnowldege();
	
	/***
	 * Escribe el archivo que contiene el gen del agente
	 * @param values arreglo que representa el valor material del gen
	 */
	public abstract void writeKnowledge(double values[]);
	
	/***
	 * Genera valores aleatorios para el gen, lo ideal es aplicarlo antes de que empieze a jugar
	 * para probar el desempeño de la nueva modificacion
	 */
	public abstract void randomizeGen();

	public abstract void writeKnowledgeBackup(double[] values);

	public abstract void restoreKnowledgeBackup(double[] values);
	
}