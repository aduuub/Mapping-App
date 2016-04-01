import java.util.*;
public class searchPath {

	public Set<Node> nodes = new HashSet<Node>();

	public searchPath(Map<Integer, Node> oldNodes){
		for(Node n : oldNodes.values())
			nodes.add(n);	
		initilize();
	}


	/** 
	 * Initializes all nodes to be ready for a path search
	 */
	public void initilize(){
		for(Node n : nodes){ 
			n.visited = false;
			n.pathFrom = null;
		}
	}

	/** 
	 * Calculates the shortest path between two different nodes using A* search
	 * @param start
	 * @param goal
	 */
	public double calculateDistance(Node start, Node goal){
		// declare priority queue (fringe) of nodes (by total cost to goal)
		PriorityQueue fringe = new PriorityQueue<queueStruct>();		
		// add initial node (start, null, 0, estimate(start,goal)
		queueStruct initial = new queueStruct(start, (Node) null, (double)0, estimate(start, goal));
		fringe.add(initial);

		//while fringe != null
		while(!fringe.isEmpty()){
			// dequeue 1st one
			queueStruct queueStruct = (queueStruct) fringe.poll();
			Node node = queueStruct.node;
			if(!node.visited){
				// node.visited ←true, node.pathFrom←from, node.cost←costToHere
				node.visited = true;
				node.pathFrom = queueStruct.from;
				node.cost = queueStruct.costToHere;
				// if node = goal then exit			
				if(node.equals(goal))
					return node.cost;
				//for each edge to neigh out of node
				for(Segment s : node.getSeg()){
					int otherNodeID  = s.getOtherNodeId(node.getID());
					Node neighbour  = MapProgramv2.getNode(otherNodeID);
					// if not neigh.visited then
					if(!neighbour.visited){
						// costToNeigh ← costToHere + edge.weight
						double costToNeigh = queueStruct.costToHere + s.length;
						// estTotal ← costToNeigh + estimate(neighbour, goal )
						double estTotal = costToNeigh + estimate(neighbour, goal);
						// fringe.enqueue(Neighbor, node, costToNeigh, estTotal)
						fringe.add(new queueStruct(neighbour, node, costToNeigh, estTotal));
					}
					
				}

			}
		}

		return -1;
	}






	/** 
	 * Returns the linear distance between two nodes
	 * @param start
	 * @param goal
	 * @return linear distance between the two nodes
	 */
	public double estimate(Node start, Node goal){
		if(start == null || goal == null)
			throw new NullPointerException();
		return start.getLoc().distance(goal.getLoc());
	}





}
