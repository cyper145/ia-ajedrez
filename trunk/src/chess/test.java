package chess;
/*
 * Author: Matthew Bardeen <me@mbardeen.net>, (C) 2008
 *
 * Copyright: See COPYING file that comes with this distribution
 *
*/

import java.io.*;
import java.util.Arrays;
import java.util.Random;
import util.Node;
import util.Tree;

public class test {
	Board b;
	public static int LEVEL_TREE = 4;
	
	public static void main(String[] args) {
		//test.testDFS();
		test.testMinMax();	
	}
	
	/**
	 * Test based at graph: http://upload.wikimedia.org/wikipedia/commons/6/6f/Minimax.svg
	 * 
	 */
	public static void testMinMax(){
		Tree t = new Tree();
		Node root =  new Node (0);
		t.setRootElement(root);
				
		Node n20 =  new Node (2);
		root.addChild(n20);
		Node n21 =  new Node (2);
		root.addChild(n21);
		Node n22 =  new Node (2);
		root.addChild(n22);
		Node n23 =  new Node (2);
		root.addChild(n23);
		
		/*Node n30 =  new Node (3);
		n20.addChild(n30);
		Node n31 =  new Node (3);
		n20.addChild(n31);
		Node n32 =  new Node (3);
		n21.addChild(n32);
		Node n33 =  new Node (3);
		n22.addChild(n33);
		Node n34 =  new Node (3);
		n22.addChild(n34);
		Node n35 =  new Node (3);
		n23.addChild(n35);*/
		
		Node n41 =  new Node (4);
		n41.value = 10;
		n20.addChild(n41);
		Node n42 =  new Node (4);
		n42.value = Integer.MAX_VALUE;
		n20.addChild(n42);
		Node n43 =  new Node (4);
		n43.value = 5;
		n20.addChild(n43);
		Node n44 =  new Node (4);
		n44.value = -10;
		n21.addChild(n44);
		Node n45 =  new Node (4);
		n45.value = 7;
		n22.addChild(n45);
		Node n46 =  new Node (4);
		n46.value = 5;
		n22.addChild(n46);
		Node n47 =  new Node (4);
		n47.value = Integer.MIN_VALUE;
		n22.addChild(n47);
		Node n48 =  new Node (4);
		n48.value = -7;
		n23.addChild(n48);
		Node n49 =  new Node (4);
		n49.value = -5;
		n23.addChild(n49);
		
		System.out.println(t.toString());
		System.out.println(MinMax.MinMax(root));
	}
	
	public static void testDFS(){
		Tree t = new Tree();
		Node root =  new Node (-1);
		t.setRootElement(root);
		for (int i = 0; i < 2; i++) {
			Node c = new Node(0);
			c.father = root;
			root.addChild(c);
			for (int j = 0; j < 2; j++) {
				Node d = new Node(1);
				c.addChild(d);
				d.father = c;
			}
		}		
		DFS.DFS(root);
		System.out.println(t.toString());
	}
	


}
