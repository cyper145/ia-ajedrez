package chess;

import util.Node;
import util.Tree;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Tree t = new Tree();
		Node root =  new Node (Math.random()*10);
		t.setRootElement(root);
		for (int i = 0; i < 10; i++) {
			 Node n =  new Node (Math.random()*10);
			 root.addChild(n);
		}
		System.out.println(t.toString());
	}

}
