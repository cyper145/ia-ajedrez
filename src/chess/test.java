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
