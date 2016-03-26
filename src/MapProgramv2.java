import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.JTextField;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * This is a small example class to demonstrate extending the GUI class and
 * implementing the abstract methods. I
 * 
 * @author Adam Wareing
 * @ID 300 337 630
 */
public class MapProgramv2 extends GUI {
	private static final int NUM_SQUARES = 10;
	private static final int SQUARE_SIZE = 30;


	private Map<Integer, Node> nodes = new HashMap<Integer, Node>();
	private Map<Integer, Road> roads = new HashMap<Integer, Road>();
	private ArrayList<Segment> segments = new ArrayList<Segment>();
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();
	private ArrayList<Set<Segment>> selectedSeg = new ArrayList<Set<Segment>>(); // roads to highlight

	private double scale = 20; // default zoom
	private Trie trie; 
	private Location origin = Location.newFromLatLon(-36.847622 , 174.763444); // centre of Auckland location

	public MapProgramv2() {
	}

	@Override
	protected void redraw(Graphics g) {
		drawPolygons(g);
		drawNodes(g);
		drawSeg(g);
	}

	public void drawPolygons(Graphics g){
		for(Polygon p : polygons){
			List<Location> locations = p.getCo();
			int[] x = new int[locations.size()];
			int[] y = new int[locations.size()];
			int countOfArraySize = 0;

			for(Location l : locations){
				Point pointOfLoc = l.asPoint(origin , scale);
				x[countOfArraySize] = (int) pointOfLoc.getX();
				y[countOfArraySize] = (int) pointOfLoc.getY();
				countOfArraySize++;
			}
			g.fillPolygon(x,y,locations.size());
		}

	}

	public void drawSeg(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.blue);

		for (Segment s : segments) { 

			ArrayList<Location> segLoc = s.getLocations();
			for(int i = 1; i < segLoc.size(); i++){
				Location one = segLoc.get(i-1);
				Location two = segLoc.get(i);
				Point p1 = one.asPoint(origin, scale);
				Point p2 = two.asPoint(origin, scale);
				g2.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
			}
		}

		// draws the selected segments in red and a thicker line

		g2.setColor(Color.red);
		g2.setStroke(new BasicStroke(5));

		for (Set<Segment> setOfSeg : selectedSeg) {
			for(Segment s : setOfSeg){
				ArrayList<Location> segLoc = s.getLocations();
				for(int i = 1; i < segLoc.size(); i++){
					Location one = segLoc.get(i-1);
					Location two = segLoc.get(i);
					Point p1 = one.asPoint(origin, scale);
					Point p2 = two.asPoint(origin, scale);
					g2.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
				}
			}
		}


	}


	public void drawNodes(Graphics g){
		g.setColor(Color.black);

		for (Map.Entry<Integer, Node> entry : nodes.entrySet()) { // draws the nodes
			Node node = entry.getValue();		
			Location nodeLocation = node.getLoc();
			Point point = nodeLocation.asPoint(origin, scale);
			g.drawOval((int) point.getX(), (int) point.getY(), 2, 2);

		}		
	}

	@Override
	protected void onScroll(MouseWheelEvent e) {
		int notches = e.getWheelRotation();	
		if(notches < 0)
			onMove(GUI.Move.ZOOM_OUT);
		else
			onMove(GUI.Move.ZOOM_IN);
	}

	@Override
	protected void onClick(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		double closestDistance = 99999999;
		Node closestNode = null;
		Point p = new Point(x,y);
		Location c = new Location(0,0);
		Location click = c.newFromPoint(p, origin, scale);

		for (Map.Entry<Integer, Node> entry : nodes.entrySet()) {  
			Node tempNode = entry.getValue();
			Location tempNodeLoc = tempNode.getLoc(); 	
			if(tempNodeLoc.distance(click) < closestDistance){
				closestDistance = tempNodeLoc.distance(click);
				closestNode = tempNode;				
			}
		}

		if(closestNode == null){
			getTextOutputArea().setText("No intersections found"); 
			return;
		}

		Set<Segment> segments = closestNode.getSeg(); // gets segments related to that node
		Set<Integer> roadIDs = new HashSet<Integer>(); // for the road id's related to that segment
		String printText = "Connecting roads are: "; // text that will display connecting roads
		for(Segment s : segments)
			roadIDs.add(s.getId()); // fills the set roadIDs with the relevant road ID


		Set<String> labels = new HashSet<String>();
		for (Map.Entry<Integer, Road> entry : roads.entrySet() ) {  // checks for matching roadID
			if(roadIDs.contains(entry.getKey() )){
				labels.add(entry.getValue().getLabel());
				printText += entry.getValue().getLabel() + ",  ";
			}
		}
		printText = printText.substring(0, printText.length() - 3);

		getTextOutputArea().setText(printText);

	}
	/** Called on key press to load the potential roads
	 * @return String[] of road names matching input text
	 */

	@Override
	protected String[] onSearch() {
		selectedSeg.clear();
		String inputText = getSearchBox();
		System.out.println(inputText);
		String[] results =  trie.getWord(inputText);	
		ArrayList<String> selectedRoadName = new ArrayList<String>();

		for(int i = 0; i < results.length; i ++) // adds from array to array list
			selectedRoadName.add(results[i]);

		for(Map.Entry<Integer, Road> entry : roads.entrySet()){
			if(selectedRoadName.contains(entry.getValue().getLabel() ))
				selectedSeg.add(entry.getValue().getSeg());
		}
		return results;
	}


	@Override
	protected void onMove(Move m) {
		double shift = 20 / scale; // adjusts how far it moves when clicked, depends on scale how much it will move

		switch(m){
		case NORTH: origin = origin.moveBy(0, shift); break; 
		case SOUTH: origin = origin.moveBy(0, -shift); break; 
		case EAST: origin = origin.moveBy(shift, 0); break; 
		case WEST: origin = origin.moveBy(-shift, 0); break; 
		case ZOOM_IN: scale = scale * 1.5 ; break;  // used to calculate how much to move depending on the zoom level
		case ZOOM_OUT: scale = scale / 1.5; break; 
		}
		if(scale == 0){scale = 5;} // so there is no divide by zero errors

	}
	@Override
	protected void onLoad(File nodesFile, File roadsFile, File segmentsFile, File polygonsFile)  {

		// Loading the nodes
		BufferedReader data;
		try {
			String line = null;
			data = new BufferedReader(new FileReader(nodesFile));

			while ((line = data.readLine()) != null) {
				String[] values = line.split("\t");
				int nodeID = Integer.parseInt(values[0]);
				double lat = Double.parseDouble(values[1]);
				double lng = Double.parseDouble(values[2]);
				Node node = new Node(nodeID, lat, lng);
				this.nodes.put(nodeID, node);

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}


		// Loading the roads
		try {
			String line = null;
			data = new BufferedReader(new FileReader(roadsFile));
			line = data.readLine(); //skips the first line with file names

			while ((line = data.readLine()) != null) {

				String[] values = line.split("\t");
				int roadID = (int) Integer.parseInt(values[0]);
				int type = Integer.parseInt(values[1]);
				String label = values[2];
				String city = values[3];
				int oneWay = Integer.parseInt(values[4]);
				int speed = Integer.parseInt(values[5]);
				int roadClass = Integer.parseInt(values[6]);
				int notForCar = Integer.parseInt(values[7]);
				int notForPde = Integer.parseInt(values[8]);
				int notForBicy = Integer.parseInt(values[9]);
				Road road = new Road(roadID, type, label, city, oneWay, speed, roadClass, notForCar, notForPde, notForBicy );
				roads.put(roadID, road);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}


		// Loading the segments
		try {
			String line = null;
			data = new BufferedReader(new FileReader(segmentsFile));
			line = data.readLine(); //skips the first line with file names

			while ((line = data.readLine()) != null) {

				String[] values = line.split("\t");
				int roadID = Integer.parseInt(values[0]);
				double length = Double.parseDouble(values[1]);
				int nodeOne = Integer.parseInt(values[2]);
				int nodeTwo = Integer.parseInt(values[3]);
				ArrayList<Double> co = new ArrayList<Double>(); // co-ordinates 
				int count = 4;

				while(count < values.length){ // adds all the co-ordinates along the road
					co.add( (Double) Double.parseDouble(values[count] ));
					count++;
				}
				Segment newSegment = new Segment(roadID, length, nodeOne, nodeTwo, co );
				segments.add(newSegment); // creates and adds the new segment		

				for (Map.Entry<Integer, Road> entry : roads.entrySet()) {  // checks for matching roadID and adds the segment to the corisponding road
					if(entry.getKey() == roadID)
						entry.getValue().addSeg(newSegment);
				}

				for (Map.Entry<Integer, Node> entry : nodes.entrySet()) {   // adds the segments to the appropriate nodes
					if(entry.getKey() == nodeOne) // checks for matching Node 1
						entry.getValue().addSeg(newSegment);
					if(entry.getKey() == nodeTwo) // checks for matching Node 2
						entry.getValue().addSeg(newSegment);
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}

		loadTrie();
		loadPolygons(polygonsFile);

	}


	public void loadPolygons(File polygonsFile){
		try {
			String line = null;
			BufferedReader data = new BufferedReader(new FileReader(polygonsFile));

			while ((line = data.readLine()) != null) {
				ArrayList<Location> coordinates = new ArrayList<Location>();
				data.readLine(); //skips the first line "[POLYGON]"
				String type = data.readLine();
				String endLevel = data.readLine();
				Scanner scanner = new Scanner(new File(endLevel));
				String label = "Label";
				if(scanner.next().startsWith(label))
					data.readLine();
				scanner.close();						
				String cityID = data.readLine();
				String polygonData = data.readLine();
				Scanner scan = new Scanner(new File(polygonData));
				while(scan.hasNext()){
					double lat = scan.nextInt();
					double lng = scan.nextInt();
					Location loc = Location.newFromLatLon(lat, lng);
					coordinates.add(loc);
				}
				data.readLine();
				polygons.add(new Polygon(type, endLevel, cityID, coordinates)); // creates and adds the new polygon						
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}	

	}

	public void loadTrie(){
		trie = new Trie(); // global variable declared in header
		for (Map.Entry<Integer, Road> entry : roads.entrySet()) {
			String roadName = entry.getValue().getLabel();
			trie.addWord(roadName);
		}
	}

	public static void main(String[] args) {
		new MapProgramv2();
	}
}
