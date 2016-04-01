import java.util.*;

public class Node {

	private int id;
	private double x;
	private double y;
	
	public boolean visited;
	public Node pathFrom;
	public double cost;
	
	private Location location;
	private Set<Segment> segments = new HashSet<Segment>();
	
	public Node(int id, double x, double y){
		this.id = id;
		this.x = x;
		this.y = y;
		this.pathFrom = null;
		this.visited = false;
		this.cost = 0;
		location = new Location(x, y);
		location = location.newFromLatLon(x, y);	
		
	}
	
	public int getID(){ return this.id; }
	public double getX(){return this.x;}
	public double getY(){return this.y;}
	public Location getLoc(){return this.location;}
	public void setLoc(Location l){this.location = l;}
	public void addSeg(Segment s){ segments.add(s); }
	public Set<Segment> getSeg(){ return this.segments ; }

	
}
