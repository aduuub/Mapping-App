import java.util.*;

public class Road {

	private int RoadID;
	private int type;
	private String label;
	private String city;
	private int oneway;
	private int speed;
	private int roadClass;
	private int notForCar;
	private int notForPde;
	private int notForBicy;
	private Set<Segment> segments = new HashSet<Segment>();
	
	public Road(int RoadID, int type, String label, String city, int oneway, int speed, int roadClass, int notForCar,int notForPde, int notForBicy ){
		this.RoadID = RoadID;
		this.type = type;
		this.label = label;
		this.city = city;
		this.oneway = oneway;
		this.speed = speed;
		this.roadClass = roadClass;
		this.notForCar = notForCar;
		this.notForPde = notForPde;
		this.notForBicy = notForBicy;
	}
	
	
	public void addSeg(Segment s){ segments.add(s); }
	public int getID(){ return this.RoadID; }
	public String getLabel() { return this.label; }
	public Set<Segment> getSeg() { return this.segments; }
	
}
