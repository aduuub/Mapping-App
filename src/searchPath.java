import java.util.*;
public class searchPath {

	public static Set<Node> nodes;

	public searchPath(Map<Integer, Node> oldNodes){
		nodes = new HashSet<Node>();

		for(Node n : oldNodes.values())
			nodes.add(n);	
		
		initilize();
	}


	/** 
	 * Initializes all nodes to be ready for a path search
	 */
	public static void initilize(){

		for(Node n : nodes){ 
			n.visited = false;
			n.pathFrom = null;
		}
	}

	/** 
	 * Calculates the shortest path between two different nodes using A* search
	 * @param start
	 * @param goal
	 * @return shortest distance
	 */
	public double calculateDistance(Node start, Node goal){

		initilize();
		
		// declare priority queue (fringe) of nodes (by total cost to goal)
		PriorityQueue<queueStruct> fringe = new PriorityQueue<queueStruct>();	

		// add initial node (start, null, 0, estimate(start,goal)
		queueStruct initial = new queueStruct(start, null, 0.0, estimate(start, goal), null, null);
		fringe.add(initial);

		//while fringe != null
		while(!fringe.isEmpty()){

			// dequeue 1st one
			queueStruct queueStruct = (queueStruct) fringe.poll();

			Node node = queueStruct.node;
			queueStruct structFrom = queueStruct;
			Node lastNodeFrom = queueStruct.from;
			structFrom.node = lastNodeFrom;


			if(!node.visited){

				// node.visited ←true, node.pathFrom←from, node.cost←costToHere
				node.visited = true;
				node.pathFrom = queueStruct.from;
				node.cost = queueStruct.costToHere;

				// if node = goal then exit			
				if(node.equals(goal)){
					pathTaken(queueStruct);
					return node.cost;
				}

				//for each edge to neigh out of node
				for(Segment s : node.getSeg()){
					int otherNodeID  = s.getOtherNodeId(node.getID());
					Node neighbour  = Application.getNode(otherNodeID);
					Road segmentRoad = Application.getRoad(s.getId());
					boolean oneWayRoad = segmentRoad.oneway == 1 ? true : false;



					// if not neigh.visited then
					if(!neighbour.visited){

						if((oneWayRoad && s.node1 == otherNodeID) || !oneWayRoad ){ // is the segment one way?

							// costToNeigh ← costToHere + edge.weight
							double costToNeigh = queueStruct.costToHere + s.length;

							// estTotal ← costToNeigh + estimate(neighbour, goal)
							double estTotal = costToNeigh + estimate(neighbour, goal);

							// fringe.enqueue(Neighbor, node, costToNeigh, estTotal)

							fringe.add(new queueStruct(neighbour, node, costToNeigh, estTotal, structFrom , s));
						}
					} 
				}
			}
		}
		
		return -1; // no path was found
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

	/** 
	 * Returns the path taken
	 * @param start
	 * @param goal
	 * @return linear distance between the two nodes
	 */
	public void pathTaken(queueStruct struct){
		Application.selectedPath = new HashSet<Segment>();		

		while(struct != null){
			if(struct.segmentFrom != null)
				Application.selectedPath.add(struct.segmentFrom);
			struct = struct.structFrom;
		}
	}





}
