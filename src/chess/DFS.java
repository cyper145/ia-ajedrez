package chess;

import java.util.Stack;

import util.Node;

public class DFS {

	public static int time = 0;
	
	public static void DFS(Node n){
			for (int i = 0; i < n.getChildren().size(); i++) {
				if(n.visited==0){
					DFS.DFS_VISIT(n);
				}
				DFS.DFS(((Node)n.getChildren().get(i)));
			}
	}
	
	
	public static void DFS_VISIT(Node u){
		u.visited = 1;
		DFS.time++;
		u.d = DFS.time;
		for (int i = 0; i < u.getChildren().size(); i++) {
			if(((Node) u.getChildren().get(i)).visited==0){
				((Node) u.getChildren().get(i)).father = u.father;
				DFS.DFS_VISIT((Node) u.getChildren().get(i));					
			}
		}
		u.visited = 2;
		DFS.time++;
		u.f = DFS.time;
		
	}
	
}
