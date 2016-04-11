
public class Restriction {
	public int nodeID1;
	public int roadID1;
	public int nodeID;
	public int roadID2;
	public int nodeID2;

	public Restriction(int nodeID1, int roadID1, int nodeID, int roadID2, int nodeID2){
		this.nodeID1 = nodeID1;
		this.roadID1 = roadID1;
		this.nodeID = nodeID;	
		this.roadID2 = roadID2;
		this.nodeID2 = nodeID2;
	}

	/**
	 * returns true if it is a restriction, or false if its okay
	 * @param from
	 * @param middle
	 * @param neighbour
	 * @return
	 */
	public boolean checkRestriction(Node from, Node middle, Node neighbour){

		if(this.nodeID1 == 0 || this.nodeID == 0 || this.nodeID2 == 0)
			return false;

		if(this.nodeID1 == from.getID() && this.nodeID == middle.getID() && this.nodeID2 == neighbour.getID() )
			return true;

		return false;

	}

}
