import java.util.*;

public class Segment {
	int roadID;
	double length;
	int node1;
	int node2;
	public ArrayList<Location> locations = new ArrayList<Location>();
	public Road road;


	public Segment(int id, double length, int node1, int node2, ArrayList<Double> co, Road rd){
		this.roadID = id;
		this.length = length;
		this.node1 = node1;
		this.node2 = node2;
		for(int i= 1; i < co.size(); i+=2){
			double lat = co.get(i-1);
			double lng = co.get(i);
			Location tempLocation = Location.newFromLatLon(lat, lng);
			locations.add(tempLocation);
		}
		road = rd;
		System.out.println("");
	}


	// getter and setter methods

	public int getId(){ return this.roadID; }
	public double getLength(){ return this.length; }
	public int getNode1(){ return this.node1; }
	public int getNode2(){ return this.node2; }
	public ArrayList<Location> getLocations(){ return this.locations; }
	

}
