import java.awt.*;
import java.util.List;
import java.util.*;
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
public class Application extends GUI {

	public static Map<Integer, Node> nodes = new HashMap<Integer, Node>();
	private static Map<Integer, Road> roads = new HashMap<Integer, Road>();
	public static List<Segment> segments = new ArrayList<Segment>();
	private List<Polygon> polygons = new ArrayList<Polygon>();
	private List<Set<Segment>> selectedSeg = new ArrayList<Set<Segment>>(); // roads to highlight from search
	public static Set<Segment> selectedPath = new HashSet<Segment>(); // roads to highlight for path finding
	public static Map<Integer, Restriction> restrictions = new HashMap<Integer, Restriction>();


	private Trie trie; // structure used for the search algorithm
	private searchPath searchPath; // structure used for finding the path
	private articulationPoints artPoints; // used for finding the articulation points
	private boolean displayArticulationPoints = false;

	private double scale = 20; // default zoom	
	private boolean awaitingClick = false; // used for map directions
	private Node transitOne; // first click node
	private Node transitTwo; // seconds click node
	private Location origin = Location.newFromLatLon(-36.847622 , 174.763444); // default centre of Auckland location

	public Application() {

	}

	/** 
	 * Redraws the graphics, called every time a move was made
	 */
	@Override
	protected void redraw(Graphics g1D) {
		Graphics2D g = (Graphics2D) g1D;
		drawPolygons(g);
		drawSeg(g);
		// drawNodes(g); // not required but code runs as expected.
		drawPathSegments(g);
		drawSearchSegments(g);
		if(displayArticulationPoints)
			drawArtPoints(g);
	}

	/**
	 * Draws all the articulation points
	 */
	public void drawArtPoints(Graphics g) {
		g.setColor(Color.red);

		if(displayArticulationPoints){

			for(Node node : artPoints.articulationPoints){
				Location nodeLocation = node.getLoc();
				Point point = nodeLocation.asPoint(origin, scale);
				g.drawOval((int) point.getX(), (int) point.getY(), 2, 2);
			}			
		}
	}



	/** 
	 * Draws the polygons which includes lakes, oceans, parks, reserves etc.
	 * @param g Graphics
	 */
	public void drawPolygons(Graphics2D g){
		for(Polygon p : polygons){
			g.setColor(Color.DARK_GRAY);
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

			String typeOfPolygon = p.getType();
			Color col = null;
			switch(typeOfPolygon){
			case "Type=0x28": col = new Color(174,209,255); break; //water
			case "Type=0x16" : col = new Color(202,223,170); break; // forest				
			case "Type=0x17" : col = new Color(202,223,170); break; // reserve	
			case "Type=0xe" : col = new Color(211,202,189); break; // airstrip
			case "Type=0x7" : col = new Color(223,219,212); break;  // terminal
			case "Type=0x18" : col = new Color(202,223,170); break;  // golf course 
			case "Type=0x14" : col = new Color(232,221,129); break;  // national park	
			case "Type=0x15" : col = new Color(232,221,129); break;  // national park
			case "Type=0x3c" : col = new Color(174,209,255); break;  // lake 
			case "Type=0x3d" : col = new Color(174,209,255); break;  // lake 
			case "Type=0x3e" : col = new Color(174,209,255); break;  // lake 
			case "Type=0x29" : col = new Color(174,209,255); break; // lake 
			case "Type=0x32" : col = new Color(174,209,255); break;  // lake 
			default :  col = new Color(202,223,170); break;
			}
			g.setColor(col);	
			g.fillPolygon(x,y,locations.size());
		}
	}

	/** 
	 * Draws each individual segment of the road.
	 * @param g
	 */
	public void drawSeg(Graphics2D g){
		g.setColor(Color.blue);
		float size = (float) (scale / 100.0);
		g.setStroke(new BasicStroke(size));

		for (Segment s : segments) { 

			ArrayList<Location> segLoc = s.getLocations();
			for(int i = 1; i < segLoc.size(); i++){

				Location one = segLoc.get(i-1);
				Location two = segLoc.get(i);
				Point p1 = one.asPoint(origin, scale);
				Point p2 = two.asPoint(origin, scale);

				switch(s.road.getType()){

				case 22 : g.setColor(new Color(213,212,200)); break; // walkway
				case 6 : g.setColor(new Color(255,255,255)); break; // road
				case 1 : g.setColor(new Color(255,255,104)); break; // motorway
				case 9 : g.setColor(new Color(255,255,104)); break; // motorway
				default: g.setColor(new Color(255,255,255)); break;

				}

				g.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());

			}
		}
	} 

	/** 
	 * Draws each individual segment of the roads that have been searched in a highlighted colour
	 * @param g
	 */	
	public void drawSearchSegments(Graphics2D g){
		g.setColor(Color.red);
		g.setStroke(new BasicStroke(3));
		for (Set<Segment> setOfSeg : selectedSeg) {
			for(Segment s1 : setOfSeg){
				ArrayList<Location> segLoc1 = s1.getLocations();
				for(int i = 1; i < segLoc1.size(); i++){
					Location one = segLoc1.get(i-1);
					Location two = segLoc1.get(i);
					Point p1 = one.asPoint(origin, scale);
					Point p2 = two.asPoint(origin, scale);
					g.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
				} 
			} 
		}
	}

	/** 
	 * Draws each individual segment of the roads involved on the selected path to a destination in a highlighted colour.
	 * @param g
	 */		
	public void drawPathSegments(Graphics2D g){
		g.setColor(Color.red);
		g.setStroke(new BasicStroke(3));
		for (Segment s : selectedPath) {
			ArrayList<Location> segLoc = s.getLocations();
			for(int i = 1; i < segLoc.size(); i++){
				Location one = segLoc.get(i-1);
				Location two = segLoc.get(i);
				Point p1 = one.asPoint(origin, scale);
				Point p2 = two.asPoint(origin, scale);
				g.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
			} 
		}
	}


	/** 
	 * Draws all of the nodes (road intersections)
	 * Not required to be drawn, as the roads naturally form the intersections anyway.
	 * @param g
	 */	
	public void drawNodes(Graphics2D g){
		g.setColor(Color.blue);

		for (Map.Entry<Integer, Node> entry : nodes.entrySet()) { 
			Node node = entry.getValue();		
			Location nodeLocation = node.getLoc();
			Point point = nodeLocation.asPoint(origin, scale);
			g.fillRect((int) point.getX(), (int) point.getY(), 3, 3);
		}		
		g.setColor(Color.red);

		if(segments.size() == 0)
			return;			
	}

	/** 
	 * Abstract method declared in GUI.
	 * Called on the event of a scroll, delegates the work to the onMove method.
	 * @param e
	 */
	@Override
	protected void onScroll(MouseWheelEvent e) {
		int notches = e.getWheelRotation();	
		if(notches < 0)
			onMove(GUI.Move.ZOOM_OUT);
		else
			onMove(GUI.Move.ZOOM_IN);
	}

	/** 
	 * Abstract method declared in GUI.
	 * Called on the event of a click, delegates the work to the onMove method.
	 * @param e
	 */
	@Override
	protected void onClick(MouseEvent e) {

		double closestDistance = Double.MAX_VALUE;
		Node closestNode = null;
		Location click = Location.newFromPoint(new Point(e.getX() , e.getY()), origin, scale);

		for (Map.Entry<Integer, Node> entry : nodes.entrySet()) {  
			Node tempNode = entry.getValue();
			Location tempNodeLoc = tempNode.getLoc(); 	
			if(tempNodeLoc.distance(click) < closestDistance){ // if its closer from the current node compared to the current shortest
				closestDistance = tempNodeLoc.distance(click);
				closestNode = tempNode;				
			}
		}

		if(closestNode == null) 
			getTextOutputArea().setText("No intersections found. Please try again."); 

		if(awaitingClick) // if in navigation mode
			transit(closestNode);
		else // otherwise show connecting roads
			connectedRoads(closestNode);
	}


	/** 
	 * Finds the all articulation points
	 */
	@Override
	public void findArticulationPoints(){
		if(artPoints == null){
			artPoints = new articulationPoints();
			artPoints.startSearch();
			getTextOutputArea().append("Currently displaying the articulation points.");
		}

		displayArticulationPoints = !displayArticulationPoints;
		redraw();
	}

	/** 
	 * Used for the transit between the two nodes selected 
	 * @param closestNode
	 */
	public void transit(Node closestNode){

		if(transitOne == null && transitTwo == null){
			transitOne = closestNode;
			getTextOutputArea().setText("Start Point is: " + closestNode.getID());
		}
		else if(transitOne != null && transitTwo == null){
			transitTwo = closestNode;
			getTextOutputArea().setText("End Point is: " + closestNode.getID());
			calculateShortestPath();

			String pathTaken = "";
			double totalLength = 0;

			Set<Integer> roadIdsOnPath = new HashSet<Integer>();

			for(Segment s : selectedPath){

				Road road = getRoad(s.roadID);

				if(!roadIdsOnPath.contains(road.getID())) // prevents duplicates
					pathTaken += "\n" + road.getLabel() + " ( " +  s.length*1000 + " m )";

				roadIdsOnPath.add(road.getID());
				totalLength += s.length;
			}

			// getTextOutputArea().setText("The shortest path with length " + totalLength + " km is:");
			getTextOutputArea().append(pathTaken);

		}

	}

	/** 
	 * Displays the connecting codes from the param.
	 * @param closestNode
	 */
	public void connectedRoads(Node closestNode){
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

	/** 
	 * Calculates the shortest from one intersection to another
	 */
	public void calculateShortestPath() {
		double dist = -2; // shortest path using the roads
		double direct = -2; // direct path from first click to second click

		if(transitOne != null && transitTwo != null){
			dist = searchPath.calculateDistance(transitOne, transitTwo);
			direct = transitOne.getLoc().distance(transitTwo.getLoc());

			getTextOutputArea().append("\nThe shortest path is " + dist + " km.");
			getTextOutputArea().append("\nIn a direct line the path is " + direct + " km.");
		}
	}

	/** 
	 * Called on key press to search for matching/ predicted roads
	 * @return String[] of road names matching input text
	 */
	@Override
	protected String[] onSearch() {
		selectedSeg.clear();
		String inputText = getSearchBox();
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


	/**
	 *  Called when the user wants to shift the GUI 
	 *  @param m : NORTH, SOUTH, EAST, WEST, ZOOM_IN, ZOOM_OUT
	 */
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

	/**
	 * Used to toggle whether the user wants transit instructions or not
	 */
	@Override
	protected void directions(){
		transitOne = transitTwo = null;
		if(awaitingClick){
			selectedPath.clear();
			getTextOutputArea().setText("");
		}

		this.awaitingClick = !this.awaitingClick;
	}

	/** 
	 * Loads the files (nodes, roads, segments, polygons)
	 */
	@Override
	protected void onLoad(File nodesFile, File roadsFile, File segmentsFile, File polygonsFile, File restrictionsFile)  {

		loadNodes(nodesFile);
		loadRoads(roadsFile);
		loadSegments(segmentsFile);
		loadTrie();
		loadPolygons(polygonsFile);
		loadRestrictions(restrictionsFile);
		addNodeNeighbours();

		getTextOutputArea().append("Preparing to index the path finding algorithm \n"); 
		searchPath = new searchPath(nodes);
		getTextOutputArea().append("Path finding indexed successfully \n"); 

		getTextOutputArea().setText("Sucessfully loaded \n"); 


	}

	/**
	 * Loads the road intersections
	 * @param nodesFile
	 */
	public void loadNodes(File nodesFile){
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
				Application.nodes.put(nodeID, node);

			}
			data.close();
			getTextOutputArea().append("Loaded intersections sucesssfully \n"); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * Loads the main roads (these are not drawn on the GUI- thats the road segments).
	 * @param roadsFile
	 */
	public void loadRoads(File roadsFile){
		getTextOutputArea().append("Loading roads \n"); 
		try {
			String line = null;
			BufferedReader data = new BufferedReader(new FileReader(roadsFile));
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
			data.close();
			getTextOutputArea().append("Loaded roads sucesssfully \n"); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * Loads the road segments
	 * @param segmentsFile
	 */
	public void loadSegments(File segmentsFile){
		try {
			getTextOutputArea().append("Loading road segments \n"); 
			String line = null;
			BufferedReader data = new BufferedReader(new FileReader(segmentsFile));
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

				Road rd = null;
				for (Map.Entry<Integer, Road> entry : roads.entrySet()) {  // checks for matching roadID and adds the segment to the corisponding road
					if(entry.getKey() == roadID){
						rd = entry.getValue();
						break;
					}
				}

				Segment newSegment = new Segment(roadID, length, nodeOne, nodeTwo, co, rd);
				rd.addSeg(newSegment);
				segments.add(newSegment); // creates and adds the new segment		

				for (Map.Entry<Integer, Road> entry : roads.entrySet()) {  // checks for matching roadID and adds the segment to the corisponding road
					if(entry.getKey() == roadID){
						entry.getValue().addSeg(newSegment);
						break;
					}
				}

				int addedBoth = 0;
				for (Map.Entry<Integer, Node> entry : nodes.entrySet()) {   // adds the segments to the appropriate nodes
					if(entry.getKey() == nodeOne){ // checks for matching Node 1
						entry.getValue().addSeg(newSegment);
						addedBoth++;
					}
					if(entry.getKey() == nodeTwo){ // checks for matching Node 2
						entry.getValue().addSeg(newSegment);
						addedBoth++;
					}
					if(addedBoth == 2)
						break;
				}

			}
			data.close();
			getTextOutputArea().append("Loaded segments sucesssfully\n"); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
	}



/** 
 * Loads the polygon data (lakes, rivers, reserves, parks etc.)
 * @param polygonsFile
 */
public void loadPolygons(File polygonsFile){
	getTextOutputArea().append("Loading objects \n"); 
	try {
		@SuppressWarnings("unused")
		String line = null;
		BufferedReader data = new BufferedReader(new FileReader(polygonsFile));

		while ((line = data.readLine()) != null) {
			String endLevel = "";
			String label = "";
			String cityID = "";
			String polygonData = "";
			boolean hasData = false;

			ArrayList<Location> coordinates = new ArrayList<Location>();

			String type = data.readLine();
			if(!type.startsWith("Type=")){
				label = type;
				type = "";
			}

			label = data.readLine();
			if(!label.startsWith("Label=")){
				endLevel = label;
				label = "";
			}

			endLevel = data.readLine();
			if(!endLevel.startsWith("EndLevel=")){
				cityID = endLevel;
				endLevel = "";

			}
			cityID = data.readLine();
			if(!cityID.startsWith("CityIdx")){
				polygonData = cityID;
				cityID = "";
			}
			else{
				polygonData = data.readLine();
			}

			if(polygonData.startsWith("Data"))
				hasData = true;

			if(hasData){

				polygonData = polygonData.substring(6, polygonData.length()-1);  // removes the "Data="
				String[] co = polygonData.split(","); 
				for(int i = 0; i < co.length; i++){
					if(co[i].startsWith("(")) 
						co[i] = co[i].substring(1, co[i].length()-1); // removes the brackets
					else if(co[i].endsWith(")"))
						co[i] = co[i].substring(0, co[i].length()-2); // removes the brackets
				}

				for(int i = 1; i < co.length; i+=2){ // gets the co ordinates as doubles from co
					double one = Double.parseDouble(co[i-1]);
					double two = Double.parseDouble(co[i]);
					Location l = Location.newFromLatLon(one, two);
					coordinates.add(l);
				}
			}

			String end = data.readLine();
			while(true){ // skips [end]
				if(end.compareTo("[END]") == 0)
					break;
				end = data.readLine();
			}	

			data.readLine(); // skips whitespace
			polygons.add(new Polygon(type, endLevel, cityID, coordinates)); // creates and adds the new polygon						
		}
		data.close();
		getTextOutputArea().append("Loaded objects sucesssfully \n"); 
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch(Exception e){
		e.printStackTrace();
	}	

}

/**
 * Adds references to the all nodes in the sets neighbours
 */	
public void addNodeNeighbours(){
	for(Map.Entry<Integer, Node> entry : nodes.entrySet()){
		Node node = entry.getValue();
		for(Segment s : node.getSeg()){
			int otherNodeID  = s.getOtherNodeId(node.getID());
			Node neighNode = getNode(otherNodeID);
			node.neighbours.add(neighNode);
		}	
	}

}

/**
 * Indexes the search
 */
public void loadTrie(){
	getTextOutputArea().append("Loading search\n"); 
	trie = new Trie(); // global variable declared in header
	for (Map.Entry<Integer, Road> entry : roads.entrySet()) {
		String roadName = entry.getValue().getLabel();
		trie.addWord(roadName);
	}
	getTextOutputArea().append("Search loaded sucessfully \n"); 

}


/**
 * Indexes the restrictions
 */
public void loadRestrictions(File restrictionsFile){
	getTextOutputArea().append("Loading restrictions\n"); 
	if(restrictionsFile == null)
		return;
	try{
		Scanner scan = new Scanner(restrictionsFile);
		scan.nextLine();
		while(scan.hasNext()){
			int nodeID1 = scan.nextInt();
			int roadID1  = scan.nextInt();
			int nodeID = scan.nextInt();
			int roadID2 = scan.nextInt();
			int nodeID2 = scan.nextInt();		
			restrictions.put(nodeID, new Restriction(nodeID1, roadID1, nodeID, roadID2, nodeID2));
		}
		scan.close();
	}catch(IOException e){
		getTextOutputArea().append("Error reading the restrictions file");
		return;
	}

	getTextOutputArea().append("Search loaded sucessfully \n"); 

}

/**
 * @param id
 * @return the corresponding road to the id
 */
public static Road getRoad(int id){		
	return roads.get(id);			
}

/**
 * @param id
 * @return the corresponding Node to the id
 */
public static Node getNode(int id){		
	return nodes.get(id);
}


public static void main(String[] args) {
	new Application();
}
}
