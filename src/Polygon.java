import java.awt.Point;
import java.util.*;

public class Polygon {

	private String type;
	private String endLevel;
	private String cityID;
	private List<Location> coordinates;



	public Polygon(String type, String endLevel, String cityID, ArrayList<Location> co){
		this.type = type;
		this.endLevel = endLevel;
		this.cityID = cityID;
		this.coordinates = co;
		
		
		
		
//		for(int i = 1; i < loc.size(); i++){
//			Location one = loc.get(i-1);
//			Location two = loc.get(i);
//			Point p1 = one.asPoint(origin, scale);
//			Point p2 = two.asPoint(origin, scale);
//			g.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
//		}
	}

	public List<Location> getCo(){
		return this.coordinates;
	}

}