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
	}

	public List<Location> getCo(){
		return this.coordinates;
	}
	public String getType(){
		return this.type;
	}

}