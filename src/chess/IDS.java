package chess;

import util.Node;

public class IDS {
	// public static int time = 0;
	//
	// BFS(grafo G, nodo_fuente s) 
	// { 
	//    for u ∈ V[G] do
	//    {
	//       estado[u] = NO_VISITADO;
	//       distancia[u] = INFINITO; /* distancia infinita si el nodo no es alcanzable */
	//       padre[u] = NULL;
	//    }
	//    estado[s] = VISITADO;
	//    distancia[s] = 0;
	//    Encolar(Q, s);
	//    while !vacia(Q) do
	//    {  u = extraer(Q);
	//       for  v ∈ adyacencia[u]  do
	//       {
	//          if estado[v] == NO_VISITADO then
	//          {
	//               estado[v] = VISITADO;
	//               distancia[v] = distancia[u] + 1;
	//               padre[v] = u;
	//               Encolar(Q, v);
	//          }
	//       }
	//    }
	// }
	
	public static int IDS(Node<Board> n){
		
		for (int i = 0; i < n.getChildren().size(); i++) {
			
		}
		
		Board b = (Board) n.data;
		Move[] moves=b.getValidMoves();
	
		for (Move m:moves) {
			Board b_child = b.clone();
			b_child.makeMove(m);
			Node n_new =  new Node (b_child);
			n_new.father = n;
			if(b_child.isCheckMate()){
				if(b_child.turn== Board.TURNWHITE){
					System.out.println("Movimiento: "+m.toString());
					return 1;
					
				}else{
					System.out.println("Movimiento: "+m.toString());
					return -1;
				}
			}
		}
		for (Move m:moves) {
			Board b_child = b.clone();
			b_child.makeMove(m);
			Node n_new =  new Node (b_child);
			int valor = IDS(n_new);
			if(n_new.father==null && valor==1)
				System.out.println("gano "+m.toString());
		}
		return 0;
	}
	
	
	
}
