import java.util.AbstractQueue;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Set.*;
import java.util.Stack;

public class articulationPoints {

	Stack<stackStruct> stack = new Stack<stackStruct>();
	Set<Node> articulationPoints;
	public int numOfArtPoints;

	public articulationPoints(){

	}

	public void startSearch(){
		
		//	Initialise: for each node: node.depth ← Max Value, articulationPoints ← { }
		articulationPoints = new HashSet<Node>();
		for(Map.Entry<Integer, Node> entry : Application.nodes.entrySet()){
			entry.getValue().depth = Integer.MAX_VALUE;			
			entry.getValue().numSubtrees = 0;
		}

		//	start.depth ← 0, numSubtrees ← 0
		Node start = Application.getNode(10526);
		int numSubtrees = 0;
		// stackStruct startStruct = new stackStruct(start, 0, newStackStruct( null, 0, null);
		
		// for each neighbour of start
		if(start == null)
			throw new NullPointerException();
		
		for(Node neighbour : start.neighbours){

			//	if neighbour.depth = MAX_VALUE then:
			if(neighbour.depth == Integer.MAX_VALUE){
				iterArtPoints(neighbour, start); 
				numSubtrees++;
			}
		}

		//	if numSubtrees > 1 then add start to articulationPoints
		if(numSubtrees > 1)
			articulationPoints.add(start);

		numOfArtPoints = articulationPoints.size();
	}


	public void iterArtPoints(Node firstNode, Node root){
		// push (firstNode, 1, 〈root, 0, -〉〉 onto stack
		stack.push(new stackStruct(firstNode, 1, new stackStruct(root, 0, null)));

		// while stack not empty
		while(!stack.isEmpty()){

			// elem ← peek at stack, node ← elem.node
			stackStruct elem = stack.peek();
			Node node = elem.node;

			// if elem.children = null
			if(elem.children == null){

				// node.depth ← elem.depth, elem.reach ← elem.depth elem.children ← new queue
				node.depth = elem.depth;
				elem.reach = elem.depth;
				elem.children = new LinkedList<Node>();					

				// for each neighbour of node
				for(Node neigh : node.neighbours){

					// if neighbour ≠ elem.parent.node
					if(!neigh.equals(elem.parent.node)){

						// add neighbour to elem.children
						elem.children.add(neigh);
					}
				}

			}
			// else if elem.children not empty
			else if(!elem.children.isEmpty()){
				
				// child ← dequeue elem.children
				Node child = elem.children.poll();

					//if child.depth < MAX_VALUE then elem.reach ← min(elem.reach, child.depth)
					if(child.depth < Integer.MAX_VALUE){
						elem.reach = Math.min(elem.reach, child.depth);
					}
					// else push 〈child, node.depth+1, <elem, 0 - >〉 onto stack
					else{
						stack.push(new stackStruct(child, node.depth + 1, elem));
					}
				}
			
			else { 
				// if node ≠ firstNode
				if(node != firstNode){
					
					// if elem.reach ≥ elem.parent.depth then
					if(elem.reach >= elem.parent.depth){
						
						// add elem.parent.node to articulationPoints
						articulationPoints.add(elem.parent.node);
					}
					// elem.parent.reach = min (elem.parent.reach, elem.reach)
					
					elem.parent.reach = Math.min(elem.parent.reach, elem.reach);
				}
				stack.pop();
			}
		}
	}
}